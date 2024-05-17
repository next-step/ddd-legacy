package kitchenpos.application.testfixture;

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
    public static OrderTable newOne(int numberOfGuest) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번 테이블");
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setOccupied(true);
        return orderTable;
    }

    public static OrderTable newOne(UUID id, int numberOfGuest) {
        var orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName("1번 테이블");
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setOccupied(true);
        return orderTable;
    }

    public static OrderTable newOne(String orderTableName, int numberOfGuests, boolean isOccupied) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(orderTableName);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(isOccupied);
        return orderTable;
    }

    public static OrderTable newOne(UUID id, String orderTableName, int numberOfGuests, boolean isOccupied) {
        var orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(orderTableName);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(isOccupied);
        return orderTable;
    }
}
