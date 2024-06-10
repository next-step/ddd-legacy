package kitchenpos;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TestFixture {
    public static Menu createMenu(BigDecimal menuPrice, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuProducts(menuProducts);
        MenuGroup menuGroup = createMenuGroup();
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        return menu;
    }

    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static MenuProduct createMenuProductRequest(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static MenuGroup createMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("menuGroup");
        return menuGroup;
    }
}
