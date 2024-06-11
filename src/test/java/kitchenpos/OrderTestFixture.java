package kitchenpos;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderTable;

public class OrderTestFixture {
    private OrderTestFixture() {
    }

    public static OrderTable createOrderTableRequest(String name) {
        OrderTable table = new OrderTable();
        table.setName(name);
        return table;
    }

    public static OrderTable getSavedOrderTable(OrderTableService orderTableService, String name) {
        return orderTableService.create(createOrderTableRequest(name));
    }

    public static OrderTable changeOrderTableRequest(int numberOfGuest) {
        OrderTable table = new OrderTable();
        table.setNumberOfGuests(numberOfGuest);
        return table;
    }
}
