package kitchenpos.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {
    public static OrderLineItem create(Menu menu, BigDecimal price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }
}
