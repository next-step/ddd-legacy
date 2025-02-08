package kitchenpos.fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixtures {

    public static Product createProduct(final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static MenuProduct createMenuProduct(final UUID productId, final long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuGroup createMenuGroup(final String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }


    public static Menu createMenu(
        final String name,
        final BigDecimal price,
        final boolean displayed,
        final MenuGroup menuGroup,
        final List<MenuProduct> menuProducts
    ) {
        Menu menu  = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenuWithoutMenuGroupId(
        final String name,
        final BigDecimal price,
        final boolean displayed,
        final List<MenuProduct> menuProducts
    ) {
        Menu menu  = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(null);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
