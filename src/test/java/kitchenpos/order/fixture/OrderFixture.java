package kitchenpos.order.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    private OrderLineItemFixture orderLineItemFixture = new OrderLineItemFixture();
    private OrderTableFixture orderTableFixture = new OrderTableFixture();

    public Order 매장_주문_A = createEatIn(List.of(orderLineItemFixture.주문_항목_A), orderTableFixture.손님_있는_주문_테이블);
    public Order 포장_주문_A = createTakeOut(List.of(orderLineItemFixture.주문_항목_A));
    public Order 배달_주문_A = createDelivery(List.of(orderLineItemFixture.주문_항목_A), "행궁동");

    public Order 주문_유형_없는_주문 = create(null, List.of(orderLineItemFixture.주문_항목));
    public Order 주문_항목_없는_주문 = create(OrderType.EAT_IN, null);

    public static Order createDeliveryHasStatus(List<OrderLineItem> orderLineItems, String deliveryAddress,
                                                OrderStatus orderStatus)
    {
        Order order = createDelivery(orderLineItems, deliveryAddress);
        order.setStatus(orderStatus);

        return order;
    }

    public static Order createEatInHasStatus(List<OrderLineItem> orderLineItems, OrderTable orderTable,
                                             OrderStatus orderStatus)
    {
        Order order = createEatIn(orderLineItems, orderTable);
        order.setStatus(orderStatus);

        return order;
    }

    public static Order createHasStatus(OrderType type, List<OrderLineItem> orderLineItems, OrderStatus status) {
        Order order = create(type, orderLineItems);
        order.setStatus(status);

        return order;
    }

    public static Order createEatIn(List<OrderLineItem> orderLineItems, OrderTable orderTable) {
        Order order = create(OrderType.EAT_IN, orderLineItems);
        order.setOrderTable(orderTable);

        return order;
    }

    public static Order createTakeOut(List<OrderLineItem> orderLineItems) {
        return create(OrderType.TAKEOUT, orderLineItems);
    }

    public static Order createDelivery(List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order deliveryOrder = create(OrderType.DELIVERY, orderLineItems);
        deliveryOrder.setDeliveryAddress(deliveryAddress);
        return deliveryOrder;
    }

    public static Order create(OrderType type, List<OrderLineItem> orderLineItems) {
        return create(UUID.randomUUID(), type, OrderStatus.WAITING, LocalDateTime.now(), orderLineItems);
    }

    public static Order create(UUID id, OrderType type, OrderStatus status, LocalDateTime orderDateTime,
                               List<OrderLineItem> orderLineItems)
    {
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);

        return order;
    }
}
