package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;

public class OrderFixture {

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final OrderType ORDER_TYPE = OrderType.DELIVERY;
    private static final OrderStatus ORDER_STATUS = OrderStatus.WAITING;
    private static final LocalDateTime ORDER_DATE_TIME = LocalDateTime.MIN;
    private static final String DELIVERY_ADDRESS = "address";

    public static Order createOrder() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setType(ORDER_TYPE);
        order.setStatus(ORDER_STATUS);
        order.setOrderDateTime(ORDER_DATE_TIME);
        order.setOrderLineItems(List.of(createOrderLineItem(1, BigDecimal.TEN)));
        order.setDeliveryAddress(DELIVERY_ADDRESS);
        order.setOrderTable(createOrderTable());
        order.setOrderTableId(createOrderTable().getId());

        return order;
    }

    public static OrderLineItem createOrderLineItem(final int quantity,
                                                    final BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(createMenu());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);

        return orderLineItem;
    }

    public static Order createOrderWithType(final OrderType orderType) {
        Order order = createOrder();
        order.setType(orderType);

        return order;
    }

    public static Order createOrderWithStatus(final OrderStatus orderStatus) {
        Order order = createOrder();
        order.setStatus(orderStatus);

        return order;
    }

    public static Order createOrderWithTypeAndStatus(final OrderType orderType,
                                                     final OrderStatus orderStatus) {
        Order order = createOrder();
        order.setType(orderType);
        order.setStatus(orderStatus);

        return order;
    }

    public static Order createOrderWithOrderLineItems(final List<OrderLineItem> orderLineItems) {
        Order order = createOrder();
        order.setOrderLineItems(orderLineItems);

        return order;
    }

}
