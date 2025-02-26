package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable createOrderTable(final String name, final int numberOfGuests, final boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable createUnOccupiedOrderTable(final String name, final int numberOfGuests) {
        return createOrderTable(name, numberOfGuests, false);
    }

    public static OrderTable createOccupiedTable() {
        return createOrderTable("테이블1", 0, true);
    }

    public static OrderTable createOrderTable() {
        return createOrderTable("테이블1", 0, false);
    }

    public static OrderTable createOrderTableRequest(final String name, final int numberOfGuests, final boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

}
