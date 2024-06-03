package kitchenpos.application.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable createOrderTable(String name, int numberOfGuests, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable createRequest(String name) {
        OrderTable request = new OrderTable();
        request.setName(name);
        return request;
    }

    public static OrderTable createRequest(int numberOfGuests) {
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(numberOfGuests);
        return request;
    }
}
