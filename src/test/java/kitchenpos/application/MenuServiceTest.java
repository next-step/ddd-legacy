package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.dummy.DummyMenu;
import kitchenpos.dummy.DummyMenuGroup;
import kitchenpos.dummy.DummyMenuProduct;
import kitchenpos.dummy.DummyProduct;
import kitchenpos.fake.FakeProfanityClient;
import kitchenpos.fake.InMemoryMenuGroupRepository;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryProductRepository;
import kitchenpos.infra.ProfanityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MenuServiceTest {

    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private ProductRepository productRepository = new InMemoryProductRepository();
    private ProfanityClient purgomalumClient = new FakeProfanityClient();
    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, purgomalumClient);
    }

    @DisplayName("[정상] 메뉴를 등록한다.")
    @Test
    void create() {
        Menu menu = getMenuData();
        Menu actual = menuService.create(menu);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menu.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(actual.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(menu.isDisplayed()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(false)
        );
    }

    @DisplayName("[정상] 메뉴를 등록시 기본값으로 비공개로 한다.")
    @Test
    void create_displayed_false() {
        Menu menu = getMenuData();
        Menu actual = menuService.create(menu);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menu.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(actual.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(menu.isDisplayed()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(false)
        );
    }

    @DisplayName("[오류] 상품 정보 없이 메뉴를 등록할 수 없다.")
    @Test
    void create_not_product() {
        Menu menu = getMenuData();
        menu.setMenuProducts(null);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 이름 정보 없이 메뉴를 등록할 수 없다.")
    @Test
    void create_not_name() {
        Menu menu = getMenuData();
        menu.setName(null);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 비속어를 포함하여 메뉴를 등록할 수 없다.")
    @Test
    void create_not_profanity() {
        Menu menu = getMenuData();
        menu.setName("욕설");
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 가격 정보 없이 메뉴를 등록할 수 없다.")
    @Test
    void create_not_price() {
        Menu menu = getMenuData();
        menu.setPrice(null);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 0원 보다 낮은 가격으로 메뉴를 등록할 수 없다.")
    @Test
    void create_under_0_price() {
        Menu menu = getMenuData();
        menu.setPrice(BigDecimal.valueOf(-1));
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 가격이 금액 * 수량의 총합 보다 작다으면 메뉴를 등록할 수 없다.")
    @Test
    void create_under_total_price() {
        Menu menu = getMenuData();
        BigDecimal requestPirce = menu.getPrice().add(BigDecimal.valueOf(1));
        menu.setPrice(requestPirce);
        assertThatThrownBy(() -> menuService.create(menu))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[정상] 메뉴 가격을 변경한다.")
    @Test
    void changePrice_over_price() {
        Menu actual = menuService.create(getMenuData());
        actual.setPrice(new BigDecimal(20000));
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 메뉴 가격이 0보다 작으면, 메뉴 가격을 변경할 수 없다.")
    @Test
    void changePrice_under_0() {
        Menu actual = menuService.create(getMenuData());
        actual.setPrice(new BigDecimal(-1L));
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[오류] 메뉴 가격이 없으면, 메뉴 가격을 변경할 수 없다.")
    @Test
    void changePrice_is_null() {
        Menu actual = menuService.create(getMenuData());
        actual.setPrice(null);
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("[정상] 메뉴 가격을 작은 값으로 변경한다.")
    @Test
    void changePrice_under_price() {
        Menu menuRequest = menuService.create(getMenuData());
        menuRequest.setPrice(new BigDecimal(2000));
        Menu actual = menuService.changePrice(menuRequest.getId(), menuRequest);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menuRequest.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(menuRequest.getPrice()),
                () -> assertThat(actual.getMenuGroup()).isEqualTo(menuRequest.getMenuGroup()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(menuRequest.isDisplayed()),
                () -> assertThat(actual.getMenuProducts()).isEqualTo(menuRequest.getMenuProducts())
        );
    }

    @DisplayName("[정상] 메뉴를 공개할 수 있다.")
    @Test
    void display() {
        Menu menuRequest = menuService.create(getMenuData());
        Menu actual = menuService.display(menuRequest.getId());
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menuRequest.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(menuRequest.getPrice()),
                () -> assertThat(actual.getMenuGroup()).isEqualTo(menuRequest.getMenuGroup()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(true),
                () -> assertThat(actual.getMenuProducts()).isEqualTo(menuRequest.getMenuProducts())
        );
    }

    @DisplayName("[정상] 메뉴를 비공개한다.")
    @Test
    void hide() {
        Menu menuRequest = menuService.create(getMenuData());
        Menu actual = menuService.hide(menuRequest.getId());
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menuRequest.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(menuRequest.getPrice()),
                () -> assertThat(actual.getMenuGroup()).isEqualTo(menuRequest.getMenuGroup()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(false),
                () -> assertThat(actual.getMenuProducts()).isEqualTo(menuRequest.getMenuProducts())
        );
    }

    @DisplayName("[정상] 전체 메뉴를 조회한다.")
    @Test
    void findAll() {
        menuService.create(getMenuData());
        List<Menu> all = menuService.findAll();
        assertAll(
                () -> assertThat(all).isNotNull(),
                () -> assertThat(all.size()).isSameAs(1)
        );

    }

    private Menu getMenuData() {
        MenuGroup menuGroup = menuGroupRepository.save(DummyMenuGroup.createMenuGroup());
        Product productRequest = productRepository.save(DummyProduct.createProductRequest());
        List<MenuProduct> menuProducts = DummyMenuProduct.defaultMenuProducts(productRequest, 1L);
        Menu menu = DummyMenu.createMenu(menuGroup, menuProducts);
        return menu;
    }


}