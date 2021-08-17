package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.InMemoryMenuRepository;
import kitchenpos.domain.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.FakePurgomalunClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ProductServiceTest {

    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private PurgomalumClient purgomalumClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        menuRepository = new InMemoryMenuRepository();
        purgomalumClient = new FakePurgomalunClient();
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
    }

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void create() {
        final Product expected = createProductRequest("후라이드", 16000L);

        final Product actual = productService.create(expected);

        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @DisplayName("상품의 이름이 올바르지 않으면 등록할 수 없다.")
    @ValueSource(strings = {"비속어", "욕설"})
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidName(final String name) {
        final Product expected = createProductRequest(name, 16000L);

        assertThatThrownBy(() -> productService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격이 올바르지 않으면 등록할 수 없다.")
    @ValueSource(strings = "-16000")
    @NullSource
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidPrice(final BigDecimal price) {
        final Product expected = createProductRequest("후라이드", price);

        assertThatThrownBy(() -> productService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        final Product original = productRepository.save(createProduct("후라이드", 16000L));
        final Product expected = createProductRequest(original.getName(), 18000L);

        final Product actual = productService.changePrice(original.getId(), expected);

        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getName()).isEqualTo(original.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice())
        );
    }

    @DisplayName("상품의 가격이 올바르지 않으면 변경할 수 없다.")
    @ValueSource(strings = "-18000")
    @NullSource
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void changePrice_InvalidPrice(final BigDecimal price) {
        final Product expected = createProductRequest("후라이드", price);

        assertThatThrownBy(() -> productService.changePrice(UUID.randomUUID(), expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 상품의 가격을 변경할 수 없다.")
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @NullSource
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void changePrice_UnregisteredProduct(final UUID id) {
        final Product expected = createProductRequest("후라이드", 18000L);

        assertThatThrownBy(() -> productService.changePrice(id, expected))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("상품의 가격이 올바르지 않으면 메뉴를 숨긴다.")
    @Test
    void changePrice_HideMenu() {
        // given
        final Product product1 = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setSeq(1L);
        menuProduct1.setProduct(product1);
        menuProduct1.setQuantity(1);
        menuProduct1.setProductId(product1.getId());

        final Product product2 = productRepository.save(createProduct("양념치킨", 16000L));
        final MenuProduct menuProduct2 = new MenuProduct();
        menuProduct2.setSeq(2L);
        menuProduct2.setProduct(product2);
        menuProduct2.setQuantity(1);
        menuProduct2.setProductId(product2.getId());

        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드치킨+양념치킨");
        menu.setPrice(BigDecimal.valueOf(32000L));
        menu.setMenuGroup(new MenuGroup());
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProduct1, menuProduct2));

        final Menu actualMenu = menuRepository.save(menu);

        // when
        productService.changePrice(product1.getId(), createProductRequest("후라이드", 8000L));

        // then
        assertThat(actualMenu.isDisplayed()).isFalse();
    }

    @DisplayName("상품의 목록을 조회할 수 있다.")
    @Test
    public void findAll() {
        productRepository.save(createProduct("후라이드", 16000L));
        productRepository.save(createProduct("양념치킨", 16000L));

        final List<Product> actual = productService.findAll();

        assertThat(actual).hasSize(2);
    }

    private Product createProduct(final UUID id, final String name, final long price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    private Product createProduct(final String name, final long price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    private Product createProductRequest(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private Product createProductRequest(final String name, final long price) {
        return createProductRequest(name, BigDecimal.valueOf(price));
    }
}
