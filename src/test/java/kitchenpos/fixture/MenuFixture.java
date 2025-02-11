package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.TestConstant.*;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;

public class MenuFixture {
    private MenuFixture() {
    }

    public static Menu createMenu() {
        return createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, createMenuGroup(),
                true, List.of(createMenuProduct()), 후라이드치킨_MENU_GROUP_UUID);
    }

    public static Menu createMenu(BigDecimal price) {
        return createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, price, createMenuGroup(),
                true, List.of(createMenuProduct()), 후라이드치킨_MENU_GROUP_UUID);
    }

    public static Menu createMenu(final UUID id, final String name, final BigDecimal price, final MenuGroup menuGroup, final List<MenuProduct> menuProducts, final UUID menugroupId) {
        return createMenu(id, name, price, menuGroup, true, menuProducts, menugroupId);
        }

    public static Menu createMenu(final UUID id, final String name, final BigDecimal price, final MenuGroup menuGroup, final boolean isDisplayed, final List<MenuProduct> menuProducts, final UUID menugroupId) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(isDisplayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menugroupId);
        return menu;
    }

    public static Menu createMenu(final UUID id, final String name, final BigDecimal price, final MenuGroup menuGroup, final UUID menugroupId) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(createMenuProduct()));
        menu.setMenuGroupId(menugroupId);
        return menu;
    }

    public static Menu createMenu(UUID id, String name, BigDecimal price) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        return menu;
    }

    public static Menu createMenu(MenuGroup menuGroup, MenuProduct menuProducts) {
        Menu menu = new Menu();
        menu.setId(후라이드치킨_MENU_UUID);
        menu.setName(후라이드치킨_MENU_NAME);
        menu.setPrice(후라이드치킨_DEFAULT_PRICE);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(List.of(menuProducts));
        menu.setMenuGroupId(후라이드치킨_MENU_GROUP_UUID);
        return menu;
    }

    public static Menu createMenu(final BigDecimal price, final boolean displayed, final MenuProduct menuProducts) {
        Menu menu = new Menu();
        menu.setId(후라이드치킨_MENU_UUID);
        menu.setName(후라이드치킨_MENU_NAME);
        menu.setPrice(price);
        menu.setMenuGroup(createMenuGroup());
        menu.setDisplayed(displayed);
        menu.setMenuProducts(Arrays.asList(menuProducts));
        return menu;
    }

}
