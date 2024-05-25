package kitchenpos.application;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable createOrderTableRequest(final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("테이블2");
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable createOrderTableRequest(final String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(4);
        orderTable.setOccupied(true);
        return orderTable;
    }

    public static OrderTable createOrderTableRequest(){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("테이블1");
        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(4);
        return orderTable;
    }
    public static OrderTable createOrderTableRequest(final String name, final int numberOfGuests){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable createOrderTableRequest(final String name, final int numberOfGuests, final boolean occupied){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

}
