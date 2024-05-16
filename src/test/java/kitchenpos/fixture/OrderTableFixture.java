package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    private OrderTableFixture() {
    }

    public static OrderTable createOrderTable(final UUID id, final String name, final int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable createOrderTable(final UUID id) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        return orderTable;
    }

    public static OrderTable createOrderTable(final String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable createOrderTableWithId(final String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable createOrderTableWithId(final String name, final boolean isOccupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(isOccupied);
        return orderTable;
    }

    public static OrderTable createOrderTableWithId(final String name, final boolean isOccupied, final int numberOfGuest) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(isOccupied);
        orderTable.setNumberOfGuests(numberOfGuest);
        return orderTable;
    }
}
