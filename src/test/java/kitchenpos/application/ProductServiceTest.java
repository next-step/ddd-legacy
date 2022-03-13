package kitchenpos.application;

import kitchenpos.application.stub.MenuRepositoryStub;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.KitchenposFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private MenuRepository menuRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        menuRepository = new MenuRepositoryStub();
        productService = new ProductService(productRepository, menuRepository, null);

        product = new Product();
    }

    @DisplayName("상품의 가격이 입력되지 않으면 상품을 등록할 수 없다.")
    @ParameterizedTest
    @NullSource
    void emptyPrice(BigDecimal price) {
        product.setPrice(price);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격은 0 이하일 수 없다.")
    @Test
    void negativeInteger() {
        product.setPrice(BigDecimal.valueOf(-1));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 이름이 입력되지 않으면 상품을 등록할 수 없다.")
    @ParameterizedTest
    @NullSource
    void emptyName(String name) {
        product.setName(name);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 이름에 나쁜말이 포함되면 상품을 등록할 수 없다.")
    @Test
    void badName() {
        product.setName("fuck");
        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("생성된 상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        product.setId(UUID.randomUUID());
        product.setName("치킨");
        product.setPrice(BigDecimal.valueOf(10000));

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(15000));

        Product actual = productService.changePrice(product.getId(), request);

        assertThat(actual.getPrice()).isEqualTo(request.getPrice());
    }

    @DisplayName("상품의 가격을 변경했을 때 변경된 상품을 포함한 메뉴의 가격이 메뉴의 모든 상품의 가격을 더한 가격보다 작으면 메뉴를 전시하지 않는다.")
    @Test
    void changePriceWithMenu() {
        // 상품 생성
        Product chicken = chickenProduct();

        // 메뉴 생성
        Menu menu = KitchenposFixture.menu(menuGroup(), chicken, pastaProduct());
        menuRepository.save(menu);

        given(productRepository.findById(any())).willReturn(Optional.of(chicken));

        // when
        chicken.setPrice(BigDecimal.valueOf(15000));
        productService.changePrice(chicken.getId(), chicken);

        // then
        menu = menuRepository.findById(menu.getId()).get();
        assertThat(menu.isDisplayed()).isFalse();
    }
}
