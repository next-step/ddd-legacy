package kitchenpos.menu.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(int price) {
        Menu menu = new Menu();
        menu.setPrice(new BigDecimal(price));
        return menu;
    }

    public static Menu createMenu(int price, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(price);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenu(String name, int price, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(price, menuProducts);
        menu.setName(name);
        return menu;
    }

    public static Menu createMenu(String name, int price, boolean displayed, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = createMenu(name, price, menuProducts);
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        return menu;
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuProduct createMenuProduct(Product product, Long quantity, UUID productId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }
}
