package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order createRequest(final OrderType type) {
        final Order request = new Order();
        request.setType(type);
        request.setOrderLineItems(List.of(OrderLineItemFixture.createRequest()));

        if (type != null) {
            setAdditionalInfo(request);
        }
        return request;
    }

    private static void setAdditionalInfo(final Order request) {
        switch (request.getType()) {
            case EAT_IN:
                request.setOrderTableId(OrderTableFixture.createUsedTable().getId());
                break;
            case TAKEOUT:
                break;
            case DELIVERY:
                request.setDeliveryAddress("서울특별시");
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
