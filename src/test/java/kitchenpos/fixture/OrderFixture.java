package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order create(OrderStatus orderStatus, OrderTable orderTable) {
        return create(UUID.randomUUID(), OrderType.EAT_IN,  orderStatus, LocalDateTime.now(), Collections.emptyList(), "성동구", orderTable);
    }

    public static Order create(OrderType orderType, List<OrderLineItem> orderLineItems, OrderTable orderTable) {
        return create(UUID.randomUUID(), orderType, OrderStatus.WAITING, LocalDateTime.now(), orderLineItems, "성동구", orderTable);
    }

    public static Order create(OrderType orderType, List<OrderLineItem> orderLineItems, OrderTable orderTable, String deliveryAddress) {
        return create(UUID.randomUUID(), orderType, OrderStatus.WAITING, LocalDateTime.now(), orderLineItems, deliveryAddress, orderTable);
    }

    public static Order create(OrderType orderType, List<OrderLineItem> orderLineItems, OrderTable orderTable, String deliveryAddress, OrderStatus orderStatus) {
        return create(UUID.randomUUID(), orderType, orderStatus, LocalDateTime.now(), orderLineItems, deliveryAddress, orderTable);
    }

    private static Order create(final UUID id, final OrderType type,
                               final OrderStatus orderStatus, final LocalDateTime orderDateTime,
                               final List<OrderLineItem> orderLineItems, final String deliveryAddress,
                               final OrderTable orderTable) {
        final Order order = new Order();

        order.setId(id);
        order.setType(type);
        order.setStatus(orderStatus);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());

        return order;
    }

}
