package kitchenpos.fixture;

import kitchenpos.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    public static Order order(OrderType type, OrderStatus status, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        ReflectionTestUtils.setField(order, "type", type);
        ReflectionTestUtils.setField(order, "status", status);
        ReflectionTestUtils.setField(order, "orderDateTime", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "orderLineItems", orderLineItems);
        return order;
    }

    public static Order acceptedTakeoutOrder(List<OrderLineItem> orderLineItems) {
        return order(OrderType.TAKEOUT, OrderStatus.ACCEPTED, orderLineItems);
    }

    public static Order waitingTakeoutOrder(List<OrderLineItem> orderLineItems) {
        return order(OrderType.TAKEOUT, OrderStatus.WAITING, orderLineItems);
    }

    public static Order servedDeliveryOrder(List<OrderLineItem> orderLineItems) {
        return order(OrderType.DELIVERY, OrderStatus.SERVED, orderLineItems);
    }

    public static Order deliveringDeliveryOrder(List<OrderLineItem> orderLineItems) {
        return order(OrderType.DELIVERY, OrderStatus.DELIVERING, orderLineItems);
    }

    public static Order deliveryOrder(String address, List<OrderLineItem> orderLineItems) {
        Order order = order(OrderType.DELIVERY, OrderStatus.WAITING, orderLineItems);
        ReflectionTestUtils.setField(order, "deliveryAddress", address);
        return order;
    }

    public static Order eatInOrder(UUID orderTableId, List<OrderLineItem> orderLineItems) {
        Order order = order(OrderType.EAT_IN, OrderStatus.WAITING, orderLineItems);
        ReflectionTestUtils.setField(order, "orderTableId", orderTableId);
        return order;
    }

    public static Order takeoutOrder(List<OrderLineItem> orderLineItems) {
        return order(OrderType.TAKEOUT, OrderStatus.WAITING, orderLineItems);
    }

    public static OrderLineItem orderLineItem(Menu menu, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        ReflectionTestUtils.setField(orderLineItem, "menuId", menu.getId());
        ReflectionTestUtils.setField(orderLineItem, "menu", menu);
        ReflectionTestUtils.setField(orderLineItem, "quantity", quantity);
        ReflectionTestUtils.setField(orderLineItem, "price", menu.getPrice());
        return orderLineItem;
    }

    public static OrderLineItem orderLineItem(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        ReflectionTestUtils.setField(orderLineItem, "menuId", menuId);
        ReflectionTestUtils.setField(orderLineItem, "quantity", quantity);
        ReflectionTestUtils.setField(orderLineItem, "price", price);
        return orderLineItem;
    }
}
