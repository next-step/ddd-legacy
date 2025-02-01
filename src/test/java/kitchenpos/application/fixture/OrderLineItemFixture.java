package kitchenpos.application.fixture;

import kitchenpos.domain.OrderLineItem;

import java.util.Arrays;
import java.util.List;

public class OrderLineItemFixture {

    public static final List<OrderLineItem> DEFAULT_ORDER_LINE_ITEM = Arrays.asList(createOrderLineItem());

    public static OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1);
        orderLineItem.setMenu(MenuFixture.DEFAULT_MENU);
        orderLineItem.setMenuId(MenuFixture.DEFAULT_MENU.getId());
        orderLineItem.setPrice(MenuFixture.DEFAULT_MENU.getPrice());
        return orderLineItem;
    }
}
