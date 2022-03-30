package kitchenpos.application;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static final OrderTable 일번_식탁 = new OrderTable();
    public static final OrderTable 삼번_식탁 = new OrderTable();
    public static final OrderTable 착석_식탁 = new OrderTable();

    static {
        initialize(일번_식탁, "1번");
        initialize(삼번_식탁, "3번");
        initialize(착석_식탁, "5번", false);
    }

    private static void initialize(OrderTable orderTable, String name) {
        initialize(orderTable, name, true);
    }

    private static void initialize(OrderTable orderTable, String name, boolean empty) {
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(empty);
    }
}
