package kitchenpos.application.testFixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public record OrderTableFixture() {

    public static OrderTable newOne(String orderTableName) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(orderTableName);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        return orderTable;
    }

    public static OrderTable newOne(UUID id, String orderTableName, int numberOfGuests, boolean isOccupied) {
        var orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(orderTableName);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        return orderTable;
    }
}
