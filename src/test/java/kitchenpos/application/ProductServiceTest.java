package kitchenpos.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Null;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    PurgomalumClient purgomalumClient;
    @InjectMocks
    ProductService productService;
    private Product product;

    @DisplayName("상품등록 가격체크")
    @Test
    public void 상품등록_가격체크() throws Exception {
        product = ProductFixture.create(BigDecimal.valueOf(-10000));

        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("상품등록 이름체크")
    @ParameterizedTest
    @NullSource
    @Test
    public void 상품등록_이름_필수값체크(String name) throws Exception {
        product = ProductFixture.create(name);

        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("상품등록 이름 욕설체크")
    @Test
    public void 상품등록_이름_욕설체크() throws Exception {
        product = ProductFixture.create("욕설");
        when(purgomalumClient.containsProfanity(any())).thenReturn(true);

        assertThatThrownBy(() -> productService.create(product)).isInstanceOf(
            IllegalArgumentException.class);
    }

    @DisplayName("상품등록 성공")
    @Test
    public void 상품등록_성공() throws Exception {
        product = ProductFixture.create("정상 상품");
        when(purgomalumClient.containsProfanity(any())).thenReturn(false);
        when(productRepository.save(any())).thenReturn(product);

        Product savedProduct = productService.create(product);

        assertThat(product.getName()).isEqualTo(savedProduct.getName());
    }

    @DisplayName("상품가격 변경 상품가격 체크")
    @Test
    public void 상품가격변경_0원이상체크() throws Exception {
        product = ProductFixture.create("상품", BigDecimal.valueOf(10000));
        product.setPrice(BigDecimal.valueOf(-10000));
        assertThatThrownBy(()->productService.changePrice(product.getId(),product))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품가격 변경 시 메뉴가격이 구성상품 가격의 합보다 작으면 비활성화")
    @Test
    void 삼풍가격변경_메뉴가격비교_비활성화() {
        Product product_10000 = ProductFixture.create(BigDecimal.valueOf(10000));
        Product product_20000 = ProductFixture.create(BigDecimal.valueOf(20000));
        Menu menu = MenuFixture.create(
            BigDecimal.valueOf(30000)
            , MenuProductFixture.createDefaultsWithProduct(product_10000, product_20000));
        when(productRepository.findById(product_10000.getId())).thenReturn(Optional.of(product_10000));
        when(menuRepository.findAllByProductId(any())).thenReturn(List.of(menu));
        assertThat(menu.isDisplayed()).isTrue();

        product_10000.setPrice(BigDecimal.valueOf(1500));
        productService.changePrice(product_10000.getId(), product_10000);

        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("상품가격 변경 성공")
    @Test
    public void 상품가격변경_성공() throws Exception {
        product = ProductFixture.create("상품", BigDecimal.valueOf(10000));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        product.setPrice(BigDecimal.valueOf(20000));

        Product changeProduct = productService.changePrice(product.getId(), product);

        assertThat(product.getPrice()).isEqualTo(changeProduct.getPrice());
    }
}