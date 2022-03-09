package kitchenpos.util;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFactory {
    private MenuFactory() {
    }

    public static Menu createMenu(UUID uuid, int price, String name, boolean display, MenuGroup menuGroup, List<MenuProduct> products) {
        Menu menu = new Menu();
        menu.setId(uuid);
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(display);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(products);
        return menu;
    }

    public static MenuProduct createMenuProduct(Product saved) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(saved);
        menuProduct.setProductId(saved.getId());
        return menuProduct;
    }
}
