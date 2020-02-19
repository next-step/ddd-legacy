package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

import static kitchenpos.model.OrderTableTest.FIRST_OF_MULTI_TABLE_ID;
import static kitchenpos.model.OrderTableTest.SINGLE_TABLE_ID;

public class OrderTest {
    static final Long SINGLE_TABLE_ORDER_ID = 1L;
    static final Long TABLE_GROUP_ORDER_ID = 2L;

    public static Order ofOneHalfAndHalfInSingleTable() {
        Order order = new Order();
        order.setId(SINGLE_TABLE_ORDER_ID);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(SINGLE_TABLE_ID);
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.of())
        );
        return order;
    }

    public static Order ofOneHalfAndHalfInTableGroup() {
        Order order = new Order();
        order.setId(TABLE_GROUP_ORDER_ID);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(FIRST_OF_MULTI_TABLE_ID);
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.of())
        );
        return order;
    }
}
