package kitchenpos.dummy;

import kitchenpos.domain.OrderTable;

public class DummyOrderTable {

    public static OrderTable createOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable createOrderTable() {
        return createOrderTable("테이블1");
    }

}
