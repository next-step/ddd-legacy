package kitchenpos.fixture.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(String menuName, MenuGroup menuGroup, List<MenuProduct> menuProducts, BigDecimal menuPrice) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(menuName);
        menu.setPrice(menuPrice);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    public static Menu createMenu(String menuName, MenuGroup menuGroup, BigDecimal menuPrice, boolean displayed, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(menuName);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProducts));
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        return menu;
    }

    public static Menu 커플_강정_후라이드_메뉴(MenuGroup menuGroup, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("커플 강정 + 후라이드");
        menu.setPrice(BigDecimal.valueOf(20_000));
        menu.setMenuProducts(List.of(menuProducts));
        menu.setDisplayed(true);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        return menu;
    }

    public static Menu withoutMenuGroup(String menuName, boolean displayed, BigDecimal menuPrice, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(menuName);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProducts));
        menu.setDisplayed(displayed);
        return menu;
    }

    public static Menu changePriceMenu(BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        return menu;
    }

    public static Menu 전시_메뉴(boolean displayed) {
        Menu menu = new Menu();
        menu.setDisplayed(displayed);
        return menu;
    }
}
