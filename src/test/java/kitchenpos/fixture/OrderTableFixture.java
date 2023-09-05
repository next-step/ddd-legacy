package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTable createOrderTable(String name, boolean isOccupied, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setOccupied(isOccupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable createOrderTable() {
        return createOrderTable("1번 테이블", false, 0);
    }

    public static OrderTable createOrderTableWithName(String name) {
        return createOrderTable(name, false, 0);
    }

    public static OrderTable createOrderTableWithIsOccupied(boolean isOccupied) {
        return createOrderTable("1번 테이블", isOccupied, 0);
    }

    public static OrderTable createOrderTableWithNumberOfGuests(int numberOfGuests) {
        return createOrderTable("1번 테이블", false, numberOfGuests);
    }

    public static OrderTable createOrderTableWithIsOccupiedAndNumberOfGuests(boolean isOccupied, int numberOfGuests) {
        return createOrderTable("1번 테이블", isOccupied, numberOfGuests);
    }
}
