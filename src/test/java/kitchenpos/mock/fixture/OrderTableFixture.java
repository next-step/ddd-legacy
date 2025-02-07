package kitchenpos.mock.fixture;

import kitchenpos.domain.OrderTable;

public class OrderTableFixture {

    public static OrderTable create(final String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable create(final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

}
