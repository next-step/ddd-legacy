package kitchenpos.fixture;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static Order createOrder(OrderStatus status) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(status);
        return order;
    }

    public static Order createOrder(
        OrderStatus orderStatus,
        OrderType orderType,
        List<kitchenpos.domain.OrderLineItem> orderLineItem
    ) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(orderStatus);
        order.setType(orderType);
        order.setOrderLineItems(orderLineItem);
        return order;
    }

    public static Order createOrderRequest(
        OrderType type, List<OrderLineItem> orderLineItem) {
        Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(orderLineItem);
        return order;
    }

    public static Order createEatInOrderRequest(List<OrderLineItem> orderLineItem) {
        return createOrderRequest(OrderType.EAT_IN, orderLineItem);
    }

    public static Order createDeliveryOrderRequest(List<OrderLineItem> orderLineItem) {
        return createDeliveryOrderRequest(orderLineItem, "서울시 강남구");
    }

    public static Order createDeliveryOrderRequest(
        List<OrderLineItem> orderLineItem,
        String deliveryAddress
    ) {
        Order order = createOrderRequest(OrderType.DELIVERY, orderLineItem);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order createOrder(
        OrderStatus orderStatus,
        OrderType orderType,
        OrderTable orderTable
    ) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(orderStatus);
        order.setType(orderType);
        order.setOrderTable(orderTable);
        return order;
    }
}
