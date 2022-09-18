package kitchenpos.factory;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class MenuFactory {
    static final Product 황금올리브 = ProductFactory.of("황금올리브", 20000L);
    static final Product 호가든 = ProductFactory.of("호가든", 5000L);
    public static final String DEFAULT_MENU_NAME = "치맥세트";
    public static final long DEFAULT_MENU_PRICE = 28000L;

    public static Menu getDefaultMenu(MenuGroup createMenuGroup, List<MenuProduct> menuProducts) {
        return MenuFactory.of(createMenuGroup, menuProducts, DEFAULT_MENU_PRICE);
    }

    public static Menu of(MenuGroup createMenuGroup, long price) {
        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName(DEFAULT_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuProducts(List.of(MenuProductFactory.of(황금올리브), MenuProductFactory.of(호가든, 2)));
        return menu;
    }

    public static Menu of(MenuGroup createMenuGroup, List<MenuProduct> menuProducts, String name) {
        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(DEFAULT_MENU_PRICE));
        menu.setMenuProducts(menuProducts);
        return menu;
    }


    public static Menu of(MenuGroup createMenuGroup, List<MenuProduct> menuProducts, long price) {
        return MenuFactory.of(createMenuGroup, menuProducts, BigDecimal.valueOf(price));
    }

    public static Menu of(MenuGroup createMenuGroup, List<MenuProduct> menuProducts, BigDecimal price) {
        final Menu menu = new Menu();
        menu.setMenuGroupId(createMenuGroup.getId());
        menu.setName(DEFAULT_MENU_NAME);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu of(List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setMenuGroupId(null);
        menu.setName(DEFAULT_MENU_NAME);
        menu.setPrice(BigDecimal.valueOf(DEFAULT_MENU_PRICE));
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu of(MenuGroup createMenuGroup) {
        return MenuFactory.of(createMenuGroup, DEFAULT_MENU_PRICE);
    }
}
