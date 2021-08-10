package kitchenpos.application.fixture;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    private static final UUID UUID1 = UUID.randomUUID();
    private static final UUID UUID2 = UUID.randomUUID();
    private static final UUID UUID3 = UUID.randomUUID();
    private static final String TABLE_NAME1 = "테이블1";
    private static final String TABLE_NAME2 = "테이블2";
    private static final int ZERO = 0;

    public static OrderTable ORDER_TABLE1_REQUEST() {
        return createOrderTable(null, TABLE_NAME1, ZERO, true);
    }

    public static OrderTable ORDER_TABLE_WITH_NAME_REQUEST(final String name) {
        return createOrderTable(null, name, ZERO, true);
    }

    public static OrderTable NOT_EMPTY_TABLE_WITH_GUESTS_REQUEST(final int numberOfGuests) {
        return createOrderTable(null, TABLE_NAME1, numberOfGuests, false);
    }

    public static OrderTable NOT_EMPTY_TABLE() {
        return createOrderTable(UUID3, TABLE_NAME1, ZERO, false);
    }

    public static OrderTable ORDER_TABLE1() {
        return createOrderTable(UUID1, TABLE_NAME1, ZERO, true);
    }

    public static OrderTable ORDER_TABLE2() {
        return createOrderTable(UUID2, TABLE_NAME2, ZERO, true);
    }

    public static List<OrderTable> ORDER_TABLES() {
        return Arrays.asList(ORDER_TABLE1(), ORDER_TABLE2());
    }

    private static OrderTable createOrderTable(final UUID id, final String tableName, final int numberOfGuests, final boolean isEmpty) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(tableName);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(isEmpty);
        return orderTable;
    }

}
