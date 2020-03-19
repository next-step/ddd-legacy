package kitchenpos.model;

import static kitchenpos.model.MenuTest.HALF_AND_HALF_SET_MENU_ID;
import static kitchenpos.model.OrderTest.*;

public class OrderLineItemTest {
    static final Long SINGLE_TABLE_ORDER_LINE_ITEM_ID = 1L;
    static final Long FIRST_TABLE_GROUP_ORDER_LINE_ITEM_ID = 2L;
    static final Long SECOND_TABLE_GROUP_ORDER_LINE_ITEM_ID = 3L;

    public static OrderLineItem ofSingle() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(SINGLE_TABLE_ORDER_LINE_ITEM_ID);
        orderLineItem.setOrderId(TABLE_ORDER_ID);
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        return orderLineItem;
    }

    public static OrderLineItem ofFirstInTableGroup() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(FIRST_TABLE_GROUP_ORDER_LINE_ITEM_ID);
        orderLineItem.setOrderId(TABLE_GROUP_FIRST_ORDER_ID);
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        return orderLineItem;
    }

    public static OrderLineItem ofSecondInTableGroup() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(SECOND_TABLE_GROUP_ORDER_LINE_ITEM_ID);
        orderLineItem.setOrderId(TABLE_GROUP_SECOND_ORDER_ID);
        orderLineItem.setQuantity(1L);
        orderLineItem.setMenuId(HALF_AND_HALF_SET_MENU_ID);
        return orderLineItem;
    }
}
