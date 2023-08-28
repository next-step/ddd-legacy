package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public static OrderTable createOrderTable() {
        return createOrderTable(null, "테이블1", 4, false);
    }

    public static OrderTable createOrderTable(final String name) {
        return createOrderTable(null, name, 4, false);
    }

    public static OrderTable createOrderTable(
            final String name, final int numberOfGuests, final boolean occupied
    ) {
        return createOrderTable(null, name, numberOfGuests, occupied);
    }

    public static OrderTable createOrderTable(
            final int numberOfGuests, final boolean occupied
    ) {
        return createOrderTable(null, "테이블1", numberOfGuests, occupied);
    }

    public static OrderTable createOrderTable(
            final UUID id, final String name, final int numberOfGuests, final boolean occupied
    ) {
        final OrderTable orderTable = new OrderTable();

        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);

        return orderTable;
    }
}
