package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MenuServiceTest {

    public static MenuRepository menuRepository = new InMemoryMenuRepository();

    private MenuGroupRepository menuGroupRepository = MenuGroupServiceTest.menuGroupRepository;
    private ProductRepository productRepository = ProductServiceTest.productRepository;

    private ProfanityClient profanityClient = new FakeProfanityClient();

    private MenuService menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);

    private MenuGroup menuGroup;
    private Product product;
    private MenuGroup toastMenuGroup;
    private Menu savedSingleMenu;
    private Menu hideMenu;

    @BeforeEach
    public void saveDummyMenu() {

        menuGroup = MenuGroupServiceTest.saveMenuGroup(MenuGroupTest.create("1인 혼닭"));
        product = ProductServiceTest.save(ProductTest.create("옛날통닭", 18000L));

        MenuGroup setMenuGroup = MenuGroupServiceTest.saveMenuGroup(MenuGroupTest.create("1인메뉴"));
        toastMenuGroup = MenuGroupServiceTest.saveMenuGroup(MenuGroupTest.create("인기 대표 토스트"));

        Product 토스트 = ProductServiceTest.save(ProductTest.create("계란햄치즈토스트", 5000L));
        Product 커피 = ProductServiceTest.save(ProductTest.create("아이스아메리카노", 3000L));

        List<MenuProduct> setMenuProducts = new ArrayList();
        List<MenuProduct> singleMenuProducts = new ArrayList();
        setMenuProducts.add(MenuProductTest.create(토스트, 1));
        setMenuProducts.add(MenuProductTest.create(커피, 1));

        singleMenuProducts.add(MenuProductTest.create(토스트, 1));

        Menu setMenu = createMenu("든든한 아침 세트", 7000L, setMenuGroup, setMenuProducts);
        Menu singleMenu = createMenu("토스트 단품", 5000L, toastMenuGroup, singleMenuProducts);
        Menu hideMenu = createMenu("사장님의 비밀병기", 5000L, false, toastMenuGroup, singleMenuProducts);

        menuRepository.save(setMenu);

        this.savedSingleMenu = menuRepository.save(singleMenu);
        this.hideMenu = menuRepository.save(hideMenu);
    }

    @AfterEach
    void clearDummyMenu() {
        menuRepository.deleteDataForTest();
    }

    @DisplayName("메뉴를 생성한다.")
    @Test
    void create() {
        List<MenuProduct> menuProducts = new ArrayList();
        MenuProduct menuProduct = MenuProductTest.create(product, 1);
        menuProducts.add(menuProduct);

        Menu createdMenu = menuService.create(createMenu("후라이드 1인 메뉴", 18000L, menuGroup, menuProducts));

        assertThat(createdMenu).isNotNull();
        assertThat(createdMenu.getId()).isNotNull();
    }

    @DisplayName("메뉴의 가격은 0원 이상이여야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -5000, -10000})
    void menuPriceTest(int price) {
        List<MenuProduct> menuProducts = new ArrayList();
        menuProducts.add(MenuProductTest.create(product, 1));

        assertThatThrownBy(
                () -> menuService.create(createMenu("후라이드 1인 메뉴", price, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 등록시 1개 이상의 상품이 포함되어야 한다.")
    @Test
    void necessaryProduct() {
        assertThatThrownBy(
                () -> menuService.create(createMenu("후라이드 1인 메뉴", 18000L, menuGroup, new ArrayList()))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴에 포함되는 각 상품은 0개이상 포함되어야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -10})
    void necessaryProductCount(int quantity) {
        List<MenuProduct> menuProducts = new ArrayList();
        menuProducts.add(MenuProductTest.create(product, quantity));

        assertThatThrownBy(
                () -> menuService.create(createMenu("후라이드 1인 메뉴", 18000L, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴에 포함된 상품 총가격보다 비싸면안된다.")
    @ParameterizedTest
    @CsvSource(value = {"19000,1", "37000,2", "55000,3"}, delimiter = ',')
    void menuPriceUnderThanProductPrice(int menuPrice, int productQuantity) {
        List<MenuProduct> menuProducts = new ArrayList();
        menuProducts.add(MenuProductTest.create(product, productQuantity));

        assertThatThrownBy(
                () -> menuService.create(createMenu("후라이드 1인 메뉴", menuPrice, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    static Stream<String> invalidMenuNames() {
        return Stream.of(null, "비속어", "욕설");
    }

    @DisplayName("메뉴의 이름은 지정해야하며 욕설, 비속어등이 올수없다.")
    @ParameterizedTest
    @MethodSource("invalidMenuNames")
    void invalidMenuNames(String menuName) {
        List<MenuProduct> menuProducts = new ArrayList();
        menuProducts.add(MenuProductTest.create(product, 1));

        assertThatThrownBy(
                () -> menuService.create(createMenu(menuName, 18000L, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 수정할수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {3000, 3500, 4000})
    void changeMenuPrice(int price) {
        Menu singleMenu = this.savedSingleMenu;
        singleMenu.setPrice(BigDecimal.valueOf(price));

        Menu menu = menuService.changePrice(singleMenu.getId(), singleMenu);

        assertThat(menu.getId()).isEqualTo(singleMenu.getId());
        assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(price));
    }

    @DisplayName("메뉴의 0원 이상이여야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1000, -2000})
    void invalidMenuPrice(int price) {
        Menu singleMenu = this.savedSingleMenu;
        singleMenu.setPrice(BigDecimal.valueOf(price));

        assertThatThrownBy(
                () -> menuService.changePrice(singleMenu.getId(), singleMenu)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴에 포함된 상품의 총가격보다 비싸서는 안된다.")
    @ParameterizedTest
    @ValueSource(ints = {20000, 30000, 40000})
    void invalidMenuPriceUpperThanProductPrice(int price) {
        Menu singleMenu = this.savedSingleMenu;
        singleMenu.setPrice(BigDecimal.valueOf(price));

        assertThatThrownBy(
                () -> menuService.changePrice(singleMenu.getId(), singleMenu)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 노출상태로 변경할수 있다.")
    @Test
    void display() {
        Menu menu = menuService.display(hideMenu.getId());

        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 비노출상태로 변경할수 있다.")
    @Test
    void hide() {
        Menu menu = menuService.hide(savedSingleMenu.getId());

        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴에 포함된 상품의 총 가격보다 메뉴의 가격이 바쌀경우 노출상태로 변경이 불가능하다.")
    @Test
    void unableChangeDisplay() {
        List<MenuProduct> menuProducts = new ArrayList();
        menuProducts.add(MenuProductTest.create(product, 1));
        Menu menu = createMenu("준비중인메뉴 단품", 200000L, toastMenuGroup, menuProducts);
        menuRepository.save(menu);

        assertThatThrownBy(
                () -> menuService.display(menu.getId())
        ).isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("메뉴를 조회한다.")
    @Test
    void findAll() {
        int menuSize = menuRepository.findAll()
                .size();

        List<Menu> findMenu = menuService.findAll();

        assertThat(findMenu).hasSize(menuSize);
    }

    public static Menu createMenu(String name, long price, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);

        return createMenu(name, price, true, menuGroup, menuProducts);
    }

    public static Menu createMenu(String name, long price, boolean display, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(display);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static Menu save(String name, long price, boolean display, MenuGroup menuGroup, List<MenuProduct> setMenuProducts) {
        Menu setMenu = MenuServiceTest.createMenu(name, price, display, menuGroup, setMenuProducts);
        return menuRepository.save(setMenu);
    }
}
