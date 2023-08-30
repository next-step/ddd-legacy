package kitchenpos.service;

import java.util.UUID;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {
    private final OrderTable orderTable;

    public OrderTableFixture() {
        orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번 테이블");
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);
    }

    public static OrderTableFixture builder() {
        return new OrderTableFixture();
    }

    public OrderTable build() {
        return orderTable;
    }

    public OrderTableFixture name(String name) {
        orderTable.setName(name);
        return this;
    }

    public OrderTableFixture occupied(boolean occupied) {
        orderTable.setOccupied(occupied);
        return this;
    }
}
