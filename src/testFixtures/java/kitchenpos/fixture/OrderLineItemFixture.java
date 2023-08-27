package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemFixture {

    private OrderLineItemFixture() {}

    public static OrderLineItem generateOrderLineItem(final Menu menu, final long quantity) {
        return createOrderLineItem(menu, quantity);
    }

    private static OrderLineItem createOrderLineItem(final Menu menu, final long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();

        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice().multiply(BigDecimal.valueOf(quantity)));

        return orderLineItem;
    }
}
