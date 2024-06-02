package kitchenpos.fixture;

import java.util.UUID;
import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTable createRequest(final String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable changeNumberOfGuestsRequest(final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable createNumber1(){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번 테이블");
        return orderTable;
    }
}
