package kitchenpos.stub;

import kitchenpos.domain.OrderTable;

public class OrderTableStub {

    public static final String EMPTY_ORDER_TABLE_NAME = "빈테이블";
    public static final String NOT_EMPTY_TWIN_ORDER_TABLE_NAME = "2명사용중인테이블";

    private OrderTableStub() {
    }

    public static OrderTable generateEmptyOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(EMPTY_ORDER_TABLE_NAME);
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(0);
        return orderTable;
    }

    public static OrderTable generateNotEmptyForTwinOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(NOT_EMPTY_TWIN_ORDER_TABLE_NAME);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(2);
        return orderTable;
    }

    public static OrderTable generateEmptyNameOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(1);
        return orderTable;
    }

    public static OrderTable generateChangingNumberOfGuestsToFourRequest() {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(4);
        return orderTable;
    }

    public static OrderTable generateChangingNumberOfGuestsToNegativeNumberRequest() {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(-4);
        return orderTable;
    }
}
