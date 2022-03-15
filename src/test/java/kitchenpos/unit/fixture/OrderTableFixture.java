package kitchenpos.unit.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {
    public static final OrderTable 테이블_1번;

    static {
        테이블_1번 = createOrderTable("1번");
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }

    public static OrderTable createOrderTable(String name) {
        return createOrderTable(name, 0, true);
    }
}
