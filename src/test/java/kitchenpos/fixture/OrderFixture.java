package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderLineItemFixture.orderLineItem;
import static kitchenpos.fixture.OrderTableFixture.orderTable;

public class OrderFixture {

    public static final String DEFAULT_DELIVERY_ADDRESS = "서울시 송파구 위례성대로 2";

    public static final OrderType DEFAULT_ORDER_TYPE = OrderType.DELIVERY;

    public static final OrderStatus DEFAULT_ORDER_STATUS = OrderStatus.WAITING;


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

    public static Order order() {
        OrderLineItem orderLineItem = orderLineItem();
        OrderTable orderTable = orderTable();
        return order(createOrderId(), DEFAULT_ORDER_TYPE, DEFAULT_ORDER_STATUS, LocalDateTime.of(2021, 7, 27, 10, 30), List.of(orderLineItem), DEFAULT_DELIVERY_ADDRESS, orderTable, orderTable.getId());
    }

    public static UUID createOrderId() {
        return UUID.randomUUID();
    }

}
