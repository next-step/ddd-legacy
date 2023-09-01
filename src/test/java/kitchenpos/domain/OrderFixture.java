package kitchenpos.domain;

public class OrderFixture {

    public static Order watingOrder(OrderTable orderTable) {
        return new OrderBuilder()
                .orderTable(orderTable)
                .orderTableId(orderTable.getId())
                .status(OrderStatus.WAITING)
                .build();
    }

    public static Order completedOrder(OrderTable orderTable) {
        return new OrderBuilder()
                .orderTable(orderTable)
                .orderTableId(orderTable.getId())
                .status(OrderStatus.COMPLETED)
                .build();
    }
}
