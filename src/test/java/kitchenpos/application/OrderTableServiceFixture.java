package kitchenpos.application;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderTable;

public final class OrderTableServiceFixture {

    private OrderTableServiceFixture() {

    }

    public static OrderTable orderTable() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("주문 테이블 이름");
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static List<OrderTable> orderTables() {
        List<OrderTable> orderTables = new ArrayList<>();
        orderTables.add(orderTable());
        return orderTables;
    }

}
