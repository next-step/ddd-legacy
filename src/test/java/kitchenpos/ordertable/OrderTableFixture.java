package kitchenpos.ordertable;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public static OrderTable orderTable(boolean occupied) {
        return new OrderTable("기본 테이블", 0, occupied);
    }

    public static OrderTable orderTable() {
        return new OrderTable("기본 테이블", 0, false);
    }

    public static OrderTable orderTable(String name) {
        return new OrderTable(name, 0, false);
    }

    public static OrderTable orderTableWithRandomId(String name) {
        return new OrderTable(UUID.randomUUID(), name, 0, false);
    }

    public static OrderTable changeOrderTableRequest(int numberOfGuest) {
        return new OrderTable(null, numberOfGuest, false);
    }
}
