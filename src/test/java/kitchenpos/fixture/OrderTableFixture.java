package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    private OrderTableFixture() {
    }

    public static OrderTable createOrderTable() {
        return createOrderTable(UUID.randomUUID(), "1번", 1, true);
        }

    public static OrderTable createOrderTable(UUID orderTableId, String orderTableName, int numberOfGuest, boolean istableUsable) {
        var orderTable = new OrderTable();
        orderTable.setId(orderTableId);
        orderTable.setName(orderTableName);
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setOccupied(istableUsable);
        return orderTable;
    }

    public static OrderTable createOrderTable(String name) {
        return createOrderTable(name,1);
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests) {
        return createOrderTable(UUID.randomUUID(), name, numberOfGuests, true);
        }

    public static OrderTable createOrderTable(String name, boolean isOcupied) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(1);
        orderTable.setOccupied(isOcupied);
        return orderTable;
    }
}
