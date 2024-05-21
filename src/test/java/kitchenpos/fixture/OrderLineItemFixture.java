package kitchenpos.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {

    public static OrderLineItem createOrderLineItem(final Menu menu) {
        return createOrderLineItem(menu, 1L);
    }

    public static OrderLineItem createOrderLineItem(Menu menu, long quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }


    public static OrderLineItem createOrderLineItem(Menu menu, BigDecimal price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
