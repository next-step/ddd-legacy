package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderLineItemFixture.orderLineItem;
import static kitchenpos.fixture.OrderTableFixture.orderTable;

public class OrderFixture {

    public static final String DEFAULT_DELIVERY_ADDRESS = "서울시 강남구";

    public static Order order(final UUID uuid, final OrderType type, final OrderStatus status, final LocalDateTime orderDateTime, final List<OrderLineItem> orderLineItems, final String deliveryAddress, final OrderTable orderTable, final UUID oderTableId) {
        final Order order = new Order();
        order.setId(uuid);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(oderTableId);
        return order;
    }

    public static Order order(final OrderType type, final Menu menu, final OrderTable orderTable) {
        return order(null, type, null, null, List.of(orderLineItem(menu)), null, orderTable, orderTable.getId());
    }

    public static Order order(final OrderType type, final Menu menu, final String deliveryAddress) {
        return order(null, type, null, null, List.of(orderLineItem(menu)), deliveryAddress, null, null);
    }

    public static Order order(final OrderType type, final Menu menu) {
        return order(null, type, null, null, List.of(orderLineItem(menu)), null, null, null);
    }

    public static Order order(final OrderType type, final OrderStatus status, final OrderTable orderTable) {
        return order(getUuid(), type, status, LocalDateTime.of(2021, 7, 27, 10, 30), null, null, orderTable, orderTable.getId());
    }

    public static Order order() {
        OrderTable orderTable = orderTable();
        return order(getUuid(), OrderType.DELIVERY, OrderStatus.WAITING, LocalDateTime.of(2021, 7, 27, 10, 30), List.of(orderLineItem()), DEFAULT_DELIVERY_ADDRESS, orderTable, orderTable.getId());
    }

    public static UUID getUuid() {
        return UUID.randomUUID();
    }

}
