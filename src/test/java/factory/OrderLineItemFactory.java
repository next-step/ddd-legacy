package factory;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFactory {

    public static final int DEFAULT_QUANTITY = 1;

    public static OrderLineItem of(Menu menu) {
        return OrderLineItemFactory.of(menu, DEFAULT_QUANTITY);
    }

    public static OrderLineItem of(Menu menu, int quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }
}
