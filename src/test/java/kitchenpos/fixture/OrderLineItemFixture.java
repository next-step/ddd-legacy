package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemFixture {

    public static OrderLineItem create(Menu menu, BigDecimal price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(price);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }
}
