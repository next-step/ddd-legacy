package kitchenpos.fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemFixture {
    public static long seq = 0L;

    public static OrderLineItem create(Menu menu, BigDecimal price, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(++OrderLineItemFixture.seq);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static OrderLineItem 단일메뉴_주문품목(Menu menu) {
        return create(menu, menu.getPrice(), 1);
    }
}