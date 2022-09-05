package kitchenpos.application.stub;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableStub {

    public static OrderTable create(final String name, final int numberOfGuests, final boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
