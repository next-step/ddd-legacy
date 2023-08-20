package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderFixture {
    public static Order create(OrderType type, Optional<OrderTable> table, Optional<String> address, List<OrderLineItem> orderItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderTable(table.orElse(null));
        order.setOrderTableId(table.map(OrderTable::getId).orElse(null));
        order.setDeliveryAddress(address.orElse(null));
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderItems);
        return order;
    }

    public static Order createDelivery(Optional<String> address, List<OrderLineItem> orderLineItems) {
        return create(OrderType.DELIVERY
                , Optional.empty()
                , address
                , orderLineItems);
    }

    public static Order createEatIn(Optional<OrderTable> orderTable, List<OrderLineItem> orderLineItems) {
        return create(OrderType.EAT_IN
                , orderTable
                , Optional.empty()
                , orderLineItems);
    }

    public static Order createTakeOut(List<OrderLineItem> orderLineItems) {
        return create(OrderType.TAKEOUT, Optional.empty(), Optional.empty(), orderLineItems);
    }
}
