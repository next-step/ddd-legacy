package kitchenpos.helper;


import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

import java.util.List;
import java.util.UUID;

public final class OrderHelper {

    private OrderHelper() {
    }

public static Order createOrderTypeIsDelivery(List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order createOrderTypeIsEatIn(OrderType orderType, List<OrderLineItem> orderLineItems, UUID orderTableId) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setOrderTableId(orderTableId);
        return order;
    }

    public static Order createOrderTypeIsTakeOut(OrderType orderType, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order create(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress, UUID orderTableId) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableId);
        return order;
    }

}
