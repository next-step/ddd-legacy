package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

import java.util.List;
import java.util.UUID;

public class OrderFixture {
    public static final String ORDER_배달주소 = "서울시 D아파트 D동 D호";

    public static Order orderEatInCreateRequest(UUID orderTableId, OrderLineItem...orderLineItems) {
        return orderCreateRequest(OrderType.EAT_IN, null, orderTableId, orderLineItems);
    }

    public static Order orderDeliveryCreateRequest(String deliveryAddress, OrderLineItem...orderLineItems) {
        return orderCreateRequest(OrderType.DELIVERY, deliveryAddress, null, orderLineItems);
    }

    public static Order orderTakeOutCreateRequest(OrderLineItem...orderLineItems) {
        return orderCreateRequest(OrderType.TAKEOUT, null, null, orderLineItems);
    }

    private static Order orderCreateRequest(OrderType type, String deliveryAddress, UUID orderTableId, OrderLineItem...orderLineItems) {
        Order order = new Order();
        order.setType(type);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(List.of(orderLineItems));
        return order;
    }
}
