package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.util.List;

public class OrderFixture {

    public static Order createOrder(OrderType type,
                                    OrderStatus status,
                                    List<OrderLineItem> orderLineItems,
                                    String deliveryAddress,
                                    OrderTable orderTable
    ) {
        Order order = new Order();
        order.setType(type);
        order.setStatus(status);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order createOrder() {
        return createOrder(OrderType.EAT_IN,
                OrderStatus.WAITING,
                List.of(OrderLineItemFixture.createOrderLineItem()),
                null,
                OrderTableFixture.createOrderTable());
    }

    public static Order createOrderWithStatus(OrderStatus status) {
        return createOrder(OrderType.EAT_IN,
                status,
                List.of(OrderLineItemFixture.createOrderLineItem()),
                null,
                OrderTableFixture.createOrderTable());
    }

    public static Order createOrderWithType(OrderType type) {
        return createOrder(type,
                OrderStatus.WAITING,
                List.of(OrderLineItemFixture.createOrderLineItem()),
                null,
                OrderTableFixture.createOrderTable());
    }

    public static Order createOrderWithTypeAndStatus(OrderType type, OrderStatus status) {
        return createOrder(type,
                status,
                List.of(OrderLineItemFixture.createOrderLineItem()),
                null,
                OrderTableFixture.createOrderTable());
    }

    public static Order createOrderWithOrderLineItems(List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.EAT_IN,
                OrderStatus.WAITING,
                orderLineItems,
                null,
                OrderTableFixture.createOrderTable());
    }

    public static Order createOrderWithTypeAndOrderLineItems(OrderType type,
                                                             List<OrderLineItem> orderLineItems) {
        return createOrder(type,
                OrderStatus.WAITING,
                orderLineItems,
                null,
                OrderTableFixture.createOrderTable());
    }

    public static Order createOrderWithTypeAndDeliveryAddress(OrderType type, String deliveryAddress) {
        return createOrder(type,
                OrderStatus.WAITING,
                List.of(OrderLineItemFixture.createOrderLineItem()),
                deliveryAddress,
                OrderTableFixture.createOrderTable());
    }
}
