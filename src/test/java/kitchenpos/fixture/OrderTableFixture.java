package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {
    private OrderTableFixture() {
    }

    public static OrderTable createOrderTable(String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }
}
