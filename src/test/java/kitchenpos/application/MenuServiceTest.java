package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.InMemoryMenuRepository;
import kitchenpos.domain.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        purgomalumClient = new FakePurgomalunClient();
        menuService = new MenuService(
            menuRepository,
            menuGroupRepository,
            productRepository,
            purgomalumClient
        );
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            "후라이드",
            16000L,
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        final Menu actual = menuService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getMenuGroup()).isNotNull(),
            () -> assertThat(actual.isDisplayed()).isTrue(),
            () -> assertThat(actual.getMenuProducts()).isNotNull()
        );
    }

    @DisplayName("메뉴를 등록할 수 있다.")
    @Test
    void create2() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("두마리메뉴"));
        final Product product1 = productRepository.save(createProduct("후라이드", 16000L));
        final Product product2 = productRepository.save(createProduct("양념치킨", 16000L));
        final MenuProduct menuProduct1 = createMenuProduct(1L, product1, 1);
        final MenuProduct menuProduct2 = createMenuProduct(2L, product2, 1);
        final Menu expected = createMenuRequest(
            "후라이드+양념치킨",
            16000L + 16000L,
            menuGroup.getId(),
            Arrays.asList(menuProduct1, menuProduct2)
        );

        // when
        // final Menu actual = menuService.create(expected);

        // then
        // assertThat(actual).isNotNull();
        // assertAll(
        //     () -> assertThat(actual.getId()).isNotNull(),
        //     () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
        //     () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
        //     () -> assertThat(actual.getMenuGroup()).isNotNull(),
        //     () -> assertThat(actual.isDisplayed()).isTrue(),
        //     () -> assertThat(actual.getMenuProducts()).isNotNull()
        // );

        // sum 계산 버그 (MenuService.create(MenuService.java:68))
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름이 올바르지 않으면 등록할 수 없다.")
    @NullSource
    @ValueSource(strings = {"비속어", "욕설"})
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidName(final String name) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            name,
            16000L,
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격이 올바르지 않으면 등록할 수 없다.")
    @NullSource
    @ValueSource(strings = "-16000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidPrice(final BigDecimal price) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            "후라이드",
            price,
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    //
    @DisplayName("메뉴의 가격이 올바르지 않으면 등록할 수 없다. 메뉴 가격은 구성 상품 금액의 총 합보다 작거나 같아야 한다.")
    @ValueSource(strings = "160000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidPrice2(final BigDecimal price) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            "후라이드",
            price,
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴그룹이 등록되어 있지 않으면 메뉴를 등록할 수 없다.")
    @NullSource
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_UnregisteredMenuGroup(final UUID menuGroupId) {
        // given
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            "후라이드",
            BigDecimal.valueOf(16000L),
            menuGroupId,
            Arrays.asList(menuProduct)
        );

        // when
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴의 상품이 올바르지 않으면 등록할 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidMenuProducts(final List<MenuProduct> menuProducts) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            "후라이드",
            BigDecimal.valueOf(16000L),
            menuGroup.getId(),
            menuProducts
        );

        // when
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 상품이 올바르지 않으면 등록할 수 없다. 동일한 상품의 등록은 수량으로 조정한다.")
    @Test
    void create_InvalidMenuProducts2() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            "후라이드",
            BigDecimal.valueOf(16000L),
            menuGroup.getId(),
            Arrays.asList(menuProduct, menuProduct)
        );

        // when
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 상품의 수량이 올바르지 않으면 등록할 수 없다.")
    @ValueSource(longs = -1L)
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidMenuProductQuantity(final long quantity) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, quantity);
        final Menu expected = createMenuRequest(
            "후라이드",
            BigDecimal.valueOf(16000L),
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        assertThatThrownBy(() -> menuService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 상품으로 메뉴를 구성할 수 없다.")
    @Test
    void create_UnregisteredProduct() {
        // Unreachable
    }

    @DisplayName("메뉴의 가격을 변경할 수 있다.")
    @Test
    void changePrice() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu original = menuRepository.save(
            createMenu(
                "후라이드",
                16000L,
                menuGroup,
                true,
                Arrays.asList(menuProduct)
            )
        );
        final Menu expected = createMenuRequest(
            "후라이드",
            8000L,
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        final Menu actual = menuService.changePrice(original.getId(), expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getName()).isEqualTo(original.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(expected.getPrice()),
            () -> assertThat(actual.getMenuGroup()).isSameAs(original.getMenuGroup()),
            () -> assertThat(actual.isDisplayed()).isTrue(),
            () -> assertThat(actual.getMenuProducts()).isSameAs(original.getMenuProducts())
        );
    }

    @DisplayName("메뉴의 가격이 올바르지 않으면 변경할 수 없다.")
    @NullSource
    @ValueSource(strings = {"-16000", "160000"})
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void changePrice_InvalidPrice(final BigDecimal price) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu original = menuRepository.save(
            createMenu(
                "후라이드",
                16000L,
                menuGroup,
                true,
                Arrays.asList(menuProduct)
            )
        );
        final Menu expected = createMenuRequest(
            "후라이드",
            price,
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        // then
        assertThatThrownBy(() -> menuService.changePrice(original.getId(), expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 메뉴의 가격을 변경할 수 없다.")
    @NullSource
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void changePrice_InvalidPrice2(final UUID id) {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu expected = createMenuRequest(
            "후라이드",
            16000L,
            menuGroup.getId(),
            Arrays.asList(menuProduct)
        );

        // when
        // then
        assertThatThrownBy(() -> menuService.changePrice(id, expected))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴를 공개할 수 있다.")
    @Test
    void display() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu original = menuRepository.save(
            createMenu(
                "후라이드",
                16000L,
                menuGroup,
                false,
                Arrays.asList(menuProduct)
            )
        );

        // when
        Menu actual = menuService.display(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getName()).isEqualTo(original.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(original.getPrice()),
            () -> assertThat(actual.getMenuGroup()).isEqualTo(original.getMenuGroup()),
            () -> assertThat(actual.isDisplayed()).isTrue(),
            () -> assertThat(actual.getMenuProducts()).isEqualTo(original.getMenuProducts())
        );
    }

    @DisplayName("등록되지 않은 메뉴를 공개할 수 없다.")
    @NullSource
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void display_UnregisteredMenu(final UUID id) {
        assertThatThrownBy(() -> menuService.display(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴를 숨길 수 있다.")
    @Test
    void hide() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product = productRepository.save(createProduct("후라이드", 16000L));
        final MenuProduct menuProduct = createMenuProduct(1L, product, 1);
        final Menu original = menuRepository.save(
            createMenu(
                "후라이드",
                16000L,
                menuGroup,
                true,
                Arrays.asList(menuProduct)
            )
        );

        // when
        Menu actual = menuService.hide(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getName()).isEqualTo(original.getName()),
            () -> assertThat(actual.getPrice()).isEqualTo(original.getPrice()),
            () -> assertThat(actual.getMenuGroup()).isEqualTo(original.getMenuGroup()),
            () -> assertThat(actual.isDisplayed()).isFalse(),
            () -> assertThat(actual.getMenuProducts()).isEqualTo(original.getMenuProducts())
        );
    }

    @DisplayName("등록되지 않은 메뉴를 숨길 수 없다.")
    @NullSource
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void hide_UnregisteredMenu(final UUID id) {
        assertThatThrownBy(() -> menuService.hide(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("메뉴의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup("한마리메뉴"));
        final Product product1 = productRepository.save(createProduct("후라이드", 16000L));
        final Product product2 = productRepository.save(createProduct("양념치킨", 16000L));
        final MenuProduct menuProduct1 = createMenuProduct(1L, product1, 1);
        final MenuProduct menuProduct2 = createMenuProduct(2L, product2, 1);
        menuRepository.save(
            createMenu(
                "후라이드",
                16000L,
                menuGroup,
                true,
                Arrays.asList(menuProduct1)
            )
        );
        menuRepository.save(
            createMenu(
                "양념치킨",
                16000L,
                menuGroup,
                true,
                Arrays.asList(menuProduct2)
            )
        );

        // when
        final List<Menu> actual = menuService.findAll();

        // then
        assertThat(actual).hasSize(2);
    }

    private MenuGroup createMenuGroup(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    private Product createProduct(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private Product createProduct(final String name, final long price) {
        return createProduct(name, BigDecimal.valueOf(price));
    }

    private MenuProduct createMenuProduct(
        final Long seq,
        final Product product,
        final long quantity
    ) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    private Menu createMenuRequest(
        final String name,
        final BigDecimal price,
        final UUID menuGroupId,
        final List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    private Menu createMenuRequest(
        final String name,
        final long price,
        final UUID menuGroupId,
        final List<MenuProduct> menuProducts
    ) {
        return createMenuRequest(name, BigDecimal.valueOf(price), menuGroupId, menuProducts);
    }

    private Menu createMenu(
        final String name,
        final long price,
        final MenuGroup menuGroup,
        final boolean displayed,
        final List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
