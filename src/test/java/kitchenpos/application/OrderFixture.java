package kitchenpos.application;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.OrderLineItemFixture.createOrderLineItemRequest;

public class OrderFixture {
    public static Order createOrderRequest(final OrderType orderType, final OrderLineItem orderLineItemRequest) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItemRequest));
        if(orderType.equals(OrderType.DELIVERY)){
            order.setDeliveryAddress("서울 강남");
        }
        if (orderType.equals(OrderType.EAT_IN)) {
            order.setOrderTable(OrderTableFixture.createOrderTableRequest());
        }

        order.setStatus(OrderStatus.WAITING);

        return order;
    }

    public static Order createOrderRequest(final OrderType orderType, final String address, final OrderLineItem orderLineItemRequest) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItemRequest));
        if(orderType.equals(OrderType.DELIVERY)){
            order.setDeliveryAddress(address);
        }
        if (orderType.equals(OrderType.EAT_IN)) {
            order.setOrderTable(OrderTableFixture.createOrderTableRequest());
        }

        order.setStatus(OrderStatus.WAITING);

        return order;
    }

    public static Order createOrderRequest(final OrderTable orderTable, final OrderType orderType, final OrderLineItem orderLineItemRequest) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItemRequest));
        order.setStatus(OrderStatus.WAITING);

        return order;
    }

        public static Order createOrderRequest(final OrderTable orderTable, final OrderType orderType) {
        final Order order = new Order();
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItemRequest));
        order.setStatus(OrderStatus.WAITING);

        return order;
    }

    public static Order createOrderRequest(final OrderTable orderTable, final String address) {
        final Order order = new Order();
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setDeliveryAddress(address);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItemRequest));
        order.setStatus(OrderStatus.WAITING);

        return order;
    }

    public static Order createOrderRequest(final OrderTable orderTable, final OrderStatus orderStatus) {
        final Order order = new Order();
        final OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItemRequest));
        order.setStatus(orderStatus);

        return order;
    }


}
