package kitchenpos.application;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static final OrderTable 일번_테이블 = new OrderTable();
    public static final OrderTable 삼번_테이블 = new OrderTable();

    static {
        initialize(일번_테이블, "1번");
        initialize(삼번_테이블, "3번");
    }

    private static void initialize(OrderTable orderTable, String name) {
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(true);
    }
}
