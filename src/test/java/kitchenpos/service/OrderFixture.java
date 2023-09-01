package kitchenpos.service;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderType;

public class OrderFixture {
    private final Order order;

    public OrderFixture() {
        order = new Order();
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
}
