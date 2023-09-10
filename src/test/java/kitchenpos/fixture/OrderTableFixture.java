package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable create() {
        return create(UUID.randomUUID(), "테이블", 0, false);
    }
    
    public static OrderTable create(final String name) {
        return create(UUID.randomUUID(), name, 0, false);
    }

    public static OrderTable create(final int numberOfGuests, final boolean occupied) {
        return create(UUID.randomUUID(), "테이블", numberOfGuests, occupied);
    }

    private static OrderTable create(final UUID id, final String name, final int numberOfGuests, final boolean occupied) {
        final OrderTable orderTable = new OrderTable();

        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);

        return orderTable;
    }

}
