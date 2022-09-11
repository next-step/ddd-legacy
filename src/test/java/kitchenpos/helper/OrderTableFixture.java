package kitchenpos.helper;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    private static final String DEFAULT_ORDER_TABLE_NAME = "1번 테이블";
    private static final int DEFAULT_ORDER_TABLE_NUMBER_OF_GUESTS = 0;

    public static OrderTable create(int numberOfGuests, boolean occupied) {
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(DEFAULT_ORDER_TABLE_NAME);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable create(boolean occupied) {
        return create(DEFAULT_ORDER_TABLE_NUMBER_OF_GUESTS, occupied);
    }

    public static OrderTable create(int numberOfGuests) {
        return create(numberOfGuests, true);
    }

    public static OrderTable create() {
        return create(DEFAULT_ORDER_TABLE_NUMBER_OF_GUESTS);
    }
}
