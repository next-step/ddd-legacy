package kitchenpos.application.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static final UUID UUID1 = UUID.randomUUID();
    public static final UUID UUID2 = UUID.randomUUID();
    public static final UUID UUID3 = UUID.randomUUID();
    public static final String TABLE_NAME1 = "테이블1";
    public static final String TABLE_NAME2 = "테이블2";
    public static final int ZERO = 0;

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
        orderTable.setId(UUID1);
        orderTable.setName(TABLE_NAME1);
        orderTable.setNumberOfGuests(ZERO);
        orderTable.setEmpty(false);
        return orderTable;
    }

}
