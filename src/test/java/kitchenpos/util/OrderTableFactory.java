package kitchenpos.util;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
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

    public static OrderTable createOrderTableWithName(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable createNotEmptyOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(5);
        orderTable.setName("sit table");
        return orderTable;
    }

    public static Order createOrderWithNotOrderComplete(OrderTable givenOrderTable) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTable(givenOrderTable);
        order.setOrderTableId(givenOrderTable.getId());
        order.setOrderDateTime(LocalDateTime.now());
        return order;
    }
}
