package kitchenpos.helper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static Order eatInOrder(
        OrderStatus orderStatus,
        OrderTable orderTable
    ) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order deliveryOrder(
        OrderStatus orderStatus,
        String deliveryAddress
    ) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order takeoutOrder(OrderStatus orderStatus) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.TAKEOUT);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));
        return order;
    }

    public static Order request(
        OrderType orderType,
        List<OrderLineItem> orderLineItems,
        UUID orderTableId
    ) {
        var order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setOrderTableId(orderTableId);
        return order;
    }

    public static Order request(
        OrderType orderType,
        List<OrderLineItem> orderLineItems,
        String deliveryAddress
    ) {
        var order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order request(
        OrderType orderType,
        List<OrderLineItem> orderLineItems
    ) {
        var order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        return order;
    }
}
