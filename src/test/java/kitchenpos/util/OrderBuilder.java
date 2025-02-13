package kitchenpos.util;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderBuilder {

    private UUID id;
    private OrderType orderType;
    private List<OrderLineItem> orderLineItems;
    private String deliveryAddress;
    private OrderStatus orderStatus;
    private OrderTable orderTable;
    private UUID orderTableId;

    private OrderBuilder() {
    }

    public static OrderBuilder id(UUID id) {
        OrderBuilder orderBuilder = new OrderBuilder();
        orderBuilder.id = id;
        return orderBuilder;
    }

    public OrderBuilder orderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderBuilder orderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        return this;
    }

    public OrderBuilder deliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderBuilder orderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public OrderBuilder orderTable(OrderTable orderTable) {
        this.orderTable = orderTable;
        return this;
    }

    public OrderBuilder orderTableId(UUID orderTableId) {
        this.orderTableId = orderTableId;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setStatus(orderStatus);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        return order;
    }
}
