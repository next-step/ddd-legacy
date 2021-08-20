package kitchenpos.order_table.fixture;

import kitchenpos.domain.OrderTable;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable 주문_테이블_요청(final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderTable 주문_테이블_요청(final String name) {
        OrderTable request = new OrderTable();
        request.setName(name);
        return request;
    }

    public static OrderTable 주문_테이블(final UUID id, final String name, final boolean isEmpty, final int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setEmpty(isEmpty);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }
}
