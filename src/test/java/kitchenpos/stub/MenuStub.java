package kitchenpos.stub;

import kitchenpos.domain.Menu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static kitchenpos.stub.MenuGroupStub.generateFirstTestMenuGroup;
import static kitchenpos.stub.MenuProductStub.generateTestMenuProducts;

public class MenuStub {

    public static final String FIVE_THOUSAND_VISIBLE_MENU_NAME = "5000원보이는메뉴";

    private MenuStub() {
    }

    public static Menu generateFiveThousandPriceVisibleValidPriceMenu() {
        Menu menu = new Menu();
        menu.setMenuGroup(generateFirstTestMenuGroup());
        menu.setMenuProducts(generateTestMenuProducts());
        menu.setDisplayed(true);
        menu.setName(FIVE_THOUSAND_VISIBLE_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(5000));
        return menu;
    }

    public static Menu generateFiveThousandPriceVisibleInValidPriceMenu() {
        Menu menu = new Menu();
        menu.setMenuGroup(generateFirstTestMenuGroup());
        menu.setMenuProducts(generateTestMenuProducts());
        menu.setDisplayed(true);
        menu.setName(FIVE_THOUSAND_VISIBLE_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(9000));
        return menu;
    }

    public static List<Menu> generateSingleSizeValidPriceTestMenus() {
        List<Menu> menus = new ArrayList<>();
        menus.add(generateFiveThousandPriceVisibleValidPriceMenu());
        return menus;
    }
    public static List<Menu> generateSingleSizeInValidPriceTestMenus() {
        List<Menu> menus = new ArrayList<>();
        menus.add(generateFiveThousandPriceVisibleInValidPriceMenu());
        return menus;
    }


}
