package kitchenpos.mock.fixture;

import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static Order create(final OrderType orderType, final List<OrderLineItem> orderLineItems,
        final String deliveryAddress, final OrderTable orderTable) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        if (orderTable != null) {
            order.setOrderTable(orderTable);
            order.setOrderTableId(orderTable.getId());
        }
        return order;
    }
}
