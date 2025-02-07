package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.mock.TestUtil;
import kitchenpos.mock.client.FakePurgomalumClient;
import kitchenpos.mock.fixture.MenuFixture;
import kitchenpos.mock.fixture.MenuProductFixture;
import kitchenpos.mock.persistence.FakeMenuGroupRepository;
import kitchenpos.mock.persistence.FakeMenuRepository;
import kitchenpos.mock.persistence.FakeOrderTableRepository;
import kitchenpos.mock.persistence.FakeProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

class MenuServiceTest {

    private static final String MENU_NAME = "두마리 치킨";
    private static final String BAD_MENU_NAME = "bad 두마리 치킨";
    private static final boolean MENU_DISPLAYED = true;

    private MenuService menuService;
    private TestUtil testUtil;

    @BeforeEach
    void setUp() {
        FakeMenuRepository menuRepository = new FakeMenuRepository();
        FakeMenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
        FakeProductRepository productRepository = new FakeProductRepository();
        FakePurgomalumClient purgomalumClient = new FakePurgomalumClient(new RestTemplateBuilder());
        this.menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
            purgomalumClient);
        this.testUtil = new TestUtil(menuGroupRepository, productRepository,
            menuRepository, new FakeOrderTableRepository());
    }

    @DisplayName("메뉴를 생성할 수 있다.")
    @Test
    void create() {
        // given
        int quantity = 2;
        MenuGroup menuGroup = testUtil.getSavedMenuGroup();
        List<MenuProduct> menuProducts = testUtil.getMenuProducts(quantity);
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.PRICE, MENU_DISPLAYED, menuGroup,
            menuProducts);

        // when
        Menu savedMenu = menuService.create(menu);

        // then
        assertThat(savedMenu.getName()).isEqualTo(MENU_NAME);
        assertThat(savedMenu.getPrice()).isEqualTo(TestUtil.PRICE);
        assertThat(savedMenu.isDisplayed()).isEqualTo(MENU_DISPLAYED);
        assertThat(savedMenu.getMenuGroup().getId()).isEqualTo(menuGroup.getId());
        assertThat(savedMenu.getMenuProducts().getFirst().getProduct().getId()).isEqualTo(
            menuProducts.getFirst().getProduct().getId());
        assertThat(savedMenu.getMenuProducts().getFirst().getQuantity()).isEqualTo(
            menuProducts.getFirst().getQuantity());
    }

    @DisplayName("마이너스 가격으로 메뉴를 생성 시, 예외가 발생 한다.")
    @Test
    void createPriceException() {
        // given
        int quantity = 2;
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.MINUS_PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(), testUtil.getMenuProducts(quantity));

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 구성 없이 메뉴를 생성 시, 예외가 발생 한다.")
    @Test
    void createMenuProductsException() {
        // given
        List<MenuProduct> menuProducts = null;
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.MINUS_PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(), menuProducts);

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 구성 개수가 일치 하지 않으면 메뉴 생성 시, 예외가 발생 한다.")
    @Test
    void createMenuProductsSizeException() {
        // given
        int quantity = 2;
        List<MenuProduct> menuProducts = testUtil.getMenuProducts(quantity);
        MenuProduct menuProduct = MenuProductFixture.create(testUtil.getSavedProduct(), 1);
        menuProducts.add(menuProduct);
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.MINUS_PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(), menuProducts);

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("상품 구성 개수가 0개이면 메뉴 생성 시, 예외가 발생 한다.")
    @Test
    void createMenuProductQuantityException() {
        // given
        int quantity = 0;
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.MINUS_PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(), testUtil.getMenuProducts(quantity));

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구성한 상품 가격이 0원면 메뉴 생성 시, 예외가 발생 한다.")
    @Test
    void createMenuProductPriceException() {
        // given
        int quantity = 1;

        String productName = "양념치킨";
        BigDecimal productPrice = BigDecimal.ZERO;
        Product product = testUtil.getSavedProduct(productName, productPrice);

        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.MINUS_PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(), testUtil.getMenuProducts(product, quantity));

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("비속어 이름으로 메뉴 생성 시, 예외가 발생 한다.")
    @Test
    void createNameException() {
        // given
        int quantity = 2;
        Menu menu = MenuFixture.create(BAD_MENU_NAME, TestUtil.PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(),
            testUtil.getMenuProducts(quantity));

        // when then
        assertThatThrownBy(() -> menuService.create(menu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 가격을 수정할 수 있다.")
    @Test
    void changePrice() {
        // given
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(),
            testUtil.getMenuProducts(2));
        Menu savedMenu = menuService.create(menu);

        UUID id = savedMenu.getId();
        BigDecimal newPrice = BigDecimal.valueOf(10_000);
        Menu newMenu = MenuFixture.create(newPrice);

        // when
        Menu updatedMenu = menuService.changePrice(id, newMenu);

        // then
        assertThat(updatedMenu.getPrice()).isEqualTo(newPrice);
    }

    @DisplayName("마이너스 가격으로 메뉴 가격 수정 시, 예외가 발생한다.")
    @Test
    void changePriceException() {
        // given
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(),
            testUtil.getMenuProducts(2));
        Menu savedMenu = menuService.create(menu);

        UUID id = savedMenu.getId();
        Menu newMenu = MenuFixture.create(TestUtil.MINUS_PRICE);

        // when then
        assertThatThrownBy(() -> menuService.changePrice(id, newMenu))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 전시 상태로 변경할 수 있다.")
    @Test
    void display() {
        // given
        boolean displayed = false;
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.PRICE, displayed,
            testUtil.getSavedMenuGroup(),
            testUtil.getMenuProducts(2));
        Menu savedMenu = menuService.create(menu);

        UUID id = savedMenu.getId();

        // when
        Menu updatedMenu = menuService.display(id);

        // then
        assertThat(updatedMenu.isDisplayed()).isTrue();
    }

    @DisplayName("메뉴를 숨김 상태로 변경할 수 있다.")
    @Test
    void hide() {
        // given
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(),
            testUtil.getMenuProducts(2));
        Menu savedMenu = menuService.create(menu);

        UUID id = savedMenu.getId();

        // when
        Menu updatedMenu = menuService.hide(id);

        // then
        assertThat(updatedMenu.isDisplayed()).isFalse();
    }

    @DisplayName("저장된 메뉴 리스트를 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Menu menu = MenuFixture.create(MENU_NAME, TestUtil.PRICE, MENU_DISPLAYED,
            testUtil.getSavedMenuGroup(),
            testUtil.getMenuProducts(2));
        Menu savedMenu = menuService.create(menu);
        Menu anotherSavedMenu = menuService.create(menu);

        // when
        List<Menu> savedMenus = menuService.findAll();

        // then
        assertThat(savedMenus)
            .hasSize(2)
            .extracting(Menu::getId)
            .containsExactlyInAnyOrder(anotherSavedMenu.getId(), savedMenu.getId());
    }

}
