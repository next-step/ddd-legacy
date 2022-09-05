package kitchenpos.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order Order(
        UUID orderTableId,
        OrderType type,
        String deliveryAddress,
        OrderLineItem... orderLineItems
    ) {
        Order order = new Order();
        order.setOrderTableId(orderTableId);
        order.setType(type);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static Order OrderWithUUIDAndOrderDateTimeAndStatus(
        UUID orderTableId,
        OrderType type,
        String deliveryAddress,
        LocalDateTime orderDateTime,
        OrderStatus status,
        OrderLineItem... orderLineItems
    ) {
        Order order = Order(
            orderTableId,
            type,
            deliveryAddress,
            orderLineItems
        );
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(orderDateTime);
        order.setStatus(status);
        return order;
    }

    public static Order OrderWithUUIDAndOrderTableAndOrderDateTimeAndStatus(
        OrderTable orderTable,
        OrderType type,
        String deliveryAddress,
        LocalDateTime orderDateTime,
        OrderStatus status,
        OrderLineItem... orderLineItems
    ) {
        Order order = Order(
            orderTable.getId(),
            type,
            deliveryAddress,
            orderLineItems
        );
        order.setOrderTable(orderTable);
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(orderDateTime);
        order.setStatus(status);
        return order;
    }
}
