package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemTest {
    public static OrderLineItem create(final Menu menu, final int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(BigDecimal.valueOf(menu.getPrice().intValueExact() * quantity));
        return orderLineItem;
    }
}
