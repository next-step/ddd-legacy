package kitchenpos.testfixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableTestFixture {

    public static OrderTable createOrderTableRequest(){
        return createOrderTableRequest("1번", false, 0);
    }

    public static OrderTable createOrderTableRequest(String name, boolean isOccupied, int guests){
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setOccupied(isOccupied);
        orderTable.setNumberOfGuests(guests);

        return orderTable;
    }

    public static OrderTable createOrderTable(UUID id, String name, boolean isOccupied, int guests){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setOccupied(isOccupied);
        orderTable.setNumberOfGuests(guests);

        return orderTable;
    }
}
