package kitchenpos.service;

import java.util.List;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {
    private final Order order;

    public OrderFixture() {
        order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
    }

    public static OrderFixture builder() {
        return new OrderFixture();
    }

    public static OrderFixture builder(Menu menu) {
        return builder()
                .orderLineItem(List.of(OrderLineItemFixture.builder(menu).build()))
                .deliveryAddress("서울시 영등포구");
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

    public OrderFixture deliveryAddress(String deliveryAddress) {
        order.setDeliveryAddress(deliveryAddress);
        return this;
    }

    public OrderFixture orderTable(OrderTable orderTable) {
        if (orderTable != null) {
            order.setOrderTableId(orderTable.getId());
        }
        order.setOrderTable(orderTable);
        return this;
    }

    public OrderFixture status(OrderStatus orderStatus) {
        order.setStatus(orderStatus);
        return this;
    }

    public OrderFixture eatIn(OrderTable orderTable) {
        order.setType(OrderType.EAT_IN);
        return orderTable(orderTable);
    }
}
