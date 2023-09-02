package kitchenpos.testfixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    private OrderFixture() {
    }

    public static Order copy(Order order) {
        var copiedOrder = new Order();
        copiedOrder.setId(order.getId());
        copiedOrder.setOrderDateTime(order.getOrderDateTime());
        copiedOrder.setStatus(order.getStatus());
        copiedOrder.setOrderLineItems(order.getOrderLineItems());
        copiedOrder.setOrderTableId(order.getOrderTableId());
        copiedOrder.setDeliveryAddress(order.getDeliveryAddress());
        copiedOrder.setType(order.getType());
        copiedOrder.setOrderTable(order.getOrderTable());
        return copiedOrder;
    }

    public static Order createOrder(
            OrderStatus status,
            OrderType type,
            List<OrderLineItem> orderLineItems
    ) {
        return createOrder(status, type, orderLineItems, OrderTableFixture.createOrderTable("테이블명", 4));
    }

    public static Order createOrder(
            OrderStatus status,
            OrderType type,
            List<OrderLineItem> orderLineItems,
            OrderTable orderTable
    ) {
        return createdOrder(
                UUID.randomUUID(),
                LocalDateTime.now(),
                status,
                orderLineItems,
                UUID.randomUUID(),
                "서울시 강남구 역삼동",
                type,
                orderTable
        );
    }

    public static Order createOrder(
            OrderStatus status,
            OrderType type,
            List<OrderLineItem> orderLineItems,
            OrderTable orderTable,
            String deliveryAddress
    ) {
        return createdOrder(
                UUID.randomUUID(),
                LocalDateTime.now(),
                status,
                orderLineItems,
                UUID.randomUUID(),
                deliveryAddress,
                type,
                orderTable
        );
    }


    public static Order createOrder(OrderStatus status, OrderType type) {
        return createOrder(
                status,
                type,
                List.of(
                        createOrderLineItem(MenuFixture.createMenu("후라이드", 16000L), 16000L, 1),
                        createOrderLineItem(MenuFixture.createMenu("양념치킨", 26000L), 26000L, 5)
                )
        );
    }

    public static Order createdOrder(
            UUID id,
            LocalDateTime orderDateTime,
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            UUID orderTableId,
            String deliveryAddress,
            OrderType type,
            OrderTable orderTable
    ) {
        var order = new Order();
        order.setId(id);
        order.setOrderDateTime(orderDateTime);
        order.setStatus(status);
        order.setOrderLineItems(orderLineItems);
        order.setOrderTableId(orderTableId);
        order.setDeliveryAddress(deliveryAddress);
        order.setType(type);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }

    public static OrderLineItem createOrderLineItem(
            Menu menu,
            long price,
            int quantity
    ) {
        var orderLineItem = new OrderLineItem();

        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static OrderLineItem createOrderLineItem(
            Menu menu,
            BigDecimal price,
            int quantity
    ) {
        var orderLineItem = new OrderLineItem();

        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

}
