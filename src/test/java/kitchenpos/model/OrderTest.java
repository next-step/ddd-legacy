package kitchenpos.model;

import java.time.LocalDateTime;
import java.util.Arrays;

public class OrderTest {

    public static Order ofOneHalfAndHalfInSingleTable() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(OrderTableTest.ofSingle().getId());
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.of())
        );
        return order;
    }

    public static Order ofOneHalfAndHalfInTableGroup() {
        Order order = new Order();
        order.setId(2L);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(OrderTableTest.ofFirstOfMulti().getId());
        order.setOrderLineItems(
                Arrays.asList(OrderLineItemTest.of())
        );
        return order;
    }
}