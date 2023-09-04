package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

public class OrderFixture {

    private OrderFixture() {
    }

    public static Order create(UUID id, OrderType orderType, OrderStatus orderStatus, List<OrderLineItem> orderLineItems) {
        return create(id, orderType, orderStatus, null, orderLineItems);
    }
    public static Order create(UUID id, OrderType orderType, OrderStatus orderStatus, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        Order result = new Order();
        result.setId(id);
        result.setType(orderType);
        result.setStatus(orderStatus);
        result.setOrderDateTime(now());
        result.setOrderLineItems(orderLineItems);
        result.setOrderTable(orderTable);
        result.setOrderTableId(orderTable == null ? null : orderTable.getId());
        result.setDeliveryAddress(orderType == OrderType.DELIVERY ? "서울시 강남구" : null);
        return result;
    }

}
