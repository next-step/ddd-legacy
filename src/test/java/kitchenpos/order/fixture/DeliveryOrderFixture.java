package kitchenpos.order.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public class DeliveryOrderFixture {


    private static Order 주문을_생성한다(
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        return 주문을_생성한다(null, OrderType.DELIVERY, null, orderLineItems, deliveryAddress);
    }

    private static Order 주문을_생성한다(
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        return 주문을_생성한다(null, OrderType.DELIVERY, status, orderLineItems, deliveryAddress);
    }

    private static Order 주문을_생성한다(
            OrderType type,
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        return 주문을_생성한다(null, type, status, orderLineItems, deliveryAddress);
    }

    private static Order 주문을_생성한다(
            UUID id,
            OrderType type,
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            String deliveryAddress
    ) {
        var 주문 = new Order();
        주문.setId(id);
        주문.setType(type);
        주문.setStatus(status);
        주문.setOrderDateTime(LocalDateTime.now());
        주문.setOrderLineItems(orderLineItems);
        주문.setDeliveryAddress(deliveryAddress);

        return 주문;
    }

}
