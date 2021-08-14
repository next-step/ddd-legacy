package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.fixture.MenuGroupFixture.MENU_GROUP_ONE_REQUEST;
import static kitchenpos.application.fixture.MenuProductFixture.MENU_PRODUCTS;

public class MenuFixture {
    private static final String MENU_NAME_ONE = "양념";
    private static final UUID UUID1 = UUID.randomUUID();

    private static final UUID UUID2 = UUID.randomUUID();

    private static final long MENU_PRICE_ONE = 15000L;

    public static Menu HIDE_MENU_REQUEST() {
        return createMenu(UUID1, MENU_NAME_ONE, MENU_PRICE_ONE, MENU_GROUP_ONE_REQUEST(), false, MENU_PRODUCTS());
    }

    public static Menu SHOW_MENU_REQUEST() {
        return createMenu(UUID2, MENU_NAME_ONE, MENU_PRICE_ONE, MENU_GROUP_ONE_REQUEST(), true, MENU_PRODUCTS());
    }

    private static Menu createMenu(final UUID uuid, final String menuName, final long price, final MenuGroup menuGroup, final boolean isDisplay, final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setId(uuid);
        menu.setName(menuName);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(isDisplay);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
