package kitchenpos.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTable createOrderTable() {
        return createOrderTable(false);
    }

    public static OrderTable createOrderTable(int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("테이블");
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(true);
        return orderTable;
    }

    public static OrderTable createOrderTable(boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("테이블");
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable createOrderTable(String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        return orderTable;
    }
}
