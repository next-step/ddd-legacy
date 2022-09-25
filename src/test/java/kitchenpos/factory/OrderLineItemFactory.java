package kitchenpos.factory;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFactory {

    public static OrderLineItem of(Menu menu) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }
}
