package kitchenpos.fixture;

import kitchenpos.domain.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order createOrder(OrderType orderType, Menu menu, OrderTable orderTable) {
        final var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        final var orderLineItem = createOrderLineItem(menu);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);
        return order;
    }

    private static @NotNull OrderLineItem createOrderLineItem(Menu menu) {
        final var orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice().multiply(BigDecimal.valueOf(orderLineItem.getQuantity())));
        return orderLineItem;
    }
}
