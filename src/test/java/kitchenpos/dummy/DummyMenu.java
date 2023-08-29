package kitchenpos.dummy;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class DummyMenu {

    public static Menu createMenu(String name, BigDecimal price, boolean displayed, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenu(String name, BigDecimal price, boolean displayed, MenuGroup menuGroup) {
        return createMenu(name, price, displayed, menuGroup, List.of());
    }

    public static Menu createMenu(String name, BigDecimal price, boolean displayed) {
        return createMenu(name, price, displayed, DummyMenuGroup.defaultMenuGroup());
    }

    public static Menu createMenu(String name, BigDecimal price) {
        return createMenu(name, price, true);
    }
    public static Menu createMenu(String name) {
        return createMenu(name, new BigDecimal(10000));
    }

    public static Menu createMenu(MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = defaultMenu();
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu defaultMenu() {
        return createMenu(
                "기본 메뉴",
                new BigDecimal(10000),
                false,
                DummyMenuGroup.defaultMenuGroup(),
                List.of(DummyMenuProduct.defaultMenuProduct())
        );
    }
}
