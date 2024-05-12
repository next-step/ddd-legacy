package kitchenpos.application.testFixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.OrderType.*;

public record OrderFixture() {

    public static Order newOneEatIn(OrderTable orderTable, OrderStatus orderStatus) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.newOne()));
        order.setDeliveryAddress("");
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order newOneEatIn(OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress("");
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order newOneEatIn(UUID id, OrderTable orderTable, OrderStatus orderStatus) {
        var order = new Order();
        order.setId(id);
        order.setType(EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.newOne()));
        order.setDeliveryAddress("");
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order newOneDelivery(OrderStatus orderStatus) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(DELIVERY);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.newOne()));
        order.setDeliveryAddress("강남 테헤란로 1129-54");
        order.setOrderTable(null);
        return order;
    }

    public static Order newOneDelivery(String deliveryAddress, List<OrderLineItem> orderLineItems) {
        var order = new Order();
        order.setStatus(OrderStatus.WAITING);
        order.setType(DELIVERY);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order newOneTakeOut(OrderStatus orderStatus) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(TAKEOUT);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.newOne()));
        order.setDeliveryAddress("");
        order.setOrderTable(null);
        return order;
    }

    public static Order newOneTakeOut(OrderStatus orderStatus, List<OrderLineItem> orderLineItems) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(TAKEOUT);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(null);
        order.setOrderTable(null);
        return order;
    }

    public static Order newOne(OrderType orderType) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.newOne()));
        order.setDeliveryAddress("");
        order.setOrderTable(null);
        return order;
    }

    public static Order newOne(OrderType orderType, List<OrderLineItem> orderLineItems) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress("");
        order.setOrderTable(null);
        return order;
    }
}
