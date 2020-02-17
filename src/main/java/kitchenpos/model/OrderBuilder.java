package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.List;

public final class OrderBuilder {
    private Long id;
    private Long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItem> orderLineItems;

    private OrderBuilder() {}

    public static OrderBuilder anOrder() { return new OrderBuilder(); }

    public OrderBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OrderBuilder withOrderTableId(Long orderTableId) {
        this.orderTableId = orderTableId;
        return this;
    }

    public OrderBuilder withOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public OrderBuilder withOrderedTime(LocalDateTime orderedTime) {
        this.orderedTime = orderedTime;
        return this;
    }

    public OrderBuilder withOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setId(id);
        order.setOrderTableId(orderTableId);
        order.setOrderStatus(orderStatus);
        order.setOrderedTime(orderedTime);
        order.setOrderLineItems(orderLineItems);
        return order;
    }
}
