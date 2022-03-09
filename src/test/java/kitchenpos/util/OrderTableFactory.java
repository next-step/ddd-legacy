package kitchenpos.util;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFactory {
    private OrderTableFactory() {
    }


    public static OrderTable createOrderTable(UUID id, String name, Integer numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setEmpty(empty);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

}
