package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createDisplayedMenu(String name, BigDecimal price) {
        final var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setName(name);
        return menu;
    }

    public static Menu createDisplayedMenu(String name, BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProducts) {
        final var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    public static Menu createMenu(String name, BigDecimal price, boolean isDisplayed) {
        final var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setName(name);
        menu.setDisplayed(isDisplayed);
        return menu;
    }

    public static Menu createMenu(BigDecimal price, List<MenuProduct> menuProducts, boolean isDisplayed) {
        final var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(isDisplayed);
        return menu;
    }

    public static Menu createMenu(boolean isDisplayed) {
        final var menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(isDisplayed);
        return menu;
    }
}
