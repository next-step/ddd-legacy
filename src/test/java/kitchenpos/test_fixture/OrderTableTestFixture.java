package kitchenpos.test_fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableTestFixture {
    private OrderTable orderTable;

    private OrderTableTestFixture(OrderTable orderTable) {
        this.orderTable = orderTable;
    }

    public static OrderTableTestFixture create() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("테스트 테이블");
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(true);
        return new OrderTableTestFixture(orderTable);
    }

    public OrderTableTestFixture changeId(UUID id) {
        OrderTable newOrderTable = new OrderTable();
        newOrderTable.setId(id);
        newOrderTable.setName(orderTable.getName());
        newOrderTable.setNumberOfGuests(orderTable.getNumberOfGuests());
        newOrderTable.setOccupied(orderTable.isOccupied());
        this.orderTable = newOrderTable;
        return this;
    }

    public OrderTable getOrderTable() {
        return orderTable;
    }
}
