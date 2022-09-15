package kitchenpos.fixture.domain;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.OrderType.*;
import static kitchenpos.fixture.domain.OrderLineFixture.orderLineItem;

public class OrderFixture {

    public static Order deliveryOrder(final OrderStatus status, final String deliveryAddress) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(DELIVERY);
        order.setStatus(status);
        order.setOrderDateTime(LocalDateTime.of(2022, 9, 9, 12, 0));
        order.setOrderLineItems(List.of(orderLineItem()));
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order takeOutOrder(final OrderStatus status) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(TAKEOUT);
        order.setStatus(status);
        order.setOrderDateTime(LocalDateTime.of(2022, 9, 9, 12, 0));
        order.setOrderLineItems(List.of(orderLineItem()));
        return order;
    }

    public static Order eatInOrder(final OrderStatus status, final OrderTable orderTable) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(EAT_IN);
        order.setStatus(status);
        order.setOrderDateTime(LocalDateTime.of(2022, 9, 9, 12, 0));
        order.setOrderLineItems(List.of(orderLineItem()));
        order.setOrderTable(orderTable);
        return order;
    }
}
