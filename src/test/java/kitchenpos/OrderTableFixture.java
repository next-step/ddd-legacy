package kitchenpos;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {
    public static OrderTable orderTable() {
        return orderTable(true, 0);
    }

    public static OrderTable orderTable(boolean empty, int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}
