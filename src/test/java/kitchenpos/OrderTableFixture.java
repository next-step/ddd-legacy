package kitchenpos;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    private static final String DEFAULT_TABLE_NAME = "기본 테이블 1";

    public static OrderTable 주문테이블_Request(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable 주문테이블_Request() {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        orderTable.setName(DEFAULT_TABLE_NAME);
        return orderTable;
    }
}
