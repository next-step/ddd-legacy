package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public static final String DEFAULT_ORDER_TABLE_NAME = "테이블";
    public static final int DEFAULT_NUMBER_OF_GUESTS = 0;
    private static final boolean DEFAULT_OCCUPIED = false;

    private OrderTableFixture() {
    }

    public static OrderTable orderTable(final UUID id, final String name, final int numberOfGuests, final boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable orderTable() {
        return orderTable(createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, DEFAULT_OCCUPIED);
    }

    public static UUID createOrderTableId() {
        return UUID.randomUUID();
    }

}
