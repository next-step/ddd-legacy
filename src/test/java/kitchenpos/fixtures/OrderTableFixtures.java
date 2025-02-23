package kitchenpos.fixtures;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixtures {

    public static OrderTable createOrderTable(final String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable createOrderTable(final UUID uuid, final String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable sitOrderTable(final UUID uuid, final String name, final boolean occupied, final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable clearOrderTable(final UUID uuid, final String name, final boolean occupied, final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable changeOrderTable(final UUID uuid, final String name, final boolean occupied, final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    /**
     * OrderServiceTest
     */
    public static OrderTable orderTable(final boolean occupied, final int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
