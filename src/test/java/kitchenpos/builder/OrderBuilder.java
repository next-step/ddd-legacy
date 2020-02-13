package kitchenpos.builder;

import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;

import java.time.LocalDateTime;
import java.util.List;

public class OrderBuilder {
    private Long id;
    private Long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItem> orderLineItems;

    public OrderBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public OrderBuilder setOrderTableId(Long orderTableId) {
        this.orderTableId = orderTableId;
        return this;
    }

    public OrderBuilder setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public OrderBuilder setOrderedTime(LocalDateTime orderedTime) {
        this.orderedTime = orderedTime;
        return this;
    }

    public OrderBuilder setOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        return this;
    }

    public Order build() {
        Order order = new Order();

        order.setId(id);
        order.setOrderedTime(orderedTime);
        order.setOrderLineItems(orderLineItems);
        order.setOrderStatus(orderStatus);
        order.setOrderTableId(orderTableId);

        return order;
    }
}
