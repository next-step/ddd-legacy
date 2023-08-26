package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    private static final String DEFAULT_NAME = "기본 이름";

    private OrderTableFixture() {
    }

    public static OrderTable create() {
        return create(UUID.randomUUID(), DEFAULT_NAME, 0, false);
    }

    public static OrderTable create(UUID id, String name) {
        return create(id, name, 0, false);
    }

    public static OrderTable create(UUID id, String name, int numberOfGuest, boolean occupied) {
        OrderTable result = new OrderTable();
        result.setId(id);
        result.setName(name);
        result.setNumberOfGuests(numberOfGuest);
        result.setOccupied(occupied);
        return result;
    }

}
