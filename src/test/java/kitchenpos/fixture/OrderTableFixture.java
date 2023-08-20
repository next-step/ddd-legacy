package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable create(String name, boolean occupied, int numberOfGuests){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(occupied);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }
}
