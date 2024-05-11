package kitchenpos.application.testFixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public record OrderTableFixture() {

    public static OrderTable newOne(String orderTableName) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(orderTableName);
        return orderTable;
    }

    public static OrderTable newOne(UUID id, String orderTableName) {
        var orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(orderTableName);
        return orderTable;
    }
}
