package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.util.List;
import java.util.UUID;

public class OrderFixture {

    private OrderFixture() {
    }

    public static Order create(UUID id, OrderType orderType, List<OrderLineItem> orderLineItems) {
        return create(id, orderType, null, orderLineItems);
    }
    public static Order create(UUID id, OrderType orderType, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        Order result = new Order();
        result.setId(id);
        result.setType(orderType);
        result.setOrderLineItems(orderLineItems);
        result.setOrderTable(orderTable);
        result.setOrderTableId(orderTable == null ? null : orderTable.getId());
        result.setDeliveryAddress(orderType == OrderType.DELIVERY ? "서울시 강남구" : null);
        return result;
    }

}
