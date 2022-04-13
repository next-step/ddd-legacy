package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.FakeProfanityClient;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static kitchenpos.MenuGroupFixture.menuGroup;
import static kitchenpos.MenuProductFixture.menuProduct;
import static kitchenpos.ProductFixture.product;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MenuServiceTest {

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;

    private ProfanityClient profanityClient;

    private MenuService menuService;

    private MenuGroup menuGroup;
    private Product product;

    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        productRepository = new InMemoryProductRepository();
        profanityClient = new FakeProfanityClient();

        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, profanityClient);
        saveDummyMenu();
    }

    void saveDummyMenu() {
        menuGroup = menuGroupRepository.save(menuGroup());
        product = productRepository.save(product("옛날통닭", 18000L));
    }

    @DisplayName("메뉴를 생성한다.")
    @ParameterizedTest
    @CsvSource(value = {"1인 한마리메뉴,18000", "이벤트 특가,10000"}, delimiter = ',')
    void create(String name, long price) {
        MenuProduct menuProduct = menuProduct(product.getId(), 1);
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct);

        Menu createdMenu = menuService.create(createMenu(name, price, menuGroup, menuProducts));

        assertThat(createdMenu).isNotNull();
        assertAll(
                () -> assertThat(createdMenu.getId()).isNotNull(),
                () -> assertThat(createdMenu.getName()).isEqualTo(name),
                () -> assertThat(createdMenu.getPrice()).isEqualTo(BigDecimal.valueOf(price))
        );
    }

    @DisplayName("메뉴의 가격은 0원 이상이여야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -5000, -10000})
    void menuPriceTest(int price) {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product.getId(), 1));

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
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product.getId(), quantity));

        assertThatThrownBy(
                () -> menuService.create(createMenu("후라이드 1인 메뉴", 18000L, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴에 포함된 상품 총가격보다 비싸면 안된다.")
    @ParameterizedTest
    @CsvSource(value = {"19000,1", "37000,2", "55000,3"}, delimiter = ',')
    void menuPriceUnderThanProductPrice(int menuPrice, int productQuantity) {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product.getId(), productQuantity));

        assertThatThrownBy(
                () -> menuService.create(createMenu("후라이드 1인 메뉴", menuPrice, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름은 반듯이 입력해야한다.")
    @ParameterizedTest
    @NullSource
    void invalidMenuNamesNullCase(String menuName) {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product.getId(), 1));

        assertThatThrownBy(
                () -> menuService.create(createMenu(menuName, 18000L, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 이름은 욕설, 비속어등이 올수없다.")
    @ParameterizedTest
    @ValueSource(strings = {"욕설", "비속어"})
    void invalidMenuNames(String menuName) {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product.getId(), 1));

        assertThatThrownBy(
                () -> menuService.create(createMenu(menuName, 18000L, menuGroup, menuProducts))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 수정할수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {3000, 3500, 4000})
    void changeMenuPrice(int price) {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product, 1));
        Menu singleMenu = menuRepository.save(createMenu("양념치킨", 5000L, menuGroup, menuProducts));

        singleMenu.setPrice(BigDecimal.valueOf(price));
        Menu menu = menuService.changePrice(singleMenu.getId(), singleMenu);

        assertAll(
                () -> assertThat(menu.getId()).isEqualTo(singleMenu.getId()),
                () -> assertThat(menu.getPrice()).isEqualTo(BigDecimal.valueOf(price))
        );
    }

    @DisplayName("메뉴의 0원 이상이여야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-100, -1000, -2000})
    void invalidMenuPrice(int price) {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product, 1));
        Menu singleMenu = menuRepository.save(createMenu("양념치킨", 5000L, menuGroup, menuProducts));

        singleMenu.setPrice(BigDecimal.valueOf(price));

        assertThatThrownBy(
                () -> menuService.changePrice(singleMenu.getId(), singleMenu)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격은 메뉴에 포함된 상품의 총가격보다 비싸서는 안된다.")
    @ParameterizedTest
    @ValueSource(ints = {20000, 30000, 40000})
    void invalidMenuPriceUpperThanProductPrice(int price) {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product, 1));
        Menu singleMenu = menuRepository.save(createMenu("양념치킨", 5000L, menuGroup, menuProducts));
        singleMenu.setPrice(BigDecimal.valueOf(price));

        assertThatThrownBy(
                () -> menuService.changePrice(singleMenu.getId(), singleMenu)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 노출상태로 변경할수 있다.")
    @Test
    void display() {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product, 1));
        Menu hideMenu = createMenu("사장님의 비밀병기", 5000L, false, menuGroup, menuProducts);
        Menu savedHideMenu = menuRepository.save(hideMenu);

        Menu menu = menuService.display(savedHideMenu.getId());

        assertThat(menu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 비노출상태로 변경할수 있다.")
    @Test
    void hide() {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product, 1));
        Menu createdMenu = createMenu("간장마늘치킨", 5000L, menuGroup, menuProducts);
        Menu savedMenu = menuRepository.save(createdMenu);

        Menu menu = menuService.hide(savedMenu.getId());

        assertThat(menu.isDisplayed()).isFalse();
    }

    @DisplayName("메뉴에 포함된 상품의 총 가격보다 메뉴의 가격이 바쌀경우 노출상태로 변경이 불가능하다.")
    @Test
    void unableChangeDisplay() {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product, 1));
        Menu menu = createMenu("준비중인메뉴 단품", 200000L, menuGroup, menuProducts);
        menuRepository.save(menu);

        assertThatThrownBy(
                () -> menuService.display(menu.getId())
        ).isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("메뉴를 조회한다.")
    @Test
    void findAll() {
        List<MenuProduct> menuProducts = Arrays.asList(menuProduct(product.getId(), 1));
        Menu menu = createMenu("준비중인메뉴 단품", 200000L, menuGroup, menuProducts);
        menuRepository.save(menu);

        List<Menu> findMenu = menuService.findAll();

        assertThat(findMenu).hasSize(1);
    }

    public Menu createMenu(String name, long price, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProducts);

        return createMenu(name, price, true, menuGroup, menuProducts);
    }

    public Menu createMenu(String name, long price, boolean display, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(display);
        menu.setMenuProducts(menuProducts);

        return menu;
    }
}
