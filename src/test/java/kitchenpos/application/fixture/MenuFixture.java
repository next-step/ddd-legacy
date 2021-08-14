package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.MenuProductFixture.CHEAP_MENU_PRODUCTS;
import static kitchenpos.application.fixture.MenuProductFixture.MENU_PRODUCTS;
import static kitchenpos.application.fixture.MenuProductFixture.QUANTITY_NAGATIVE_MENU_PRODUCTS;
import static kitchenpos.application.fixture.MenuProductFixture.WRONG_PRODUCTS;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

    private static final String MENU_NAME1 = "후라이드";
    private static final String MENU_NAME2 = "양념";
    private static final long PRICE1 = 20000L;
    private static final long PRICE2 = 30000L;
    private static final long EXPENSIVE_PRICE = 2000000L;
    private static final long NEGATIVE_PRICE = -10000L;
    private static final UUID UUID1 = UUID.randomUUID();
    private static final UUID UUID2 = UUID.randomUUID();
    private static final UUID HIDED_UUID1 = UUID.randomUUID();
    private static final UUID MENU_GROUP_ID1 = UUID.randomUUID();
    private static final UUID MENU_GROUP_ID2 = UUID.randomUUID();

    public static Menu MENU1_REQUEST() {
        return createMenu(null, MENU_NAME1, PRICE1, MENU_GROUP_ID1, true, MENU_PRODUCTS());
    }

    public static Menu MENU1_REQUEST(final UUID menuGroupId) {
        return createMenu(null, MENU_NAME1, PRICE1, menuGroupId, true, MENU_PRODUCTS());
    }

    public static Menu MENU1_REQUEST_WRONG_PRODUCTS(final UUID menuGroupId) {
        return createMenu(null, MENU_NAME1, PRICE1, menuGroupId, true, WRONG_PRODUCTS());
    }

    public static Menu PRICE_NULL_MENU_REQUEST() {
        return createMenu(null, MENU_NAME1, null, MENU_GROUP_ID2, true, MENU_PRODUCTS());
    }

    public static Menu PRICE_NEGATIVE_MENU_REQUEST() {
        return createMenu(null, MENU_NAME1, NEGATIVE_PRICE, MENU_GROUP_ID2, true, MENU_PRODUCTS());
    }

    public static Menu EMPTY_MENUPRODUCTS_MENU_REQUEST(final UUID menuGroupId) {
        return createMenu(null, MENU_NAME1, PRICE1, menuGroupId, true, Collections.emptyList());
    }

    public static Menu QUANTITY_NAGATIVE_MENU_REQUEST(final UUID menuGroupId) {
        return createMenu(null, MENU_NAME1, EXPENSIVE_PRICE, menuGroupId, true, QUANTITY_NAGATIVE_MENU_PRODUCTS());
    }

    public static Menu QUANTITY_NAGATIVE_MENU_REQUEST() {
        return createMenu(null, MENU_NAME1, EXPENSIVE_PRICE, MENU_GROUP_ID1, true, QUANTITY_NAGATIVE_MENU_PRODUCTS());
    }

    public static Menu EXPENSIVE_MENU_REQUEST() {
        return createMenu(null, MENU_NAME1, EXPENSIVE_PRICE, MENU_GROUP_ID1, true, MENU_PRODUCTS());
    }

    public static Menu EXPENSIVE_MENU_REQUEST(final UUID menuGroupId) {
        return createMenu(null, MENU_NAME1, EXPENSIVE_PRICE, menuGroupId, true, MENU_PRODUCTS());
    }

    public static Menu MENU_WITH_NAME_REQUEST(final String name) {
        return createMenu(null, name, PRICE2, MENU_GROUP_ID2, true, MENU_PRODUCTS());
    }

    public static Menu MENU_WITH_NAME_REQUEST(final String name, final UUID menuGroupId) {
        return createMenu(null, name, PRICE2, menuGroupId, true, MENU_PRODUCTS());
    }

    public static Menu MENU_WITH_PRICE_REQUEST(final long price) {
        return createMenu(null, MENU_NAME2, price, MENU_GROUP_ID2, true, MENU_PRODUCTS());
    }

    public static Menu MENU_WITH_PRICE_REQUEST(final long price, final UUID menuGroupId) {
        return createMenu(null, MENU_NAME2, price, menuGroupId, true, MENU_PRODUCTS());
    }

    public static Menu MENU1() {
        return createMenu(UUID1, MENU_NAME1, PRICE1, MENU_GROUP_ID1, true, MENU_PRODUCTS());
    }

    public static Menu MENU2() {
        return createMenu(UUID2, MENU_NAME2, PRICE2, MENU_GROUP_ID2, true, MENU_PRODUCTS());
    }

    public static Menu HIDED_MENU() {
        return createMenu(HIDED_UUID1, MENU_NAME1, PRICE1, MENU_GROUP_ID1, false, MENU_PRODUCTS());
    }

    public static Menu CHEAP_PRODUCT_MENU() {
        return createMenu(UUID1, MENU_NAME1, PRICE1, MENU_GROUP_ID1, true, CHEAP_MENU_PRODUCTS());
    }

    public static Menu CHEAP_PRODUCT_MENU2() {
        return createMenu(UUID2, MENU_NAME2, PRICE2, MENU_GROUP_ID2, true, CHEAP_MENU_PRODUCTS());
    }

    public static List<Menu> MENUS() {
        return Arrays.asList(MENU1(), MENU2());
    }

    private static Menu createMenu(final UUID uuid, final String menuName, final long price, final UUID menuGroupId, final boolean isDisplay, final List<MenuProduct> menuProducts) {
        return createMenu(uuid, menuName, BigDecimal.valueOf(price), menuGroupId, isDisplay, menuProducts);
    }

    private static Menu createMenu(final UUID uuid, final String menuName, final BigDecimal price, final UUID menuGroupId, final boolean isDisplay, final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setId(uuid);
        menu.setName(menuName);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(isDisplay);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

}
