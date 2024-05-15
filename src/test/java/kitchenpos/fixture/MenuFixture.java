package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(10_000);
    private static final String DEFAULT_MENU_NAME = "메뉴";
    private static final String DEFAULT_GROUP_NAME = "메뉴그룹";

    private MenuFixture() {
    }

    public static Menu 기본_메뉴() {
        return 메뉴_생성(DEFAULT_PRICE, 기본_메뉴_그룹(), List.of(MenuProductFixture.기본_메뉴_상품()), DEFAULT_MENU_NAME);
    }

    public static Menu 그룹_없는_메뉴() {
        return 메뉴_생성(DEFAULT_PRICE, null, List.of(MenuProductFixture.기본_메뉴_상품()), DEFAULT_MENU_NAME);
    }

    public static Menu 메뉴_생성(BigDecimal price) {
        return 메뉴_생성(price, 기본_메뉴_그룹(), List.of(MenuProductFixture.기본_메뉴_상품()), DEFAULT_MENU_NAME);
    }

    public static Menu 메뉴_생성(String name) {
        return 메뉴_생성(DEFAULT_PRICE, 기본_메뉴_그룹(), List.of(MenuProductFixture.기본_메뉴_상품()), name);
    }

    public static Menu 메뉴_생성(List<MenuProduct> menuProducts) {
        return 메뉴_생성(DEFAULT_PRICE, 기본_메뉴_그룹(), menuProducts, DEFAULT_MENU_NAME);
    }

    public static Menu 메뉴_생성(BigDecimal price, List<MenuProduct> menuProducts) {
        return 메뉴_생성(price, 기본_메뉴_그룹(), menuProducts, DEFAULT_MENU_NAME);
    }

    public static Menu 메뉴_생성(BigDecimal price, MenuGroup menuGroup, List<MenuProduct> menuProducts, String name) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);

        return menu;
    }

    public static MenuGroup 기본_메뉴_그룹() {
        return 메뉴_그룹_생성(DEFAULT_GROUP_NAME);
    }

    public static MenuGroup 메뉴_그룹_생성(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);

        return menuGroup;
    }
}
