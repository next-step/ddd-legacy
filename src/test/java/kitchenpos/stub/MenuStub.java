package kitchenpos.stub;

import kitchenpos.domain.Menu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static kitchenpos.stub.MenuGroupStub.generateFirstTestMenuGroup;
import static kitchenpos.stub.MenuProductStub.generateNegativeQuantityMenuProduct;
import static kitchenpos.stub.MenuProductStub.generateTestMenuProducts;

public class MenuStub {

    public static final String FIVE_THOUSAND_VISIBLE_MENU_NAME = "5000원보이는메뉴";
    public static final String FIVE_THOUSAND_INVISIBLE_MENU_NAME = "5000원안보이는메뉴";
    public static final String FOUR_THOUSAND_VISIBLE_MENU_NAME = "4000원보이는메뉴";
    public static final String NINE_THOUSAND_VISIBLE_MENU_NAME = "9000원보이는메뉴";

    private MenuStub() {
    }

    public static Menu generateFiveThousandMenuProductsPriceVisibleSamePriceMenu() {
        Menu menu = new Menu();
        menu.setMenuGroup(generateFirstTestMenuGroup());
        menu.setMenuProducts(generateTestMenuProducts());
        menu.setDisplayed(true);
        menu.setName(FIVE_THOUSAND_VISIBLE_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(5000));
        return menu;
    }

    public static Menu generateFiveThousandMenuProductsPriceVisibleSmallerPriceMenu() {
        Menu menu = new Menu();
        menu.setMenuGroup(generateFirstTestMenuGroup());
        menu.setMenuProducts(generateTestMenuProducts());
        menu.setDisplayed(true);
        menu.setName(FOUR_THOUSAND_VISIBLE_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(4000));
        return menu;
    }

    public static Menu generateFiveThousandMenuProductsPriceInVisibleSamePriceMenu() {
        Menu menu = new Menu();
        menu.setMenuGroup(generateFirstTestMenuGroup());
        menu.setMenuProducts(generateTestMenuProducts());
        menu.setDisplayed(false);
        menu.setName(FIVE_THOUSAND_INVISIBLE_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(5000));
        return menu;
    }

    public static Menu generateNineThousandMenuProductsPriceVisibleLargerPriceMenu() {
        Menu menu = new Menu();
        menu.setMenuGroup(generateFirstTestMenuGroup());
        menu.setMenuProducts(generateTestMenuProducts());
        menu.setDisplayed(true);
        menu.setName(NINE_THOUSAND_VISIBLE_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(9000));
        return menu;
    }

    public static Menu generateNegativePriceMenu() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(-1000));
        return menu;
    }

    public static Menu generateEmptyMenuProductsMenu() {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(1000));
        return menu;
    }

    public static Menu generateContainingNegativeQuantityMenuProductMenu() {
        Menu menu = new Menu();
        menu.setMenuGroup(generateFirstTestMenuGroup());
        menu.setMenuProducts(Collections.singletonList(generateNegativeQuantityMenuProduct()));
        menu.setPrice(BigDecimal.valueOf(0));
        return menu;
    }

    public static List<Menu> generateSingleSizeValidPriceTestMenus() {
        List<Menu> menus = new ArrayList<>();
        menus.add(generateFiveThousandMenuProductsPriceVisibleSamePriceMenu());
        return menus;
    }
    public static List<Menu> generateSingleSizeInValidPriceTestMenus() {
        List<Menu> menus = new ArrayList<>();
        menus.add(generateNineThousandMenuProductsPriceVisibleLargerPriceMenu());
        return menus;
    }


}
