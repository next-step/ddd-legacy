package kitchenpos.testfixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    private OrderTableFixture() {

    }

    public static OrderTable createOrderTable(
            UUID id,
            String name,
            int numberOfGuests,
            boolean occupied
    ) {
        var orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable createOrderTable(
            String name,
            int numberOfGuests
    ) {
        return createOrderTable(UUID.randomUUID(), name, numberOfGuests, true);
    }

    public static OrderTable createOrderTable(
            String name,
            int numberOfGuests,
            boolean occupied
    ) {
        return createOrderTable(UUID.randomUUID(), name, numberOfGuests, occupied);
    }

    public static OrderTable copy(OrderTable orderTable) {
        var copiedOrderTable = new OrderTable();
        copiedOrderTable.setId(orderTable.getId());
        copiedOrderTable.setNumberOfGuests(orderTable.getNumberOfGuests());
        copiedOrderTable.setName(orderTable.getName());
        copiedOrderTable.setOccupied(orderTable.isOccupied());
        return copiedOrderTable;
    }

}
