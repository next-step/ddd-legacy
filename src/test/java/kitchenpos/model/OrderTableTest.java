package kitchenpos.model;

public class OrderTableTest {
    public static OrderTable ofSingle() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        return orderTable;
    }

    public static OrderTable ofFirstOfMulti() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(2L);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(1L);
        return orderTable;
    }

    public static OrderTable ofSecondOfMulti() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(3L);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(1L);
        return orderTable;
    }
}