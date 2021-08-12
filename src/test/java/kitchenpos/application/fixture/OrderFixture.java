package kitchenpos.application.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class OrderFixture {
    public static Order generateOrder() {
        return generateOrder(OrderType.EAT_IN);
    }

    public static Order generateOrder(OrderType type) {
        MenuGroup menuGroup = MenuFixture.generateMenuGroup();
        Product product01 = ProductFixture.generateProduct("양념치킨", 12_000L);
        Product product02 = ProductFixture.generateProduct("후라이드", 11_000L);
        Product product03 = ProductFixture.generateProduct("간장치킨", 11_000L);
        List<MenuProduct> menuProducts01 = MenuFixture.generateMenuProducts(product01, product02);
        List<MenuProduct> menuProducts02 = MenuFixture.generateMenuProducts(product02, product03);
        Menu menu01 = MenuFixture.generateMenu(menuGroup, menuProducts01);
        Menu menu02 = MenuFixture.generateMenu(menuGroup, menuProducts02);
        OrderLineItem orderLineItem01 = OrderFixture.generateOrderLineItem(menu01, 2, menu01.getPrice());
        OrderLineItem orderLineItem02 = OrderFixture.generateOrderLineItem(menu02, 1, menu02.getPrice());
        List<OrderLineItem> orderLineItems = OrderFixture.generateOrderLineItems(orderLineItem01, orderLineItem02);
        OrderTable orderTable = OrderTableFixture.generateOccupiedOrderTable();
        if (type == OrderType.EAT_IN) {
            return OrderFixture.generateOrder(OrderType.EAT_IN, orderLineItems, orderTable);
        }
        if (type == OrderType.DELIVERY) {
            return OrderFixture.generateOrder(OrderType.DELIVERY, orderLineItems, "서울 강남구");
        }
        return OrderFixture.generateOrder(OrderType.TAKEOUT, orderLineItems);
    }

    public static Order generateOrder(OrderType type, List<OrderLineItem> orderLineItems, String address) {
        return generateOrder(type, orderLineItems, address, null);
    }

    public static Order generateOrder(OrderType type, List<OrderLineItem> orderLineItems, OrderTable orderTable) {
        return generateOrder(type, orderLineItems, null, orderTable);
    }

    public static Order generateOrder(OrderType type, List<OrderLineItem> orderLineItems) {
        return generateOrder(type, orderLineItems, null, null);
    }

    public static Order generateOrder(OrderType type, List<OrderLineItem> orderLineItems, String address, OrderTable orderTable) {
        Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(address);
        order.setOrderTable(orderTable);
        return order;
    }

    public static List<OrderLineItem> generateOrderLineItems(OrderLineItem... orderLineItems) {
        return Arrays.asList(orderLineItems);
    }

    public static OrderLineItem generateOrderLineItem(Menu menu, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
