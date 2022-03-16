package kitchenpos.unit;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.unit.fixture.MenuFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kitchenpos.unit.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private PurgomalumClient purgomalumClient;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록한다")
    @Test
    void create() {
        // given
        when(purgomalumClient.containsProfanity(탕수육.getName())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(탕수육);

        // when
        Product saveProduct = productService.create(탕수육);

        // then
        assertThat(saveProduct.getName()).isEqualTo(탕수육.getName());
        assertThat(saveProduct.getPrice()).isEqualTo(탕수육.getPrice());
    }

    @DisplayName("상품의 이름이 null인 경우 등록되지 않는다")
    @ParameterizedTest
    @NullSource
    void createInvalidName(String name) {
        assertThatThrownBy(() -> productService.create(createProduct(name, BigDecimal.valueOf(5000))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격이 null인 경우 등록되지 않는다")
    @ParameterizedTest
    @NullSource
    void createInvalidPrice(BigDecimal price) {
        assertThatThrownBy(() -> productService.create(createProduct("탕수육", price)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 수정한다")
    @Test
    void changePrice() {
        // given
        BigDecimal changePrice = 탕수육.getPrice().add(BigDecimal.valueOf(1000));
        List<Menu> menus = new ArrayList<>();
        menus.add(MenuFixture.한그릇_세트);
        menus.add(MenuFixture.두그릇_세트);
        menus.add(MenuFixture.세그릇_세트);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(탕수육));
        when(menuRepository.findAllByProductId(PRODUCT_ID)).thenReturn(menus);

        // when
        탕수육.setPrice(changePrice);
        Product changeProduct = productService.changePrice(PRODUCT_ID, 탕수육);

        // then
        assertThat(changeProduct.getPrice()).isEqualTo(changePrice);
    }

    @DisplayName("상품의 가격은 0원 이상이어야 한다")
    @Test
    void changePriceZero() {
        // given
        Product product = createProduct("탕수육", BigDecimal.valueOf(-1));

        // when
        // then
        assertThatThrownBy(() -> productService.changePrice(PRODUCT_ID, product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 상품 목록 가격의 합보다 크면 메뉴를 비공개로 변경한다")
    @Test
    void changePriceBiggerThanProductsAmount() {
        // given
        List<Menu> menus = new ArrayList<>();
        menus.add(MenuFixture.한그릇_세트);
        menus.add(MenuFixture.두그릇_세트);
        menus.add(MenuFixture.세그릇_세트);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(탕수육));
        when(menuRepository.findAllByProductId(PRODUCT_ID)).thenReturn(menus);

        // when
        탕수육.setPrice(BigDecimal.ZERO);
        productService.changePrice(PRODUCT_ID, 탕수육);

        // then
        Assertions.assertThat(MenuFixture.한그릇_세트.isDisplayed()).isFalse();
        Assertions.assertThat(MenuFixture.두그릇_세트.isDisplayed()).isFalse();
        Assertions.assertThat(MenuFixture.세그릇_세트.isDisplayed()).isFalse();
    }
}