package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public final class OrderFixtures {

    private OrderFixtures() {
        throw new RuntimeException("생성할 수 없는 클래스");
    }

    public static OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, long quantity, BigDecimal price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static Order createOrder(OrderType type, UUID orderTableId,
        OrderLineItem... orderLineItems) {
        final Order order = new Order();
        order.setType(type);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(Arrays.asList(orderLineItems));
        return order;
    }

    public static Order createOrder(OrderType type, String deliveryAddress,
        OrderLineItem... orderLineItems) {
        return createOrder(type, deliveryAddress, Arrays.asList(orderLineItems));
    }

    public static Order createOrder(OrderType type, String deliveryAddress,
        List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(type);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createOrder(
        UUID id,
        OrderType type,
        OrderStatus status,
        LocalDateTime orderDateTime,
        String deliveryAddress,
        OrderTable orderTable,
        OrderLineItem... orderLineItems
    ) {
        final Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(Arrays.asList(orderLineItems));
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order createEatInOrder(
        UUID id,
        OrderStatus status,
        LocalDateTime orderDateTime,
        OrderTable orderTable,
        OrderLineItem... orderLineItems
    ) {
        return createOrder(
            id,
            OrderType.EAT_IN,
            status,
            orderDateTime,
            null,
            orderTable,
            orderLineItems
        );
    }

    public static Order createDeliveryOrder(
        UUID id,
        OrderStatus status,
        LocalDateTime orderDateTime,
        String deliveryAddress,
        OrderLineItem... orderLineItems
    ) {
        return createOrder(
            id,
            OrderType.DELIVERY,
            status,
            orderDateTime,
            deliveryAddress,
            null,
            orderLineItems
        );
    }

    public static Order createTakeOutOrder(
        UUID id,
        OrderStatus status,
        LocalDateTime orderDateTime,
        OrderLineItem... orderLineItems
    ) {
        return createOrder(
            id,
            OrderType.TAKEOUT,
            status,
            orderDateTime,
            null,
            null,
            orderLineItems
        );
    }
}
