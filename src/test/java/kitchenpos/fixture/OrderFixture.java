package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    public Order create(OrderType type, OrderTable table, String address, List<OrderLineItem> orderItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderTable(table);
        order.setOrderTableId(table.getId());
        order.setDeliveryAddress(address);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderItems);
        return order;
    }

    public Order createDelivery(String address, List<OrderLineItem> orderLineItems) {
        return create(OrderType.DELIVERY
                , null
                , address
                , orderLineItems);
    }

    public Order createEatIn(OrderTable table, List<OrderLineItem> orderLineItems) {
        return create(OrderType.EAT_IN
                , table
                , null
                , orderLineItems);
    }

    public Order createTakeOut(List<OrderLineItem> orderLineItems) {
        return create(OrderType.TAKEOUT, null, null, orderLineItems);
    }
}
