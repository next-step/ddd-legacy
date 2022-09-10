package kitchenpos.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;

public class MenuFixture {

    public static Menu createMenu() {
        Menu menu = new Menu();
        menu.setName("메뉴이름");
        menu.setPrice(BigDecimal.valueOf(2000));
        return menu;
    }

    public static Menu createMenu(String name) {
        Menu menu = createMenu();
        menu.setName(name);
        return menu;
    }

    public static Menu createMenu(BigDecimal price) {
        Menu menu = createMenu();
        menu.setPrice(price);
        return menu;
    }

    public static Menu createMenu(MenuGroup menuGroup) {
        Menu menu = createMenu();
        menu.setMenuGroup(menuGroup);
        return menu;
    }

    public static Menu createMenu(MenuGroup menuGroup, String menuName) {
        Menu menu = createMenu(menuGroup);
        menu.setName(menuName);
        return menu;
    }

}
