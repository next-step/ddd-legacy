package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public static OrderTable createSimpleOrderTable(String tableName) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(tableName);
        orderTable.setEmpty(true);
        return orderTable;
    };
}
