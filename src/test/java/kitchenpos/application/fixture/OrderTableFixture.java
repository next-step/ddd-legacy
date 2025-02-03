package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable createOrderTable(String name) {
        return createOrderTable(null, name, 0);
    }

    public static OrderTable createOrderTable(UUID id, String name, int numberOfGuests) {
        return createOrderTable(id, name, false, numberOfGuests);
    }

    public static OrderTable createOrderTable(UUID id, String name, boolean occupied, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    private OrderTableFixture() {
    }
}
