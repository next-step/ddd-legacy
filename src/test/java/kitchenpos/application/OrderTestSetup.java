package kitchenpos.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public abstract class OrderTestSetup {


    protected OrderTable createOrderTableRequest(final String name, final int numberOfGuests,
        final boolean occupied) {

        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);

        return orderTable;
    }

    protected Order createOrder(final OrderType type, final OrderStatus status,
        final LocalDateTime orderDateTime, final List<OrderLineItem> orderLineItems,
        final String deliveryAddress, final OrderTable orderTable) {

        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);

        return order;
    }
}
