package kitchenpos.helper;


import kitchenpos.domain.OrderTable;

import java.util.UUID;

public final class OrderTableHelper {

    public static final String DEFAULT_NAME = "테스트 기본 테이블명";

    private OrderTableHelper() {
    }

    public static OrderTable create() {
        return create(DEFAULT_NAME);
    }

    public static OrderTable create(int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable create(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        return orderTable;
    }

}
