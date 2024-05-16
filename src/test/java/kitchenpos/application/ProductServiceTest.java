package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;

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
}