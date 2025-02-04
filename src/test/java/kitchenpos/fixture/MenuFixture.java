package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static final String DEFAULT_MENU_NAME = "추억의 옛날 통닭";
    public static final String SECONDARY_MENU_NAME = "치킨 세트";
    public static final BigDecimal DEFAULT_MENU_PRICE = BigDecimal.valueOf(10_000);
    public static final BigDecimal SECONDARY_MENU_PRICE = BigDecimal.valueOf(28_000);
    public static final boolean DEFAULT_DISPLAYED = true;

    private MenuFixture() {
    }

    public static Menu menu(
            final UUID id,
            final String name,
            final BigDecimal price,
            final MenuGroup menuGroup,
            final List<MenuProduct> menuProducts,
            final boolean isDisplayed
    ) {
        final Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(isDisplayed);
        return menu;
    }

    public static Menu menu(final String name, final int price, final MenuGroup menuGroup, final List<MenuProduct> menuProducts, final boolean isDisplayed) {
        return menu(null, name, BigDecimal.valueOf(price), menuGroup, menuProducts, isDisplayed);
    }

    public static Menu menu(final String name, final BigDecimal price, MenuGroup menuGroup, final List<MenuProduct> menuProducts, final boolean isDisplayed) {
        return menu(null, name, price, menuGroup, menuProducts, isDisplayed);
    }

    public static UUID createMenuId() {
        return UUID.randomUUID();
    }

}
