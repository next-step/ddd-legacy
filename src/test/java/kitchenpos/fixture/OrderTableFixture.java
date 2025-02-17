package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable createOrderTable(String name) {
        final var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(false);
        return orderTable;
    }

    public static OrderTable createOrderTable() {
        final var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("익명 테이블");
        orderTable.setOccupied(false);
        return orderTable;
    }

    public static OrderTable createOccupiedOrderTable(int numberOfGuests) {
        final var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("익명 테이블");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }
}
