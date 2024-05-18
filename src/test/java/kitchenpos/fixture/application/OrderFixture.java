package kitchenpos.fixture.application;

import kitchenpos.domain.*;

import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order createOrderRequest(OrderType type, OrderStatus status, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(type);
        order.setStatus(status);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createEatInOrderRequest(OrderTable orderTable, UUID orderTableId, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static Order createEatInOrderResponse(OrderTable orderTable, UUID orderTableId, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static Order createDeliveryOrderRequest(String deliveryAddress, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static Order createDeliveryOrderResponse(String deliveryAddress, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static Order createTakeOutOrderRequest(OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static Order createTakeOutOrderResponse(OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static Order createOrderResponse(OrderStatus status) {
        Order order = new Order();
        order.setStatus(status);
        return order;
    }
}
