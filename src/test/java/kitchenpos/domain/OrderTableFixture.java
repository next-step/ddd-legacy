package kitchenpos.domain;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable OrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable OrderTableWithUUID(String name) {
        OrderTable orderTable = OrderTable(name);
        orderTable.setId(UUID.randomUUID());
        return orderTable;
    }
}
