package kitchenpos.builder;

import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderBuilder {
    private Long id;
    private Long orderTableId;
    private String orderStatus;
    private LocalDateTime orderedTime;
    private List<OrderLineItem> orderLineItems;

    public OrderBuilder() {
    }

    public OrderBuilder id(Long val) {
        id = val;
        return this;
    }

    public OrderBuilder orderTableId(Long val) {
        orderTableId = val;
        return this;
    }

    public OrderBuilder orderStatus(String val) {
        orderStatus = val;
        return this;
    }

    public OrderBuilder orderedTime(LocalDateTime val) {
        orderedTime = val;
        return this;
    }

    public OrderBuilder orderLineItems(List<OrderLineItem> val) {
        orderLineItems = val;
        return this;
    }

    public Order build() {
        final Order order = new Order();
        order.setId(this.id);
        order.setOrderTableId(this.orderTableId);
        order.setOrderStatus(this.orderStatus);
        order.setOrderedTime(this.orderedTime);
        order.setOrderLineItems(this.orderLineItems);
        return order;
    }


}
