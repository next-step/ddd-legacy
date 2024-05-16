package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderFixture {
    private OrderFixture() {
    }

    public static Order createOrder(final UUID id,
                                    final OrderTable orderTable,
                                    final List<OrderLineItem> orderLineItems,
                                    final OrderType type,
                                    final OrderStatus status,
                                    final String deliveryAddress) {
        final Order order = new Order();
        order.setId(id);
        order.setOrderTable(orderTable);
        if (Objects.nonNull(orderTable)) {
            order.setOrderTableId(orderTable.getId());
        }
        order.setOrderLineItems(orderLineItems);
        order.setType(type);
        order.setStatus(status);
        order.setDeliveryAddress(deliveryAddress);

        return order;
    }

    public static Order createOrder(final OrderTable orderTable,
                                    final List<OrderLineItem> orderLineItems,
                                    final OrderType type,
                                    final OrderStatus status,
                                    final String deliveryAddress) {
        final Order order = new Order();
        order.setOrderTable(orderTable);
        if (Objects.nonNull(orderTable)) {
            order.setOrderTableId(orderTable.getId());
        }
        order.setOrderLineItems(orderLineItems);
        order.setType(type);
        order.setStatus(status);
        order.setDeliveryAddress(deliveryAddress);

        return order;
    }

    public static Order createOrderWithId(final OrderTable orderTable,
                                          final List<OrderLineItem> orderLineItems,
                                          final OrderType type,
                                          final OrderStatus status,
                                          final String deliveryAddress,
                                          final LocalDateTime orderDateTime) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(orderTable);
        order.setOrderLineItems(orderLineItems);
        order.setType(type);
        order.setStatus(status);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderDateTime(orderDateTime);

        return order;
    }
}
