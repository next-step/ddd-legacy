package kitchenpos.dummy;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class DummyOrderTable {

    public static OrderTable createOrderTable() {
        return createOrderTable("테이블1", false);
    }

    public static OrderTable createOrderTable(boolean occupied) {
        return createOrderTable("테이블1", occupied);
    }

    public static OrderTable createOrderTable(String name) {
        return createOrderTable(name, false);
    }
    public static OrderTable createOrderTable(String name, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        return orderTable;
    }


}
