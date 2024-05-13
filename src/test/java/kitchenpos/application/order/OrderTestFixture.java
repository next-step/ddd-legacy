package kitchenpos.application.order;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTestFixture {

    public static OrderTable aOrderTable(String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
        return orderTable;
    }

    public static OrderTable aOrderTableWithGuests(String name, int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(true);
        return orderTable;
    }
}
