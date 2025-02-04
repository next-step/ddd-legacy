package kitchenpos.testfixture;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;

public class TestFixture {

    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuProduct createMenuProduct(int quantity, Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static Menu createMenu(String name, BigDecimal price, MenuGroup menuGroup, MenuProduct menuProduct) {
        Menu menu = new Menu();
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName(name);
        menu.setPrice(price);
        menu.setId(UUID.randomUUID());
        menu.setMenuProducts(Collections.singletonList(menuProduct));
        menu.setDisplayed(true);
        return menu;
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests, boolean isOccupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(isOccupied);
        return orderTable;
    }

}
