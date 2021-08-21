package kitchenpos.application;

import kitchenpos.domain.Menu;
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
import java.util.List;
import java.util.UUID;

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
        product.setName("상품 이름");
        product.setPrice(BigDecimal.valueOf(1000));
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
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
        product.setPrice(price);

        assertThatThrownBy(() -> 상품등록(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 이름은 빈 값이거나 비속어가 아니어야한다.")
    @ValueSource(strings = "욕설")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        product.setName(name);

        assertThatThrownBy(() -> 상품등록(product))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final Product saved = 상품등록(product);
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
        final Product saved = 상품등록(product);
        final Product request = new Product();
        request.setPrice(price);

        assertThatThrownBy(() -> 상품가격수정(saved.getId(), request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 때 상품에 속한 메뉴의 가격이 메뉴 상품 가격의 총합과 다를 경우 메뉴를 노출하지 않는다.")
    @Test
    void changePrice_Menu() {
        final Menu menu = new Menu();
    }

    @Test
    void findAll() {
        final Product other = new Product();
        other.setName("다른 상품");
        other.setPrice(BigDecimal.valueOf(3000));
        final Product saved1 = 상품등록(product);
        final Product saved2 = 상품등록(other);

        List<Product> expected = 상품전체조회();

        assertThat(expected).containsOnly(saved1, saved2);
    }

    Product 상품등록(final Product product) {
        return productService.create(product);
    }

    Product 상품가격수정(final UUID productId, final Product product) {
        return productService.changePrice(productId, product);
    }

    List<Product> 상품전체조회() {
        return productService.findAll();
    }
}
