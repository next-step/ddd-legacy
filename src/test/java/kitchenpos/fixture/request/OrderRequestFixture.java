package kitchenpos.fixture.request;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

import java.util.List;
import java.util.UUID;

public class OrderRequestFixture {
    public static Order createOrderRequest(final OrderType orderType, final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        return order;
    }


    public static Order createEatInOrderRequest(final UUID orderTableId, final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(orderLineItems);
        order.setOrderTableId(orderTableId);
        return order;
    }

    public static Order createTakeOutOrderRequest(final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(orderLineItems);
        return order;
    }


    public static Order createDeliveryOrderRequest(final List<OrderLineItem> orderLineItems) {
        return createDeliveryOrderRequest("집주소", orderLineItems);
    }

    public static Order createDeliveryOrderRequest(final String deliveryAddress, final List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }
}
