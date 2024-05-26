package kitchenpos.fixtures;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    public static Order create(OrderType orderType, OrderStatus orderStatus, LocalDateTime orderDate, List<OrderLineItem> orderLineItems, String deliveryAddress, OrderTable orderTable) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderDateTime(orderDate);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        if (orderTable != null) {
            order.setOrderTable(orderTable);
            order.setOrderTableId(orderTable.getId());
        }
        return order;
    }
}
