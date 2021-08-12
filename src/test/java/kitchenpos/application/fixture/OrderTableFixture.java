package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public static OrderTable generateEmptyOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(0);
        return orderTable;
    }

    public static OrderTable generateOccupiedOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("4인 테이블");
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);
        return orderTable;
    }
}
