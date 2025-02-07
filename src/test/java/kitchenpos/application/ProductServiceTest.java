package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Product;
import kitchenpos.mock.client.FakePurgomalumClient;
import kitchenpos.mock.fixture.ProductFixture;
import kitchenpos.mock.persistence.FakeMenuRepository;
import kitchenpos.mock.persistence.FakeProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.util.Pair;

class ProductServiceTest {

    private static final String PRODUCT_NAME = "치킨";
    private static final String BAD_PRODUCT_NAME = "bad 치킨";
    private static final BigDecimal PRICE = BigDecimal.valueOf(20000);
    private static final BigDecimal MINUS_PRICE = BigDecimal.valueOf(-100);

    private ProductService productService;

    @BeforeEach
    void setUp() {
        FakeProductRepository productRepository = new FakeProductRepository();
        FakeMenuRepository menuRepository = new FakeMenuRepository();
        FakePurgomalumClient purgomalumClient = new FakePurgomalumClient(new RestTemplateBuilder());
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("이름과 가격으로 상품을 생성할 수 있다.")
    @Test
    void create() {
        // given
        Product product = ProductFixture.create(PRODUCT_NAME, PRICE);

        // when
        Product savedProduct = productService.create(product);

        // then
        assertThat(savedProduct.getName()).isEqualTo(PRODUCT_NAME);
        assertThat(savedProduct.getPrice()).isEqualTo(PRICE);
    }

    @DisplayName("비속어 상품 이름으로 생성 시, 예외가 발생한다.")
    @Test
    void createNameProfanityException() {
        // given
        Product product = ProductFixture.create(BAD_PRODUCT_NAME, PRICE);

        // when then
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이름 없이 상품을 생성 시, 예외가 발생한다.")
    @ParameterizedTest
    @NullSource
    void createNameException(String name) {
        // given
        Product product = ProductFixture.create(name, PRICE);

        // when then
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("0원 가격으로 상품 생성 시, 예외가 발생한다.")
    @Test
    void createPriceException() {
        // given
        Product product = ProductFixture.create(PRODUCT_NAME, MINUS_PRICE);

        // when then
        assertThatThrownBy(() -> productService.create(product))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 가격을 수정할 수 있다.")
    @Test
    void changePrice() {
        // given
        Product product = ProductFixture.create(PRODUCT_NAME, PRICE);
        Product savedProduct = productService.create(product);

        UUID productId = savedProduct.getId();
        BigDecimal newPrice = PRICE.add(BigDecimal.valueOf(10000));
        Product newProduct = ProductFixture.create(newPrice);

        // when
        Product updatedProduct = productService.changePrice(productId, newProduct);

        // then
        assertThat(updatedProduct.getPrice()).isEqualTo(newPrice);
        assertThat(updatedProduct.getId()).isEqualTo(productId);
    }

    @DisplayName("마이너스 가격으로 상품 가격 수정 시, 예외가 발생한다.")
    @Test
    void changePricePriceException() {
        // given
        Product product = ProductFixture.create(PRODUCT_NAME, BigDecimal.ZERO);
        Product savedProduct = productService.create(product);

        UUID productId = savedProduct.getId();
        BigDecimal newPrice = savedProduct.getPrice().add(MINUS_PRICE);
        Product newProduct = ProductFixture.create(newPrice);

        // when then
        assertThatThrownBy(() -> productService.changePrice(productId, newProduct))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이름과 가격으로 상품을 생성할 수 있다.")
    @Test
    void findAll() {
        // given
        List<Pair<String, BigDecimal>> products = List.of(
            Pair.of("치킨", BigDecimal.valueOf(20_000)),
            Pair.of("피자", BigDecimal.valueOf(25_000)),
            Pair.of("삼겹살", BigDecimal.valueOf(28_800)),
            Pair.of("타코", BigDecimal.valueOf(24_000)),
            Pair.of("냉면", BigDecimal.valueOf(12_000))
        );

        products.forEach(pair ->
            productService.create(ProductFixture.create(pair.getFirst(), pair.getSecond()))
        );

        // when
        List<Product> savedProducts = productService.findAll();

        // then
        assertThat(savedProducts)
            .hasSize(products.size())
            .extracting(Product::getName, Product::getPrice)
            .containsExactlyInAnyOrderElementsOf(
                products.stream()
                    .map(pair -> tuple(pair.getFirst(), pair.getSecond()))
                    .toList()
            );
    }
}
