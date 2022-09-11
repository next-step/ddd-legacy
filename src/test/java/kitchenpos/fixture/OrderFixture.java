package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order createRequest(final OrderType type) {
        return createRequest(type, List.of(OrderLineItemFixture.createRequest()));
    }

    public static Order createRequest(final OrderType type, final List<OrderLineItem> orderLineItems) {
        return createRequest(type, OrderTableFixture.createUsedTable().getId(), orderLineItems);
    }

    public static Order createRequest(final OrderType type, final UUID orderTableId, final List<OrderLineItem> orderLineItems) {
        return createRequest(type, orderTableId, "서울특별시", orderLineItems);
    }

    public static Order createRequest(final OrderType type, final String deliveryAddress, final List<OrderLineItem> orderLineItems) {
        return createRequest(type, OrderTableFixture.createUsedTable().getId(), deliveryAddress, orderLineItems);
    }

    public static Order createRequest(final OrderType type, final UUID orderTableId, final String deliveryAddress, final List<OrderLineItem> orderLineItems) {
        final Order request = new Order();
        request.setType(type);
        request.setOrderLineItems(orderLineItems);

        if (type != null) {
            setAdditionalInfo(request, orderTableId, deliveryAddress);
        }
        return request;
    }

    private static void setAdditionalInfo(final Order request, final UUID orderTableId, final String deliveryAddress) {
        switch (request.getType()) {
            case EAT_IN:
                request.setOrderTableId(orderTableId);
                break;
            case TAKEOUT:
                break;
            case DELIVERY:
                request.setDeliveryAddress(deliveryAddress);
                break;
        }
    }

    public static Order create(final OrderType type, final OrderStatus status) {
        return create(type, status, OrderTableFixture.createUsedTable());
    }

    public static Order create(final OrderType type, final OrderStatus status, final OrderTable orderTable) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderLineItems(List.of(OrderLineItemFixture.createDefault()));
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(status);
        switch (type) {
            case EAT_IN:
                order.setOrderTable(orderTable);
                break;
            case TAKEOUT:
                break;
            case DELIVERY:
                order.setDeliveryAddress("서울특별시");
                break;
        }
        return order;
    }
}
