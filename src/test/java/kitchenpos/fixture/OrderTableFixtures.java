package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixtures {

    public static OrderTable createOrderTable() {
        return createOrderTable("테이블1", 3);
    }

    public static OrderTable unoccupiedOrderTable() {
        return createOrderTable("테이블1", 3, false);
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests) {
        return createOrderTable(name, numberOfGuests, true);
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
