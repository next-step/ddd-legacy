package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static final String CHICKEN_SET_MENU = "양념 후라이드 세트";
    public static final BigDecimal CHICKEN_SET_MENU_PRICE = BigDecimal.valueOf(30_000L);

    private MenuFixture() {
    }

    public static Menu menu(final MenuGroup menuGroup, final List<MenuProduct> menuProducts) {
        return menu(createMenuId(), CHICKEN_SET_MENU, CHICKEN_SET_MENU_PRICE, menuGroup, menuProducts, true);
    }

    public static Menu menu(final MenuGroup menuGroup, final List<MenuProduct> menuProducts, final boolean isDisplayed) {
        return menu(createMenuId(), CHICKEN_SET_MENU, CHICKEN_SET_MENU_PRICE, menuGroup, menuProducts, isDisplayed);
    }

    public static Menu menu(final UUID id, final String name, final BigDecimal price,
                            final MenuGroup menuGroup, final List<MenuProduct> menuProducts, final boolean isDisplayed) {
        final Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(isDisplayed);
        return menu;
    }

    public static UUID createMenuId() {
        return UUID.randomUUID();
    }
}
