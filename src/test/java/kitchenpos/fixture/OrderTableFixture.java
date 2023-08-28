package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable TEST_ORDER_TABLE() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(false);
        orderTable.setName("1번 테이블");
        orderTable.setNumberOfGuests(0);
        return orderTable;
    }
}
