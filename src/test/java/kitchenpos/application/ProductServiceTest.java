package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.PurgomalumClient;
import kitchenpos.utils.fixture.ProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ProductServiceTest {
    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        final Product product = ProductFixture.상품();
        final Product expected = 상품등록(product);

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
        final Product product = ProductFixture.상품();
        product.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 상품등록(product));
    }

    @DisplayName("상품의 이름은 빈 값이거나 비속어가 아니어야한다.")
    @ValueSource(strings = "욕설")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        final Product product = ProductFixture.상품();
        product.setName(name);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 상품등록(product));
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final Product saved = productRepository.save(ProductFixture.상품());
        final Product request = new Product();
        request.setPrice(BigDecimal.valueOf(3_000L));

        final Product expected = 상품가격수정(saved.getId(), request);

        assertAll(
                () -> assertThat(expected.getId()).isEqualTo(saved.getId()),
                () -> assertThat(expected.getName()).isEqualTo(saved.getName()),
                () -> assertThat(expected.getPrice()).isEqualTo(BigDecimal.valueOf(3_000L))
        );
    }

    @DisplayName("상품의 가격은 0원 이상이어야한다.")
    @ValueSource(strings = "-1000")
    @NullSource
    @ParameterizedTest
    void changePrice(BigDecimal price) {
        final Product saved = productRepository.save(ProductFixture.상품());
        final Product request = new Product();
        request.setPrice(price);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> 상품가격수정(saved.getId(), request));
    }

    @DisplayName("상품을 전체 조회한다.")
    @Test
    void findAll() {
        final Product saved1 = productRepository.save(ProductFixture.상품());
        final Product saved2 = productRepository.save(ProductFixture.상품());

        List<Product> expected = 상품전체조회();

        assertThat(expected).containsOnly(saved1, saved2);
    }

    private Product 상품등록(final Product product) {
        return productService.create(product);
    }

    private Product 상품가격수정(final UUID productId, final Product product) {
        return productService.changePrice(productId, product);
    }

    private List<Product> 상품전체조회() {
        return productService.findAll();
    }
}
