package kitchenpos.application.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.fixture.OrderLineItemFixture.ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderTableFixture.EMPTY_ORDER_TABLE_REQUEST;

public class OrderFixture {

    public static Order NORMAL_ORDER_REQUEST() {
        return createOrder(UUID.randomUUID(), OrderType.EAT_IN, OrderStatus.WAITING, ORDER_LINE_ITEMS(), EMPTY_ORDER_TABLE_REQUEST());
    }

    public static Order EAT_IN_ORDER_STATUS_ACCEPTED_REQUEST() {
        return createOrder(UUID.randomUUID(), OrderType.EAT_IN, OrderStatus.ACCEPTED, ORDER_LINE_ITEMS(), EMPTY_ORDER_TABLE_REQUEST());
    }

    public static Order EAT_IN_ORDER_STATUS_SERVE_REQUEST() {
        return createOrder(UUID.randomUUID(), OrderType.EAT_IN, OrderStatus.SERVED, ORDER_LINE_ITEMS(), EMPTY_ORDER_TABLE_REQUEST());
    }

    public static Order EAT_IN_ORDER_STATUS_COMPLETED_REQUEST() {
        return createOrder(UUID.randomUUID(), OrderType.EAT_IN, OrderStatus.COMPLETED, ORDER_LINE_ITEMS(), EMPTY_ORDER_TABLE_REQUEST());
    }

    public static Order DELIVERY_ORDER_STATUS_SERVE_REQUEST() {
        return createOrder(UUID.randomUUID(), OrderType.DELIVERY, OrderStatus.SERVED, ORDER_LINE_ITEMS(), "제주특별자치도 제주시 첨단로 242", null);
    }

    public static Order DELIVERY_ORDER_STATUS_DELIVERING_REQUEST() {
        return createOrder(UUID.randomUUID(), OrderType.DELIVERY, OrderStatus.DELIVERING, ORDER_LINE_ITEMS(), "제주특별자치도 제주시 첨단로 242", null);
    }

    public static Order DELIVERY_ORDER_STATUS_DELIVERED_REQUEST() {
        return createOrder(UUID.randomUUID(), OrderType.DELIVERY, OrderStatus.DELIVERED, ORDER_LINE_ITEMS(), "제주특별자치도 제주시 첨단로 242", null);
    }

    private static Order createOrder(
            final UUID id,
            final OrderType orderType,
            final OrderStatus orderStatus,
            final List<OrderLineItem> orderLineItems,
            final OrderTable orderTable
    ) {
        return createOrder(id, orderType, orderStatus, orderLineItems, null, orderTable);
    }

    private static Order createOrder(
            final UUID id,
            final OrderType orderType,
            final OrderStatus orderStatus,
            final List<OrderLineItem> orderLineItems,
            final String deliveryAddress,
            final OrderTable orderTable
    ) {
        Order order = new Order();

        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderDateTime(LocalDateTime.now());

        return order;
    }
}
