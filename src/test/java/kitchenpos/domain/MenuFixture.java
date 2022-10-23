package kitchenpos.domain;

import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.Price;
import kitchenpos.menu.menugroup.domain.MenuGroup;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(MenuGroup menuGroup, List<MenuProduct> menuProducts, Price price) {
        return new Menu(menuGroup, menuProducts, price);
    }

    public static MenuGroup createMenuGroup(UUID id, String menuGroupName) {
        return new MenuGroup(id, new Name(menuGroupName, false));
    }

    public static List<MenuProduct> createMenuProducts(final MenuProduct... menuProducts) {
        return Arrays.asList(menuProducts);
    }
}
