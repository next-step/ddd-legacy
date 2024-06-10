package kitchenpos;

import kitchenpos.domain.OrderTable;

public class OrderTestFixture {
    private OrderTestFixture() {
    }

    public static OrderTable createOrderTableRequest(String name) {
        OrderTable table = new OrderTable();
        table.setName(name);
        return table;
    }
}
