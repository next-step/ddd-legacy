package kitchenpos.application;

import kitchenpos.application.fixture.ProductTestFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    ProductService productService;
    ProductTestFixture productTestFixture;
    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;

    @BeforeEach
    void setup() {
        this.productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        this.productTestFixture = new ProductTestFixture();
    }

    @DisplayName("정상동작")
    @Test
    void createOk() {
        Product product = productTestFixture.createProduct("test", BigDecimal.valueOf(10000L));
        given(productRepository.save(any())).willReturn(product);
        Product result = productService.create(product);

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getPrice()).isEqualTo(BigDecimal.valueOf(10000L));
    }

    @Nested
    @DisplayName("제품 신규 생성 시")
    class Product_create {
        @DisplayName("가격이 입력되어야하며, 0 이상이 아니면 예외를 반환한다.")
        @Test
        void createPrice() {
            Product product = productTestFixture.createProduct(BigDecimal.valueOf(-1L));

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("제품 가격 오류");
        }

        @DisplayName("제품명은 입력되어야하며, 비속어가 포함되면 예외를 반환한다.")
        @Test
        void createName() {
            Product product = productTestFixture.createProduct("shit");
            given(purgomalumClient.containsProfanity(any())).willReturn(true);

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("제품명 오류");

        }
    }

    @Nested
    @DisplayName("가격 변경시")
    class Product_Price_change {
        @DisplayName("변경 가격이 0이상이 아니면 예외를 반환한다.")
        @Test
        void changePrice() {
            Product product1 = productTestFixture.createProduct(BigDecimal.valueOf(100));
            Product product2 = productTestFixture.createProduct(BigDecimal.valueOf(-1));

            assertThatThrownBy(() -> productService.changePrice(product1.getId(), product2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("가격 변경 오류");

        }

        @DisplayName("상품가격*수량을 한 합이 메뉴 가격보다 작은 경우 메뉴를 숨긴다.")
        @Test
        void hide() {
            Product product1 = productTestFixture.createProduct(BigDecimal.valueOf(100));
            Product product2 = productTestFixture.createProduct(BigDecimal.valueOf(1000));

            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product1);
            menuProduct.setQuantity(1L);

            Menu menu = new Menu();
            menu.setPrice(new BigDecimal(10000));
            menu.setMenuProducts(List.of(menuProduct));
            menu.setDisplayed(true);

            given(productRepository.findById(any())).willReturn(Optional.of(product2));
            given(menuRepository.findAllByProductId(any())).willReturn(List.of(menu));

            Product productResult = productService.changePrice(product1.getId(), product2);
            assertThat(menu.isDisplayed()).isFalse();

        }
    }


}
