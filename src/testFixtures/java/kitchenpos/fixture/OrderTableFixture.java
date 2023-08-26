package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    private OrderTableFixture() {}

    private static final String DEFAULT_NAME = "order_table";
    private static final int DEFAULT_NUMBER_OR_GUESTS = 0;
    private static final boolean DEFAULT_OCCUPIED = false;

    public static OrderTable generateOrderTable() {
        return createOrderTable(UUID.randomUUID(), DEFAULT_NAME, DEFAULT_NUMBER_OR_GUESTS, DEFAULT_OCCUPIED);
    }

    public static OrderTable generateOrderTable(final String name) {
        return createOrderTable(UUID.randomUUID(), name, DEFAULT_NUMBER_OR_GUESTS, DEFAULT_OCCUPIED);
    }

    public static OrderTable generateOrderTable(
            final int numberOfGuests,
            final boolean occupied
    ) {
        return createOrderTable(UUID.randomUUID(), DEFAULT_NAME, numberOfGuests, occupied);
    }

    public static OrderTable generateOrderTable(
            final String name,
            final int numberOfGuests,
            final boolean occupied
    ) {
        return createOrderTable(UUID.randomUUID(), name, numberOfGuests, occupied);
    }

    private static OrderTable createOrderTable(
            final UUID id,
            final String name,
            final int numberOfGuests,
            final boolean occupied
    ) {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);

        return orderTable;
    }
}
