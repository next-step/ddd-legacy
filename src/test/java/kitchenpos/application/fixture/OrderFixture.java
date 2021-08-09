package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.OrderLineItemFixture.HIDED_MENU_ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderLineItemFixture.NEGATIVE_QUANTITY_ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderLineItemFixture.ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderLineItemFixture.WRONG_PRICE_MENU_ORDER_LINE_ITEMS;
import static kitchenpos.application.fixture.OrderTableFixture.ORDER_TABLE1;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static Order NORMAL_ORDER() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order NORMAL_ORDER2() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.SERVED);
        return order;
    }

    public static Order ORDER_WITH_TYPE_AND_STATUS(final OrderType type, final OrderStatus orderStatus) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(ORDER_LINE_ITEMS());
        order.setStatus(orderStatus);
        return order;
    }

    public static Order NULL_TYPE_ORDER() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(null);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order NULL_ORDER_LINE_ITEMS_ORDER() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(null);
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order EMPTY_ORDER_LINE_ITEMS_ORDER() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(Collections.emptyList());
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order HIDED_MENU_ORDER() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(HIDED_MENU_ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order WRONG_PRICE_MENU_ORDER() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(WRONG_PRICE_MENU_ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order NEGATIVE_QUANTITY_ORDER_LINE_ITEMS_ORDER(final OrderType orderType) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(NEGATIVE_QUANTITY_ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order DELIVERY_ORDER_WITH_ADDRESS(final String address) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(ORDER_TABLE1());
        order.setOrderLineItems(ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.WAITING);
        order.setDeliveryAddress(address);
        return order;
    }

    public static Order EAT_IN_NULL_ORDER_TABLE_ORDER() {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(ORDER_TABLE1().getId());
        order.setOrderTable(null);
        order.setOrderLineItems(ORDER_LINE_ITEMS());
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static List<Order> ORDERS() {
        return Arrays.asList(NORMAL_ORDER(), NORMAL_ORDER2());
    }
}
