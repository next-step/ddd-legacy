package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable TEST_ORDER_TABLE() {
        return TEST_ORDER_TABLE(false, 0);
    }

    public static OrderTable TEST_ORDER_TABLE(boolean isOccupied) {
        return TEST_ORDER_TABLE(isOccupied, 0);
    }

    public static OrderTable TEST_ORDER_TABLE(int numberOfGuest) {
        return TEST_ORDER_TABLE(false, numberOfGuest);
    }

    public static OrderTable TEST_ORDER_TABLE(boolean isOccupied, int numberOfGuest) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(isOccupied);
        orderTable.setName("1번 테이블");
        orderTable.setNumberOfGuests(numberOfGuest);
        return orderTable;
    }
}
