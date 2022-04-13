package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static kitchenpos.MenuFixture.menu;
import static kitchenpos.MenuProductFixture.menuProduct;
import static kitchenpos.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ProductServiceTest {

    private ProductRepository productRepository;
    private MenuRepository menuRepository;

    private ProfanityClient profanityClient;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();

        profanityClient = new FakeProfanityClient();

        productService = new ProductService(productRepository, menuRepository, profanityClient);
    }

    @DisplayName("상품을 생성한다.")
    @Test
    void create() {
        Product product = ProductTest.create("후라이드", 18000L);

        Product createdProduct = productService.create(product);

        assertAll(
                () -> assertThat(createdProduct).isNotNull(),
                () -> assertThat(createdProduct.getId()).isNotNull(),
                () -> assertThat(createdProduct.getPrice()).isNotNull()
        );
    }

    @DisplayName("상품의 가격은 음수가 될수 없다")
    @ParameterizedTest
    @ValueSource(strings = {"-100", "-500", "-1000"})
    void create(long price) {
        Product product = ProductTest.create("양념치킨", price);

        assertThatThrownBy(
                () -> productService.create(product)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<String> invalidProductNames() {
        return Stream.of(null, "비속어", "욕설");
    }

    @DisplayName("상품의 이름은 지정해야하며 욕설, 비속어등이 올수없다.")
    @ParameterizedTest
    @MethodSource("invalidProductNames")
    void create(String name) {
        Product product = ProductTest.create(name, 18000L);

        assertThatThrownBy(
                () -> productService.create(product)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"10000", "15000", "20000"})
    void changePrice(BigDecimal price) {
        Product product = ProductTest.create("후라이드", 18000L);
        Product savedProduct = productRepository.save(product);

        savedProduct.setPrice(price);

        Product updateProduct = productService.changePrice(savedProduct.getId(), savedProduct);
        assertThat(updateProduct.getPrice()).isEqualTo(price);
    }

    @DisplayName("상품의 가격은 0원 이상이여야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-10000", "-15000", "-20000"})
    void invalidPrice(BigDecimal price) {
        Product product = ProductTest.create("후라이드", 18000L);
        Product savedProduct = productRepository.save(product);

        savedProduct.setPrice(price);

        assertThatThrownBy(
                () -> productService.changePrice(savedProduct.getId(), savedProduct)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 등록된 메뉴의 가격이 메뉴에 포함된 상품들의 총 가격보다 비싸면 메뉴는 비노출 된다.")
    @ParameterizedTest
    @ValueSource(strings = {"1000", "5000", "10000"})
    void ifExpensiveMenuPriceThenHideMenu(BigDecimal price) {
        Product 후라이드 = save(product("후라이드", 18000L));
        Product 양념치킨 = save(product("양념치킨", 20000L));

        Menu setMenu = menu("2마리 세트", 35000L, true, menuProduct(후라이드, 1), menuProduct(양념치킨, 1));
        menuRepository.save(setMenu);

        후라이드.setPrice(price);
        productService.changePrice(후라이드.getId(), 후라이드);

        Menu menu = menuRepository.findById(setMenu.getId())
                .get();

        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("상품의 목록을 조회한다.")
    @Test
    void findAll() {
        Product 후라이드 = product("후라이드", 18000L);
        Product 양념치킨 = product("양념치킨", 20000L);
        Product 치킨무 = product("치킨무", 500L);
        productRepository.save(후라이드);
        productRepository.save(양념치킨);
        productRepository.save(치킨무);

        List<Product> products = productService.findAll();

        assertThat(products).hasSize(3);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
}
