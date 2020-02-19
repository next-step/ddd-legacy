package kitchenpos.model;

public class OrderTableTest {

    static final Long SINGLE_TABLE_ID = 1L;
    static final Long FIRST_OF_MULTI_TABLE_ID = 2L;
    static final Long SECOND_OF_MULTI_TABLE_ID = 3L;
    static final Long FIRST_EMPTY_TABLE_ID = 4L;
    static final Long SECOND_EMPTY_TABLE_ID = 5L;

    public static OrderTable ofSingle() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(SINGLE_TABLE_ID);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        return orderTable;
    }

    public static OrderTable ofFirstOfMulti() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(FIRST_OF_MULTI_TABLE_ID);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(2L);
        return orderTable;
    }

    public static OrderTable ofSecondOfMulti() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(SECOND_OF_MULTI_TABLE_ID);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(2L);
        return orderTable;
    }

    public static OrderTable ofEmpty() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(FIRST_EMPTY_TABLE_ID);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static OrderTable ofAnotherEmpty() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(SECOND_EMPTY_TABLE_ID);
        orderTable.setEmpty(true);
        return orderTable;
    }
}
