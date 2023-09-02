package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderTestFixture {
    private OrderTestFixture() {
    }

    public static Order createOrder(OrderType type, OrderTable orderTable, OrderStatus status, String deliveryAddress, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderTable(orderTable);
        order.setStatus(status);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }

    public static OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static OrderTable createOrderTable(boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(occupied);
        return orderTable;
    }


}
