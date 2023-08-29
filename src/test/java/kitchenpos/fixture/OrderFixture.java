package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;

public class OrderFixture {
    public static Order createDeliveryOrder(final List<OrderLineItem> orderLineItems) {
        return createOrder(
                null, OrderType.DELIVERY, OrderStatus.COMPLETED,
                LocalDateTime.now(), orderLineItems, "청주시",
                null, null
        );
    }

    public static Order createOrder(
            final UUID id, final OrderType type, final OrderStatus status, final LocalDateTime orderDateTime,
            final List<OrderLineItem> orderLineItems, final String deliveryAddress,
            final OrderTable orderTable, final UUID orderTableId
    ) {
        final Order order = new Order();

        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);

        return order;
    }
}
