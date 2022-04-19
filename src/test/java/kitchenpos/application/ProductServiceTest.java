package kitchenpos.application;

import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.exception.EmptyOrProfanityNameException;
import kitchenpos.exception.PriceLessThanZeroException;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryProductRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("[상품]")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private final ProductRepository productRepository = new InMemoryProductRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final ProfanityClient profanityClient = new FakeProfanityClient();

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, menuRepository, profanityClient);
    }

    @ValueSource(strings = {"-1", "-100"})
    @ParameterizedTest
    @DisplayName("상품의 가격은 음수가 될 수 없다.")
    void productPriceLessThanZeroTest(final BigDecimal price) {
        Product product = createProductRequest(price);

        AssertionsForClassTypes.assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(PriceLessThanZeroException.class);
    }

    @Test
    @DisplayName("상품의 이름에는 비속어가 포함되면 안된다.")
    void productNameNotProfanity() {
        Product product = createProduct("욕설", 18000);

        AssertionsForClassTypes.assertThatThrownBy(() -> productService.create(product))
                .isInstanceOf(EmptyOrProfanityNameException.class);

    }

    @Test
    @DisplayName("상품이 정상적으로 등록된다.")
    void create() {
        final Product request = createProductRequest(18000);

        final Product actual = productService.create(request);

        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(request.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(request.getPrice())
        );
    }

    @Test
    @DisplayName("가격을 변경하고자 하는 Product가 없다면 NoSuchElementException 발생")
    public void existProductTest() {
        UUID uuid = UUID.randomUUID();
        productRepository.save(createProduct(uuid, "후라이드", BigDecimal.valueOf(18_000L)));
        Product changeProduct = createProduct(uuid, "후라이드", BigDecimal.valueOf(19_000L));

        Product actual = productService.changePrice(changeProduct.getId(), changeProduct);

        Assertions.assertAll(
                () -> assertThat(actual.getId()).isEqualTo(changeProduct.getId()),
                () -> assertThat(actual.getPrice()).isEqualTo(changeProduct.getPrice())
        );
    }

    @ValueSource(strings = {"-1", "-100"})
    @ParameterizedTest
    @DisplayName("상품 변경시 가격은 0보다 크거나 같아야한다.")
    void changePriceLessThanZeroTest(final BigDecimal price) {
        Product product = createProduct("후라이드", price);

        AssertionsForClassTypes.assertThatThrownBy(() -> productService.changePrice(product.getId(), product))
                .isInstanceOf(PriceLessThanZeroException.class);
    }


    @Test
    @DisplayName("상품의 전체 목록 조회")
    void searchProductAllTest() {

        productRepository.save(createProduct("후라이드", 18000));
        productRepository.save(createProduct("양념치킨", 20000));
        productRepository.save(createProduct("간장치킨", 19000));
        List<Product> actual = productRepository.findAll();
        assertThat(actual).hasSize(3);
    }

    private Product createProductRequest(final int price) {
        return createProductRequest("후라이드", BigDecimal.valueOf(price));
    }

    private Product createProductRequest(final BigDecimal price) {
        return createProductRequest("후라이드", price);
    }

    private Product createProductRequest(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private Product createProduct(final String name, final BigDecimal price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    private Product createProduct(final String name, final int price) {
        return createProduct(UUID.randomUUID(), name, BigDecimal.valueOf(price));
    }

    private Product createProduct(final UUID id, final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}