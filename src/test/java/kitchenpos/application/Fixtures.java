package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class Fixtures {

    static MenuGroup aMenuGroup() {
        return new MenuGroup("한마리치킨");
    }

    static Product aProduct(int price) {
        Product product = new Product();
        product.setName("후라이드 치킨");
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    static MenuProduct aMenuProduct(int price, int quantity) {
        return aMenuProduct(aProduct(price), quantity);
    }

    static MenuProduct aMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    static Menu aMenu(String name, int price, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuProducts(List.of(menuProducts));
        menu.setDisplayed(true);
        menu.setMenuGroup(aMenuGroup());
        menu.setMenuGroupId(aMenuGroup().getId());
        return menu;
    }

    static OrderLineItem anOrderLineItem(Menu menu, int quantity) {
        OrderLineItem oli = new OrderLineItem();
        oli.setMenu(menu);
        oli.setQuantity(quantity);
        return oli;
    }

    static OrderTable anOrderTable(boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    static Order anOrder(OrderType orderType) {
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(10_000));

        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(Collections.singletonList(oli));
        order.setDeliveryAddress("서울시 어딘가");

        return order;
    }
}
