package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.ProductFixture.createProduct;

public class MenuFixture {

    private static final UUID MENU_ID = UUID.randomUUID();
    private static final UUID MENU_GROUP_ID = UUID.randomUUID();
    private static final String MENU_NAME = "name";
    private static final BigDecimal MENU_PRICE = BigDecimal.ONE;

    public static Menu createMenu() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(createProduct());
        menuProduct.setQuantity(1);

        Menu menu = new Menu();
        menu.setId(MENU_ID);
        menu.setName(MENU_NAME);
        menu.setPrice(MENU_PRICE);
        menu.setMenuGroup(createMenuGroup());
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProduct));
        menu.setMenuGroupId(MENU_GROUP_ID);

        return menu;
    }

    public static Menu createMenuWithName(final String name) {
        Menu menu = createMenu();
        menu.setName(name);

        return menu;
    }

    public static Menu createMenuWithPrice(final BigDecimal price) {
        Menu menu = createMenu();
        menu.setPrice(price);

        return menu;
    }

    public static Menu createMenuWithMenuProducts(final List<MenuProduct> menuProducts) {
        Menu menu = createMenu();
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static Menu createMenuWithDisplayed(final boolean displayed) {
        Menu menu = createMenu();
        menu.setDisplayed(displayed);

        return menu;
    }

    public static List<Menu> createMenus() {
        return List.of(createMenu(), createMenu());
    }

}
