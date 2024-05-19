package kitchenpos.fixture.application;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable 테이블_생성(boolean occupied) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable 테이블_1번_생성(String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable 손님_인원_변경(int number) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(number);
        return orderTable;
    }
}
