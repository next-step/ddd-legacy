package kitchenpos.application.fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public record OrderTableFixture(UUID id, String 테이블명, int 인원수, boolean 사용여부) {

    public static final String DEFAULT_ORDER_TABLE_NAME = "1번 테이블";
    public static final int DEFAULT_ORDER_TABLE_GUESTS = 3;
    public static final boolean DEFAULT_ORDER_TABLE_OCCUPIED = false;

    public static OrderTableFixture init() {
        return new OrderTableFixture(
            UUID.randomUUID(),
            DEFAULT_ORDER_TABLE_NAME,
            DEFAULT_ORDER_TABLE_GUESTS,
            DEFAULT_ORDER_TABLE_OCCUPIED
        );
    }

    public static OrderTableFixture test(String 테이블명, int 인원수, boolean 사용여부) {
        return new OrderTableFixture(
            UUID.randomUUID(),
            테이블명,
            인원수,
            사용여부
        );
    }

    public OrderTable create() {
        var orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(테이블명);
        orderTable.setNumberOfGuests(인원수);
        orderTable.setOccupied(사용여부);
        return orderTable;
    }
}

