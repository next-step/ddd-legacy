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

    @DisplayName("메뉴를 등록한다.")
    @Test
    void create() {
        Menu menu = getMenuData();
        Menu actual = menuService.create(menu);
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(menu.getName()),
                () -> assertThat(actual.getPrice()).isEqualTo(menu.getPrice()),
                () -> assertThat(actual.getMenuGroup()).isEqualTo(menu.getMenuGroup()),
                () -> assertThat(actual.isDisplayed()).isEqualTo(menu.isDisplayed())
        );
    }

    @DisplayName("메뉴 가격을 변경한다.")
    @Test
    void changePrice_oer_price() {
        Menu actual = menuService.create(getMenuData());
        actual.setPrice(new BigDecimal(20000));
        assertThatThrownBy(() -> menuService.changePrice(actual.getId(), actual))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("메뉴 가격을 작은 값으로 변경한다.")
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

    @DisplayName("메뉴를 노출한다.")
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

    @DisplayName("메뉴를 숨긴다.")
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

    @DisplayName("전체 메뉴를 조회한다.")
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