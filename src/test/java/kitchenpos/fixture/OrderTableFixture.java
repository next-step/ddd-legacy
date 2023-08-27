package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.List;
import java.util.UUID;

public class OrderTableFixture {

    private static final UUID ORDER_TABLE_ID = UUID.randomUUID();
    private static final String ORDER_TABLE_NAME = "name";
    private static final int NUMBER_OF_GUEST = 0;
    private static final boolean OCCUPIED = false;

    public static OrderTable createOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setName(ORDER_TABLE_NAME);
        orderTable.setNumberOfGuests(NUMBER_OF_GUEST);
        orderTable.setOccupied(OCCUPIED);

        return orderTable;
    }

    public static OrderTable createOrderTableWithName(final String name) {
        OrderTable orderTable = createOrderTable();
        orderTable.setName(name);

        return orderTable;
    }

    public static OrderTable createOrderTableWithNumberOfGuest(final int numberOfGuest) {
        OrderTable orderTable = createOrderTable();
        orderTable.setNumberOfGuests(numberOfGuest);

        return orderTable;
    }

    public static OrderTable createOrderTableWithOccupied(final boolean occupied) {
        OrderTable orderTable = createOrderTable();
        orderTable.setOccupied(occupied);

        return orderTable;
    }

    public static List<OrderTable> createOrderTables() {
        return List.of(createOrderTable(), createOrderTable());
    }

}
