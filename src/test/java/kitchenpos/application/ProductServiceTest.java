package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.exception.ProductNameException;
import kitchenpos.exception.ProductPriceException;
import kitchenpos.inMemory.InMemoryMenuRepository;
import kitchenpos.inMemory.InMemoryProductRepository;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class ProductServiceTest {

    private ProductRepository productRepository = new InMemoryProductRepository();

    private MenuRepository menuRepository = new InMemoryMenuRepository();

    private ProfanityClient purgomalumClient = new FakeProfanityClient();

    private ProductService productService;

    @BeforeEach
    void setUp() throws Exception {
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품은 반드시 고유 ID, 상품명, 가격을 갖는다.")
    @Test
    void create() {

        // given
        final Product product = createProductRequest(23_000);

        // when
        final Product actual = productService.create(product);

        // then
        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("족발(중)"),
                () -> assertThat(actual.getPrice()).isEqualTo(BigDecimal.valueOf(23_000L))
        );
    }

    @DisplayName("상품 가격은 0원 이상이다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -100, -1000})
    void create(final int price) {

        // given
        final Product product = createProductRequest(price);

        // when, then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> productService.create(product))
                .isInstanceOf(ProductPriceException.class);
    }

    @DisplayName("비속어 사이트에 등록된 단어로 상품명을 사용할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"욕설", "비속어가 포함된 단어"})
    void create(final String name) {

        // given
        final Product product = createProductRequest(name, 23_000);

        assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(ProductNameException.class);
    }

    @DisplayName("등록한 모든 상품을 볼 수 있다.")
    @Test
    void finalAll() {
        productRepository.save(createProduct("족발(소)", 18_000));
        productRepository.save(createProduct("족발(중)", 23_000));

        final List<Product> actual = productService.findAll();
        assertThat(actual).hasSize(2);
    }

    @DisplayName("등록한 상품의 가격을 0원 이상으로 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"21000", "24000"})
    void changePrice(BigDecimal price) {

        // given
        final Product product = createProductRequest(23_000);
        final Product actualProduct = productService.create(product);

        // when
        actualProduct.setPrice(price);

        // then
        final Product changedProduct = productService.changePrice(actualProduct.getId(), actualProduct);
        assertThat(changedProduct.getPrice()).isEqualTo(price);
    }

    private Product createProductRequest(final int price) {
        return createProductRequest("족발(중)", price);
    }

    static Product createProductRequest(final String name, final int price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    static Product createProduct(final String name, final int price) {
        return createProduct(UUID.randomUUID(), name, BigDecimal.valueOf(price));
    }

    static Product createProduct(final UUID id, final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }


}
