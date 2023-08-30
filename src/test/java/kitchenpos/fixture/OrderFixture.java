package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.TEST_ORDER_TABLE;

public class OrderFixture {

    public static Order TEST_ORDER_EAT_IN(OrderStatus status) {
        return TEST_ORDER_EAT_IN(TEST_ORDER_TABLE(), TEST_ORDER_LINE_ITEM(), status);
    }

    public static Order TEST_ORDER_EAT_IN(OrderStatus status, Menu menu) {
        return TEST_ORDER_EAT_IN(TEST_ORDER_TABLE(), TEST_ORDER_LINE_ITEM(menu), status);
    }

    public static Order TEST_ORDER_EAT_IN(OrderTable table, OrderStatus status) {
        return TEST_ORDER_EAT_IN(table, TEST_ORDER_LINE_ITEM(), status);
    }

    public static Order TEST_ORDER_EAT_IN(OrderTable table, OrderLineItem orderLineItem, OrderStatus status) {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setStatus(status);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setOrderTable(table);
        order.setOrderTableId(table.getId());
        return order;
    }

    public static Order TEST_ORDER_DELIVERY(OrderStatus orderStatus) {
        return TEST_ORDER_DELIVERY(orderStatus, MenuFixture.TEST_MENU());
    }

    public static Order TEST_ORDER_DELIVERY(OrderStatus orderStatus, Menu menu) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("여기로 배달와줘");
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(TEST_ORDER_LINE_ITEM(menu)));
        order.setStatus(orderStatus);
        return order;
    }

    public static Order TEST_ORDER_TAKEOUT(OrderStatus orderStatus) {
        return TEST_ORDER_TAKEOUT(orderStatus, MenuFixture.TEST_MENU());
    }

    public static Order TEST_ORDER_TAKEOUT(OrderStatus orderStatus, Menu menu) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(TEST_ORDER_LINE_ITEM(menu)));
        order.setStatus(orderStatus);
        return order;
    }

    public static OrderLineItem TEST_ORDER_LINE_ITEM() {
        return TEST_ORDER_LINE_ITEM(MenuFixture.TEST_MENU());
    }

    public static OrderLineItem TEST_ORDER_LINE_ITEM(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        orderLineItem.setQuantity(1);
        return orderLineItem;
    }
}
