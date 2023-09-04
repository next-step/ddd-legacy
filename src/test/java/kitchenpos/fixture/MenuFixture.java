package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

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

    public static Menu createMenuWithProductAndMenuQuantity(Product product, long menuQuantity) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                1000,
                List.of(MenuProductFixture.createMenuProduct(product, menuQuantity)),
                true
        );
    }

    public static Menu createMenuWithProductPriceAndMenuQuantityAndPrice(Integer productPrice, long menuQuantity, Integer menuPrice) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                menuPrice,
                List.of(MenuProductFixture.createMenuProduct(
                        ProductFixture.createProductWithPrice(productPrice), menuQuantity)),
                true
        );
    }

    public static Menu createMenuWithProductAndMenuQuantityAndPrice(Product product, long menuQuantity, Integer price) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                "후라이드 한마리",
                price,
                List.of(MenuProductFixture.createMenuProduct(product, menuQuantity)),
                true
        );
    }

    public static Menu createMenuWithProductAndNameAndPriceAndMenuQuantity(
            Product product, String name, Integer productPrice, long menuQuantity) {
        return createMenu(
                MenuGroupFixture.createMenuGroup(),
                name,
                productPrice,
                List.of(MenuProductFixture.createMenuProduct(product, menuQuantity)),
                true
        );
    }
}
