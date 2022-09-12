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

    public static Order create(
        OrderType orderType,
        OrderStatus orderStatus,
        OrderTable orderTable
    ) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));
        order.setOrderTable(orderTable);
        return order;
    }

    private static class OrderLineItemFixture {

        private static final int DEFAULT_ORDER_LINE_ITEM_QUANTITY = 1;

        public static OrderLineItem create() {
            var orderLineItem = new OrderLineItem();
            orderLineItem.setMenu(MenuFixture.ONE_FRIED_CHICKEN);
            orderLineItem.setQuantity(DEFAULT_ORDER_LINE_ITEM_QUANTITY);
            return orderLineItem;
        }
    }
}
