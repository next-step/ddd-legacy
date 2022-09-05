package kitchenpos.unit;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.List;

public class Fixtures {

    public static MenuGroup aMenuGroup() {
        return new MenuGroup("한마리치킨");
    }

    public static Product aChickenProduct(int price) {
        return aProduct("후라이드 치킨", price);
    }

    public static Product aProduct(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    static MenuProduct aChickenMenuProduct(int price, int quantity) {
        return aMenuProduct(aChickenProduct(price), quantity);
    }

    public static MenuProduct aMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    static Menu aManWonChickenMenu(int price) {
        return aMenu("후라이드 치킨", price, aChickenMenuProduct(1, 10_000));
    }

    public static Menu aMenu(String name, int price, MenuProduct... menuProducts) {
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
        oli.setPrice(menu.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return oli;
    }

    static OrderTable anOrderTable(boolean occupied) {
        return anOrderTable("1번 테이블", occupied);
    }

    static OrderTable anOrderTable(String name, boolean occupied) {
        return anOrderTable(name, occupied, 0);
    }

    static OrderTable anOrderTable(String name, boolean occupied, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    static Order aDeliveryOrder(String deliveryAddress, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(List.of(orderLineItems));
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    static Order anEatInOrder(OrderTable orderTable, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(List.of(orderLineItems));
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }
}
