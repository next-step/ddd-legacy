package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

    public static Menu createMenu() {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(16_000L));
        menu.setDisplayed(true);
        return menu;
    }

    public static Menu createMenu(boolean display) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(16_000L));
        menu.setDisplayed(display);
        return menu;
    }

    public static Menu createMenu(boolean display, List<MenuProduct> menuProduct) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(16_000L));
        menu.setDisplayed(display);
        menu.setMenuProducts(menuProduct);
        return menu;
    }

    public static Menu createMenu(BigDecimal price, List<MenuProduct> menuProduct) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(price);
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProduct);
        return menu;
    }

    public static Menu createMenu(List<MenuProduct> menuProduct) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(16_000L));
        menu.setDisplayed(true);
        menu.setMenuProducts(menuProduct);
        return menu;
    }
}
