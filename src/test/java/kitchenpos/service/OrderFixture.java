package kitchenpos.service;

import java.util.List;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

public class OrderFixture {
    private final Order order;

    public OrderFixture() {
        order = new Order();
        order.setType(OrderType.DELIVERY);
    }

    public static OrderFixture builder() {
        return new OrderFixture();
    }

    public Order build() {
        return order;
    }

    public OrderFixture type(OrderType type) {
        order.setType(type);
        return this;
    }

    public OrderFixture orderLineItem(List<OrderLineItem> orderLineItems) {
        order.setOrderLineItems(orderLineItems);
        return this;
    }
}
