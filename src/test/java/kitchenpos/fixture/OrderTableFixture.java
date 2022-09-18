package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTable createOrderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("주문 테이블");
        return orderTable;
    }

    public static OrderTable createOrderTable(String name) {
        OrderTable orderTable = createOrderTable();
        orderTable.setName(name);
        return orderTable;
    }
}
