package kitchenpos.order.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    public static final Order 주문_유형_없는_주문 = create(null, List.of(OrderLineItemFixture.주문_항목));
    public static final Order 주문_항목_없는_주문 = create(OrderType.EAT_IN, null);

    private OrderLineItemFixture orderLineItemFixture = new OrderLineItemFixture();
    public Order 매장_주문_A = create(OrderType.EAT_IN, List.of(orderLineItemFixture.주문_항목_A));
    public Order 포장_주문_A = create(OrderType.TAKEOUT, List.of(orderLineItemFixture.주문_항목_A));
    public Order 배달_주문_A = create(OrderType.DELIVERY, List.of(orderLineItemFixture.주문_항목_A));

    public static Order create(OrderType type, List<OrderLineItem> orderLineItems) {
        return create(UUID.randomUUID(), type, OrderStatus.WAITING, LocalDateTime.now(), orderLineItems);
    }

    public static Order create(UUID id, OrderType type, OrderStatus status, LocalDateTime orderDateTime, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);

        return order;
    }
}
