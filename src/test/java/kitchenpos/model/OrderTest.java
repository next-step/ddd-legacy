package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

import static kitchenpos.model.OrderTableTest.*;

public class OrderTest {
    static final Long TABLE_GROUP_FIRST_ORDER_ID = 1L;
    static final Long TABLE_GROUP_SECOND_ORDER_ID = 2L;
    static final Long TABLE_ORDER_ID = 3L;

    public static Order ofCompleted() {
        final Order order = new Order();
        order.setId(TABLE_ORDER_ID);
        order.setOrderStatus(OrderStatus.COMPLETION.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(SINGLE_TABLE_ID);
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.ofSingle())
        );
        return order;
    }

    public static Order of() {
        final Order order = new Order();
        order.setId(TABLE_ORDER_ID);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(SINGLE_TABLE_ID);
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.ofSingle())
        );
        return order;
    }

    public static Order ofFirstInTableGroup() {
        final Order order = new Order();
        order.setId(TABLE_GROUP_FIRST_ORDER_ID);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(FIRST_OF_MULTI_TABLE_ID);
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.ofFirstInTableGroup())
        );
        return order;
    }

    public static Order ofSecondInTableGroup() {
        final Order order = new Order();
        order.setId(TABLE_GROUP_SECOND_ORDER_ID);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(SECOND_OF_MULTI_TABLE_ID);
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.ofSecondInTableGroup())
        );
        return order;
    }
}
