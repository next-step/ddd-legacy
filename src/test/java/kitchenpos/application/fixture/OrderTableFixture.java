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

    public static OrderTable ORDER_TABLE1() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID1);
        orderTable.setName(TABLE_NAME1);
        orderTable.setNumberOfGuests(ZERO);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static OrderTable ORDER_TABLE2() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID2);
        orderTable.setName(TABLE_NAME2);
        orderTable.setNumberOfGuests(ZERO);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static OrderTable ORDER_TABLE_WITH_NAME(final String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID2);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(ZERO);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static OrderTable NOT_EMPTY_TABLE() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID3);
        orderTable.setName(TABLE_NAME1);
        orderTable.setNumberOfGuests(ZERO);
        orderTable.setEmpty(false);
        return orderTable;
    }

    public static OrderTable NOT_EMPTY_TABLE_WITH_GUESTS(final int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID3);
        orderTable.setName(TABLE_NAME1);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(false);
        return orderTable;
    }

    public static List<OrderTable> ORDER_TABLES() {
        return Arrays.asList(ORDER_TABLE1(), ORDER_TABLE2());
    }

}
