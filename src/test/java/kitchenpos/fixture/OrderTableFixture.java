package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable createUsedTable() {
        return create("1번 테이블", 5, true);
    }

    public static OrderTable create(final boolean occupied) {
        return create("1번 테이블", 0, occupied);
    }

    public static OrderTable create(final String name, final int numberOfGuests, final boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}
