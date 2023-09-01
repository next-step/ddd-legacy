package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;

public class MenuFixture {

    public static Menu createMenu(MenuGroup menuGroup,
                                  String name,
                                  Integer price,
                                  List<MenuProduct> menuProducts,
                                  boolean displayed) {

        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price == null ? null : new BigDecimal(price));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenu() {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                1000,
                List.of(MenuProductFixture.createMenuProduct()),
                true
        );
    }

    public static Menu createMenuWithDisplayed(boolean displayed) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                1000,
                List.of(MenuProductFixture.createMenuProduct()),
                displayed
        );
    }

    public static Menu createMenuWithPrice(Integer price) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                price,
                List.of(MenuProductFixture.createMenuProduct()),
                true
        );
    }

    public static Menu createMenuWithMenuProducts(List<MenuProduct> menuProducts) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                1000,
                menuProducts,
                true
        );
    }

    public static Menu createMenuWithMenuProductsAndPrice(List<MenuProduct> menuProducts, Integer price) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                price,
                menuProducts,
                true
        );
    }

    public static Menu createMenuWithMenuProductsAndPriceAndDisplayed(
            List<MenuProduct> menuProducts, Integer price, boolean displayed) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                price,
                menuProducts,
                displayed
        );
    }

    public static Menu createMenuWithMenuProductsAndPriceAndName(String name, List<MenuProduct> menuProducts, Integer price) {

        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                name,
                price,
                menuProducts,
                true
        );
    }
}
