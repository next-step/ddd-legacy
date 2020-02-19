package kitchenpos.model;

import static kitchenpos.model.MenuTest.HALF_AND_HALF_SET_MENU_ID;
import static kitchenpos.model.OrderTest.SINGLE_TABLE_ORDER_ID;

public class OrderLineItemTest {

    public static OrderLineItem of() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setOrderId(SINGLE_TABLE_ORDER_ID);
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        return orderLineItem;
    }
}
