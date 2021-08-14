package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.OrderLineItemFixture.HIDED_MENU_ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderLineItemFixture.NEGATIVE_QUANTITY_ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderLineItemFixture.ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderLineItemFixture.WRONG_PRICE_MENU_ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderTableFixture.NOT_EMPTY_TABLE;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE1;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static Order ORDER_WITH_TYPE_AND_STATUS_REQUEST(final OrderType type, final OrderStatus orderStatus) {
        return createOrder(null, type, NOT_EMPTY_TABLE().getId(), NOT_EMPTY_TABLE(), ORDER_LINE_ITEMS(), orderStatus);
    }

    public static Order NULL_TYPE_ORDER_REQUEST() {
        return createOrder(null, null, ORDER_TABLE1().getId(), ORDER_TABLE1(), ORDER_LINE_ITEMS(), OrderStatus.WAITING);
    }

    public static Order NORMAL_ORDER_REQUEST() {
        return createOrder(null, OrderType.EAT_IN, ORDER_TABLE1().getId(), ORDER_TABLE1(), ORDER_LINE_ITEMS(), OrderStatus.WAITING);
    }

    public static Order NULL_ORDER_LINE_ITEMS_ORDER_REQUEST() {
        return createOrder(null, OrderType.EAT_IN, ORDER_TABLE1().getId(), ORDER_TABLE1(), null, OrderStatus.WAITING);
    }

    public static Order EMPTY_ORDER_LINE_ITEMS_ORDER_REQUEST() {
        return createOrder(null, OrderType.EAT_IN, ORDER_TABLE1().getId(), ORDER_TABLE1(), Collections.emptyList(), OrderStatus.WAITING);
    }

    public static Order HIDED_MENU_ORDER_REQUEST() {
        return createOrder(null, OrderType.EAT_IN, ORDER_TABLE1().getId(), ORDER_TABLE1(), HIDED_MENU_ORDER_LINE_ITEMS(), OrderStatus.WAITING);
    }

    public static Order WRONG_PRICE_MENU_ORDER_REQUEST() {
        return createOrder(null, OrderType.EAT_IN, ORDER_TABLE1().getId(), ORDER_TABLE1(), WRONG_PRICE_MENU_ORDER_LINE_ITEMS(), OrderStatus.WAITING);
    }

    public static Order NEGATIVE_QUANTITY_ORDER_LINE_ITEMS_ORDER_REQUEST(final OrderType orderType) {
        return createOrder(null, orderType, ORDER_TABLE1().getId(), ORDER_TABLE1(), NEGATIVE_QUANTITY_ORDER_LINE_ITEMS(), OrderStatus.WAITING);
    }

    public static Order DELIVERY_ORDER_WITH_ADDRESS_REQUEST(final String address) {
        return createOrder(UUID.randomUUID(), OrderType.DELIVERY, ORDER_TABLE1().getId(), ORDER_TABLE1(), ORDER_LINE_ITEMS(), OrderStatus.WAITING, address);
    }

    public static Order EAT_IN_NULL_ORDER_TABLE_ORDER_REQUEST() {
        return createOrder(null, OrderType.EAT_IN, ORDER_TABLE1().getId(), null, ORDER_LINE_ITEMS(), OrderStatus.WAITING);
    }

    public static Order NORMAL_ORDER() {
        return createOrder(UUID.randomUUID(), OrderType.EAT_IN, ORDER_TABLE1().getId(), ORDER_TABLE1(), ORDER_LINE_ITEMS(), OrderStatus.WAITING);
    }

    public static Order NORMAL_ORDER2() {
        return createOrder(UUID.randomUUID(), OrderType.DELIVERY, ORDER_TABLE1().getId(), ORDER_TABLE1(), ORDER_LINE_ITEMS(), OrderStatus.SERVED);
    }

    public static Order ORDER_WITH_TYPE_AND_STATUS(final OrderType type, final OrderStatus orderStatus) {
        return createOrder(UUID.randomUUID(), type, ORDER_TABLE1().getId(), ORDER_TABLE1(), ORDER_LINE_ITEMS(), orderStatus);
    }

    public static Order ORDER_WITH_TYPE_AND_STATUS_AND_TABLE(final OrderType type, final OrderStatus orderStatus, final OrderTable table) {
        return createOrder(UUID.randomUUID(), type, table.getId(), table, ORDER_LINE_ITEMS(), orderStatus);
    }

    public static List<Order> ORDERS() {
        return Arrays.asList(NORMAL_ORDER(), NORMAL_ORDER2());
    }

    private static Order createOrder(final UUID id, final OrderType type, final UUID orderTableId, final OrderTable orderTable, final List<OrderLineItem> orderLineItems,
        final OrderStatus orderStatus) {
        return createOrder(id, type, orderTableId, orderTable, orderLineItems, orderStatus, null);
    }

    private static Order createOrder(final UUID id, final OrderType type, final UUID orderTableId, final OrderTable orderTable, final List<OrderLineItem> orderLineItems, final OrderStatus orderStatus,
        final String address) {
        final Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setOrderTableId(orderTableId);
        order.setOrderTable(orderTable);
        order.setOrderLineItems(orderLineItems);
        order.setStatus(orderStatus);
        order.setDeliveryAddress(address);
        return order;
    }

}
