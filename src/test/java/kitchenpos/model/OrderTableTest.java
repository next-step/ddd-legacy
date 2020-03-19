package kitchenpos.model;

public final class OrderTableTest {
    static final Long SINGLE_TABLE_ID = 1L;
    static final Long FIRST_OF_MULTI_TABLE_ID = 2L;
    static final Long SECOND_OF_MULTI_TABLE_ID = 3L;

    public static OrderTable of() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(SINGLE_TABLE_ID);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        return orderTable;
    }

    public static OrderTable ofEmpty() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(SINGLE_TABLE_ID);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static OrderTable ofFirstInTableGroup() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(FIRST_OF_MULTI_TABLE_ID);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static OrderTable ofSecondInTableGroup() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(SECOND_OF_MULTI_TABLE_ID);
        orderTable.setEmpty(true);

        return orderTable;
    }
}
