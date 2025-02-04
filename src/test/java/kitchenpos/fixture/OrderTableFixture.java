package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public static final String ORDER_TABLE_NAME = "테이블";
    public static final int EMPTY_GUESTS = 0;
    public static final int COUPLE_GUESTS = 2;
    public static final boolean IS_NOT_OCCUPIED = false;
    public static final boolean IS_OCCUPIED = true;

    private OrderTableFixture() {
    }

    public static OrderTable orderTable() {
        return orderTable(createOrderTableId(), ORDER_TABLE_NAME, EMPTY_GUESTS, IS_NOT_OCCUPIED);
    }

    public static OrderTable orderTable(final String name, int numberOfGuests, boolean occupied) {
        return orderTable(createOrderTableId(), name, numberOfGuests, occupied);
    }

    public static OrderTable orderTable(final UUID id, final String name,
                                        final int numberOfGuests, final boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static UUID createOrderTableId() {
        return UUID.randomUUID();
    }
}
