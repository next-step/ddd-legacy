package kitchenpos;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class FixtureFactory {


    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Menu createMenu(String name, BigDecimal price, boolean display, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(display);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static List<MenuProduct> createMenuProducts() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        menuProduct.setQuantity(2);
        Product product = FixtureFactory.createProduct("양념 치킨", BigDecimal.valueOf(16000));
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());

        return List.of(menuProduct);


    }
}
