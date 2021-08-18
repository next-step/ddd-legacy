package kitchenpos.builder;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class OrderBuilder {
    private UUID id;
    private OrderType type;
    private OrderStatus status;
    private LocalDateTime orderDateTime;
    private List<OrderLineItem> orderLineItems;
    private String deliveryAddress;
    private OrderTable orderTable;
    private UUID orderTableId;

    private OrderBuilder() {
        id = UUID.randomUUID();
        type = OrderType.DELIVERY;
        status = OrderStatus.WAITING;
        orderDateTime = LocalDateTime.now();
        orderLineItems = Collections.singletonList(OrderLineItemBuilder.newInstance().build());
        deliveryAddress = "우리집";
        orderTable = null;
        orderTableId = null;
    }

    public static OrderBuilder newInstance() {
        return new OrderBuilder();
    }

    public OrderBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public OrderBuilder setType(OrderType type) {
        this.type = type;
        return this;
    }

    public OrderBuilder setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder setOrderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
        return this;
    }

    public OrderBuilder setOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        return this;
    }

    public OrderBuilder setOrderLineItems(OrderLineItem... orderLineItems) {
        this.orderLineItems = Arrays.asList(orderLineItems);
        return this;
    }

    public OrderBuilder setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderBuilder setOrderTable(OrderTable orderTable) {
        this.orderTable = orderTable;
        this.orderTableId = orderTable.getId();
        return this;
    }

    public OrderBuilder setOrderTableId(UUID orderTableId) {
        this.orderTableId = orderTableId;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        return order;
    }
}
