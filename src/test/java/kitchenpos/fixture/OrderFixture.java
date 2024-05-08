package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;

public class OrderFixture {

    public static OrderTable 주문_테이블_생성(String name) {
        return 주문_테이블_생성(name, 0);
    }

    public static OrderTable 주문_테이블_생성(String name, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }
}
