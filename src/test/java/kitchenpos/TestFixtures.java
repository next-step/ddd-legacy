package kitchenpos;

import kitchenpos.model.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

public class TestFixtures {

    private static final long ORDER_LINE_ITEM_MENU_ID = 1L;
    private static final long ORDER_LINE_ITEM_SEQ = 1L;
    private static final long ORDER_LINE_ITEM_ORDER_ID = 1L;
    private static final long ORDER_LINE_ITEM_QUANTITY = 2L;
    private static final long ORDER_ID = 1L;
    private static final long ORDER_TABLE_ID = 1L;
    private static final int GUEST_NUMBER = 2;
    private static final long TABLE_GROUP_ID = 1L;

    public static OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(ORDER_LINE_ITEM_SEQ);
        orderLineItem.setQuantity(ORDER_LINE_ITEM_QUANTITY);
        orderLineItem.setMenuId(ORDER_LINE_ITEM_MENU_ID);
        orderLineItem.setOrderId(ORDER_LINE_ITEM_ORDER_ID);
        return orderLineItem;
    }

    public static Order createOrderByItemEmpty() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(Collections.emptyList());
        return order;
    }

    public static Order createOrderByCooking() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(Arrays.asList(createOrderLineItem()));
        order.setOrderTableId(ORDER_TABLE_ID);
        order.setOrderStatus(OrderStatus.COOKING.name());
        return order;
    }

    public static Order createOrderByCompletion() {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(Arrays.asList(createOrderLineItem()));
        order.setOrderTableId(ORDER_TABLE_ID);
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        return order;
    }

    public static OrderTable createOrderTableByEmpty() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(GUEST_NUMBER);
        orderTable.setTableGroupId(TABLE_GROUP_ID);
        return orderTable;
    }
}
