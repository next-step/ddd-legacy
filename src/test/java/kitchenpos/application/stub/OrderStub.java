package kitchenpos.application.stub;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderStub {

    public static Order createRequest(final OrderType type) {
        final Order request = new Order();
        request.setType(type);
        request.setOrderLineItems(List.of(OrderLineItemStub.createRequest()));

        if (type != null) {
            setAdditionalInfo(request);
        }
        return request;
    }

    private static void setAdditionalInfo(final Order request) {
        switch (request.getType()) {
            case EAT_IN:
                request.setOrderTableId(OrderTableStub.createUsedTable().getId());
                break;
            case TAKEOUT:
                break;
            case DELIVERY:
                request.setDeliveryAddress("서울특별시");
                break;
        }
    }

    public static Order create(final OrderType type, final OrderStatus status) {
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setOrderLineItems(List.of(OrderLineItemStub.createDefault()));
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(status);
        switch (type) {
            case EAT_IN:
                order.setOrderTable(OrderTableStub.createUsedTable());
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
