package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceTest {
    private ProductService productService;
    private ProductRepository productRepository = new InMemoryProductRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private Product product;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        product = new Product();
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(1000));

        final Product expected = productService.create(product);

        assertThat(expected).isNotNull();
        assertAll(
                () -> assertThat(expected.getId()).isNotNull(),
                () -> assertThat(expected.getName()).isEqualTo(product.getName()),
                () -> assertThat(expected.getPrice()).isEqualTo(product.getPrice())
        );
    }

    @DisplayName("상품의 가격은 빈 값이 아니어야하고, 0원 이상이어야한다.")
    @ValueSource(strings = "-1000")
    @NullSource
    @ParameterizedTest
    void create(BigDecimal price) {
        product.setName("상품 이름");
        product.setPrice(price);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 이름은 빈 값이거나 비속어가 아니어야한다.")
    @ValueSource(strings = "욕설")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(1000));

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
