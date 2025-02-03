package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class OrderFixture {
    private OrderFixture() {
    }

    public static Order eatInOrder(
            final UUID id, final LocalDateTime orderDateTime,
            final OrderTable orderTable, final OrderStatus orderStatus, final List<OrderLineItem> orderLineItems
    ) {
        return order(id, orderDateTime, null, orderStatus, OrderType.EAT_IN, orderTable, orderLineItems);
    }

    public static Order takeoutOrder(
            final UUID id, final LocalDateTime orderDateTime,
            final OrderStatus orderStatus, final List<OrderLineItem> orderLineItems
    ) {
        return order(id, orderDateTime, null, orderStatus, OrderType.TAKEOUT, null, orderLineItems);
    }

    public static Order deliveryOrder(
            final UUID id, final LocalDateTime orderDateTime,
            final String deliverAddress,
            final OrderStatus orderStatus, final List<OrderLineItem> orderLineItems
    ) {
        return order(id, orderDateTime, deliverAddress, orderStatus, OrderType.DELIVERY, null, orderLineItems);
    }

    public static Order order(final UUID id, final LocalDateTime orderDateTime, final String deliverAddress,
                              final OrderStatus orderStatus, final OrderType orderType,
                              final OrderTable orderTable, final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setId(id);
        order.setOrderDateTime(orderDateTime);
        order.setDeliveryAddress(deliverAddress);
        order.setStatus(orderStatus);
        order.setType(orderType);
        order.setOrderTable(orderTable);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static UUID createOrderId() {
        return UUID.randomUUID();
    }

    public static OrderLineItem orderLineItem(
            final Long seq,
            final Menu menu,
            final long quantity,
            final BigDecimal price
    ) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static Long createOrderLineItemId() {
        return new Random().nextLong(1, 100);
    }
}
