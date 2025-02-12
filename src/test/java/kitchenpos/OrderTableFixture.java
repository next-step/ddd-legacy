package kitchenpos;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    private static final String DEFAULT_TABLE_NAME = "기본 테이블 1";

    public static OrderTable 주문테이블_생성_Request(String name) {
        OrderTable orderTable = 주문테이블_생성_Request();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable 주문테이블_생성_Request() {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        orderTable.setName(DEFAULT_TABLE_NAME);
        return orderTable;
    }

    public static OrderTable 주문테이블_사용중_Request() {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(3);
        orderTable.setOccupied(true);
        orderTable.setName(DEFAULT_TABLE_NAME);
        return orderTable;
    }

    public static OrderTable 주문테이블_사용중_Request(int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(true);
        orderTable.setName(DEFAULT_TABLE_NAME);
        return orderTable;
    }
}
