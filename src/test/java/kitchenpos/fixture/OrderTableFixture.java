package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTable.Builder anOrderTableRequest() {
        return OrderTable.builder()
                .name("기본 테이블1")
                .numberOfGuests(0)
                .occupied(false);
    }
}
