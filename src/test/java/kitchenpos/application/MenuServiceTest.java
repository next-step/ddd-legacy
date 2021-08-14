package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakePurgomalumClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {
    private static long seq = 1;
    private static final List<MenuGroup> menuGroups = Arrays.asList(
            createMenuGroup("첫번째 그룹"),
            createMenuGroup("두번째 그룹"),
            createMenuGroup("세번째 그룹"),
            createMenuGroup("네번째 그룹")
    );
    private static final List<Product> products = Arrays.asList(
            createProduct("첫번째 상품", BigDecimal.valueOf(1_100)),
            createProduct("두번째 상품", BigDecimal.valueOf(2_200)),
            createProduct("세번째 상품", BigDecimal.valueOf(3_300)),
            createProduct("네번째 상품", BigDecimal.valueOf(4_400))
    );
    private static final List<Menu> menus = Arrays.asList(
            createMenu("첫번째 메뉴"),
            createMenu("두번째 메뉴"),
            createMenu("세번째 메뉴")
    );

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private PurgomalumClient purgomalumClient;
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuRepository = new FakeMenuRepository();
        menuGroupRepository = new FakeMenuGroupRepository();
        productRepository = new FakeProductRepository();
        purgomalumClient = new FakePurgomalumClient();

        menuGroups.forEach(menuGroupRepository::save);
        products.forEach(productRepository::save);
        menus.forEach(menuRepository::save);

        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("새로운 메뉴를 추가할 수 있다.")
    @Test
    void create() {
        // given
        final Menu expectedMenu = createMenu("신메뉴");

        // when
        final Menu menu = menuService.create(expectedMenu);

        //then
        assertAll(
                () -> assertThat(menu.getName())
                        .isEqualTo(expectedMenu.getName()),
                () -> assertThat(menu.getPrice())
                        .isEqualTo(expectedMenu.getPrice()),
                () -> assertThat(menu.getMenuGroup())
                        .isEqualTo(expectedMenu.getMenuGroup()),
                () -> assertThat(menu.isDisplayed())
                        .isEqualTo(expectedMenu.isDisplayed()));
    }

    @DisplayName("가격이 없는 메뉴는 추가할 수 없다.")
    @Test
    void create_emptyPrice() {
        // given
        final Menu menu = createMenu("신메뉴");
        menu.setPrice(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("가격이 음수인 메뉴는 추가할 수 없다.")
    @Test
    void create_negativePrice() {
        // given
        final Menu menu = createMenu("신메뉴");
        menu.setPrice(BigDecimal.valueOf(-1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴 그룹에 속하지 않은 메뉴는 추가할 수 없다.")
    @Test
    void create_emptyMenuGroup() {
        // given
        final Menu menu = createMenu("신메뉴");
        menu.setMenuGroupId(null);

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("기존 메뉴 그룹에 속하지 않은 메뉴는 추가할 수 없다.")
    @Test
    void create_badMenuGroup() {
        // given
        final Menu menu = createMenu("신메뉴");
        menu.setMenuGroupId(UUID.randomUUID());

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴는 반드시 상품을 포함해야 한다.")
    @MethodSource
    @ParameterizedTest
    void create_emptyProduct(final List<MenuProduct> menuProducts) {
        // given
        final Menu menu = createMenu("신메뉴");
        menu.setMenuProducts(menuProducts);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    private static Stream<List<MenuGroup>> create_emptyProduct() {
        return Stream.of(null, Collections.emptyList());
    }

    @DisplayName("메뉴를 구성하는 상품은 이미 추가된 상품이어야 한다.")
    @Test
    void create_badProduct() {
        // given
        final Menu menu = createMenu("신메뉴");
        final List<MenuProduct> menuProducts = Collections.singletonList(new MenuProduct());
        menu.setMenuProducts(menuProducts);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 상품 수량은 음수일 수 없다.")
    @Test
    void create_negativeProductQuantity() {
        // given
        final Menu menu = createMenu("신메뉴");
        final int quantity = -1;
        final List<MenuProduct> menuProducts = Collections.singletonList(createMenuProduct(products.get(0), quantity));
        menu.setMenuProducts(menuProducts);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 가격은 각 상품가격의 총합보다 작아야 한다.")
    @Test
    void create_expensive() {
        // given
        final Menu menu = createMenu("신메뉴");
        final BigDecimal price = BigDecimal.valueOf(1_000_000);
        menu.setPrice(price);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 이름은 필수값이며, 비어있을 수 없다.")
    @Test
    void create_emptyName() {
        // given
        final Menu menu = createMenu(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 이름은 저속해서는 안된다.")
    @Test
    void create_badName() {
        // given
        final Menu menu = createMenu("비속어 욕설 메뉴");

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.create(menu));
    }

    @DisplayName("메뉴의 가격을 수정할 수 있다.")
    @Test
    void changePrice() {
        // given
        final Menu menu = menus.get(0);
        final BigDecimal changedPrice = BigDecimal.valueOf(20);
        final Menu changedMenu = createMenu(menu.getName());
        changedMenu.setPrice(changedPrice);

        // when
        menuService.changePrice(menu.getId(), changedMenu);

        // then
        assertThat(menu.getPrice())
                .isEqualTo(changedPrice);
    }

    @DisplayName("메뉴의 가격을 수정할 때 가격을 반드시 입력해야한다.")
    @Test
    void changePrice_emptyPrice() {
        // given
        final Menu menu = menus.get(0);
        final Menu changedMenu = createMenu(menu.getName());
        changedMenu.setPrice(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu));
    }

    @DisplayName("메뉴의 가격을 수정할 때 가격을 음수로 입력할 수 없다.")
    @Test
    void changePrice_negativePrice() {
        // given
        final Menu menu = menus.get(0);
        final Menu changedMenu = createMenu(menu.getName());
        changedMenu.setPrice(BigDecimal.valueOf(-1));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu));
    }

    @DisplayName("메뉴의 가격을 수정할 때, 수정할 메뉴를 필수적으로 지정해야한다.")
    @Test
    void changePrice_emptyMenu() {
        // given
        final UUID menuId = null;
        final Menu changedMenu = createMenu("바뀐 메뉴");
        changedMenu.setPrice(BigDecimal.valueOf(1));

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, changedMenu));
    }

    @DisplayName("가격을 수정할 메뉴는 이미 추가되어 있는 메뉴여야 한다.")
    @Test
    void changePrice_badMenu() {
        // given
        final UUID menuId = UUID.randomUUID();
        final Menu changedMenu = createMenu("바뀐 메뉴");
        changedMenu.setPrice(BigDecimal.valueOf(1));

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.changePrice(menuId, changedMenu));
    }

    @DisplayName("수정된 메뉴의 가격은 각 상품 가격의 총합보다 작아야 한다.")
    @Test
    void changePrice_expensivePrice() {
        // given
        final Menu menu = menus.get(0);
        final Menu changedMenu = createMenu(menu.getName());
        changedMenu.setPrice(BigDecimal.valueOf(1_000_000));

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> menuService.changePrice(menu.getId(), changedMenu));
    }

    @DisplayName("메뉴가 보여지도록 할 수 있다.")
    @Test
    void display() {
        // given
        final Menu menu = menus.get(0);
        menu.setDisplayed(false);

        // when
        menuService.display(menu.getId());

        // then
        assertThat(menu.isDisplayed())
                .isTrue();
    }

    @DisplayName("보여질 메뉴는 필수적으로 지정해야한다.")
    @Test
    void display_emptyMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.display(null));
    }

    @DisplayName("보여질 메뉴는 이미 추가되어 있어야 한다.")
    @Test
    void display_badMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.display(UUID.randomUUID()));
    }

    @DisplayName("메뉴가 숨겨지도록 할 수 있다.")
    @Test
    void hide() {
        // given
        final Menu menu = menus.get(0);
        menu.setDisplayed(true);

        // when
        menuService.hide(menu.getId());

        // then
        assertThat(menu.isDisplayed())
                .isFalse();
    }

    @DisplayName("숨겨질 메뉴는 필수적으로 지정해야한다.")
    @Test
    void hide_emptyMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.hide(null));
    }

    @DisplayName("숨겨질 메뉴는 이미 추가되어 있어야 한다.")
    @Test
    void findAll() {
        // given
        final List<String> expectedNames = menus.stream()
                .map(Menu::getName)
                .collect(Collectors.toList());

        // when
        final List<String> actualNames = menuService.findAll().stream()
                .map(Menu::getName)
                .collect(Collectors.toList());

        // then
        assertThat(actualNames)
                .isEqualTo(expectedNames);
    }

    @DisplayName("메뉴들의 목록을 조회할 수 있다.")
    @Test
    void hide_badMenu() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> menuService.hide(UUID.randomUUID()));
    }

    private static MenuGroup createMenuGroup(final String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    private static Product createProduct(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private static MenuProduct createMenuProduct(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        menuProduct.setSeq(seq++);
        return menuProduct;
    }

    private static Menu createMenu(final String name) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(1));
        final MenuGroup menuGroup = menuGroups.get(0);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(true);
        final List<MenuProduct> menuProducts = products.stream()
                .map(product -> createMenuProduct(product, 1))
                .collect(Collectors.toList());
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
