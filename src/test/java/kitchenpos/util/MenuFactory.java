package kitchenpos.util;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MenuFactory {
    private MenuFactory() {
    }

    public static Menu createMenu(UUID uuid, BigDecimal price, String name, boolean display, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(uuid);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(display);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenu(BigDecimal price, String name, boolean display, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(display);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        return menu;
    }


    public static Menu createMenuWithPrice(BigDecimal price) {
        Menu changeRequest = new Menu();
        changeRequest.setPrice(price);
        return changeRequest;
    }

    public static MenuProduct createMenuProductWithQuantity(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static List<MenuProduct> createMenuProducts(List<Product> products, int quantity) {
        List<MenuProduct> result = new ArrayList<>();
        for (Product product : products) {
            MenuProduct menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setProductId(product.getId());
            menuProduct.setQuantity(quantity);
            result.add(menuProduct);
        }
        return Collections.unmodifiableList(result);
    }
}
