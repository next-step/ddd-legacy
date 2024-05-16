package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest {

    private ProductRepository productRepository = new InMemoryProductRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @ParameterizedTest
    @DisplayName("가격이 null일 경우 상품 생성 시 IllegalArgumentException이 발생한다.")
    @NullSource
    void create_fail_for_price_null(BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 음수일 경우 상품 생성 시 IllegalArgumentException이 발생한다.")
    void create_fail_for_price_negative() {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-1L));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("이름이 null일 경우 상품 생성 시 IllegalArgumentException이 발생한다.")
    void create_fail_for_name_null(String name) {
        Product product = new Product();
        product.setName(name);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("비속어가 포함된 이름일 경우 상품 생성 시 IllegalArgumentException이 발생한다.")
    void create_fail_for_contains_profanity() {
        Product product = new Product();
        product.setName(FakePurgomalumClient.PROFANITY);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품이 정상적으로 생성된다.")
    void create_success() {
        // given
        Product product = new Product();
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(1000L));

        // when
        Product savedProduct = productService.create(product);

        // then
        assertThat(savedProduct.getId()).isNotNull();
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("가격이 null일 경우 상품 가격 변경 시 IllegalArgumentException이 발생한다.")
    void changePrice_fail_for_price_null(BigDecimal price) {
        // given
        Product product = new Product();
        product.setPrice(price);
        product.setId(UUID.randomUUID());

        productRepository.save(product);

        // when, then
        assertThatThrownBy(() -> productService.changePrice(product.getId(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("가격이 음수일 경우 상품 가격 변경 시 IllegalArgumentException이 발생한다.")
    void changePrice_fail_for_price_negative() {
        // given
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(-1L));
        product.setId(UUID.randomUUID());

        productRepository.save(product);

        // when, then
        assertThatThrownBy(() -> productService.changePrice(product.getId(), product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품이 존재하지 않는데 상품 가격 변경 시 IllegalArgumentException이 발생한다.")
    void changePrice_fail_for_product_not_found() {
        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), new Product()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품 가격이 정상적으로 변경된다.")
    void changePrice_success() {
        // given
        Product product = new Product();
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(1000L));

        Product savedProduct = productRepository.save(product);

        // when
        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(2000L));

        Product updatedProduct = productService.changePrice(savedProduct.getId(), request);

        // then
        assertThat(updatedProduct.getPrice()).isEqualTo(request.getPrice());
    }

    @Test
    @DisplayName("상품 가격 변경 후 메뉴 상품들의 가격 합이 메뉴 가격보다 작을 경우 메뉴의 display를 false로 변경한다.")
    void changePrice_menu_display_false_for() {
        // given
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(1000L));

        Product savedProduct = productRepository.save(product);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("메뉴 이름");
        menu.setPrice(BigDecimal.valueOf(3000L));
        menu.setDisplayed(true);

        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(savedProduct.getId());
        menuProduct.setProduct(savedProduct);
        menuProduct.setQuantity(1L);

        menu.setMenuProducts(List.of(menuProduct));

        menuRepository.save(menu);

        Product request = new Product();
        request.setPrice(BigDecimal.valueOf(2000L));

        // when
        productService.changePrice(savedProduct.getId(), request);

        // then
        assertThat(menu.isDisplayed()).isFalse();
    }
}