package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    private static final UUID EMPTY_ORDER_TABLE_UUID = UUID.randomUUID();
    private static final UUID NOT_EMPTY_ORDER_TABLE_UUID = UUID.randomUUID();

    private static final String TABLE_NAME_A = "A 테이블";
    private static final String TABLE_NAME_B = "B 테이블";

    private static final int ZERO = 0;
    private static final int ONE = 0;

    private static final boolean EMPTY = true;
    private static final boolean NOT_EMPTY = false;

    public static OrderTable EMPTY_ORDER_TABLE_REQUEST() {
        return createOrderTable(EMPTY_ORDER_TABLE_UUID, TABLE_NAME_A, ZERO, EMPTY);
    }

    public static OrderTable NOT_EMPTY_ORDER_TABLE_REQUEST() {
        return createOrderTable(NOT_EMPTY_ORDER_TABLE_UUID, TABLE_NAME_B, ONE, NOT_EMPTY);
    }

    private static OrderTable createOrderTable(final UUID id, final String name, final int numberOfGuests, final boolean empty) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);

        return orderTable;
    }
}
