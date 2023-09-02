package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableTestFixture {

    public OrderTable createOrderTable(String name, boolean occupied, int numberOfGuest){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuest);
        return orderTable;
    }

    public OrderTable createOrderTable(boolean occupied, int numberOfGuest){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("testOrderTable");
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuest);
        return orderTable;
    }

    public OrderTable createOrderTable(int numberOfGuest){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("testOrderTable");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(numberOfGuest);
        return orderTable;
    }

}
