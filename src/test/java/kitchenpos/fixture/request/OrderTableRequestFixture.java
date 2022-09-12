package kitchenpos.fixture.request;

import kitchenpos.domain.OrderTable;

public class OrderTableRequestFixture {
    public static OrderTable changeNumberOfGuestsRequest() {
        return changeNumberOfGuestsRequest(2);
    }

    public static OrderTable changeNumberOfGuestsRequest(int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable createOrderTableRequest() {
        return createOrderTableRequest("1번");
    }

    public static OrderTable createOrderTableRequest(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }
}
