package kitchenpos.fixtures;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixtures {

    public static OrderTable orderTableCreate(final String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable orderTableCreate(final UUID uuid, final String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable orderTableSit(final UUID uuid, final String name, final boolean occupied, final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable orderTableClear(final UUID uuid, final String name, final boolean occupied, final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable orderTableChange(final UUID uuid, final String name, final boolean occupied, final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(uuid);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }
}
