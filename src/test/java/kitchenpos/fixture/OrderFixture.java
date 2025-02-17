package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order createOrderByType(OrderType type) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        return order;
    }

    public static Order createOrderByStatus(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(orderStatus);
        return order;
    }

    public static Order createOrder(OrderType type, OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(orderStatus);
        return order;
    }

    public static Order createOrder(OrderType type, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createDeliveryOrder(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        return order;
    }

    public static Order createDeliveryOrder(String deliveryAddress) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order createDeliveryOrder(List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order createDeliveryOrder(OrderStatus orderStatus, List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order createEatInOrder(OrderType type, UUID orderTableId, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createTakeOutOrder(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.TAKEOUT);
        order.setStatus(orderStatus);
        return order;
    }
}
