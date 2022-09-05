package kitchenpos.domain;

import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order Order(UUID orderTableId, OrderType type, String deliveryAddress, OrderLineItem... orderLineItems) {
        Order order = new Order();
        order.setOrderTableId(orderTableId);
        order.setType(type);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }
}
