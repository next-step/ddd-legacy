package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemFixture {
    public static OrderLineItem orderLineItemCreate(Menu menu, BigDecimal price, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenu(menu);
        return orderLineItem;
    }
}
