package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class OrderLineItemFixture {

    public static @NotNull OrderLineItem createOrderLineItem(Menu menu) {
        final var orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice().multiply(BigDecimal.valueOf(orderLineItem.getQuantity())));
        return orderLineItem;
    }

    public static @NotNull OrderLineItem createOrderLineItem(Menu menu, int quantity) {
        final var orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice().multiply(BigDecimal.valueOf(orderLineItem.getQuantity())));
        return orderLineItem;
    }
}
