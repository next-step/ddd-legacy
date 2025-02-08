package kitchenpos.fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class OrderFixtures {

    public static Order order(final OrderStatus status, final OrderTable orderTable, final Menu menu) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(status);
        order.setOrderDateTime(LocalDateTime.of(2025, 2, 1, 12, 0));
        order.setOrderLineItems(Arrays.asList(orderLineItem(menu)));
        order.setOrderTable(orderTable);
        return order;
    }

    public static OrderLineItem orderLineItem(final Menu menu) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(new Random().nextLong());
        orderLineItem.setMenu(menu);
        return orderLineItem;
    }
}
