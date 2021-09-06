package kitchenpos.utils.fixture;

import kitchenpos.domain.OrderTable;

import static java.util.UUID.randomUUID;

public class OrderTableFixture {

    public static OrderTable 주문테이블() {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(randomUUID());
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(0);
        orderTable.setName("주문테이블");
        return orderTable;
    }

    public static OrderTable 앉은테이블() {
        final OrderTable orderTable = 주문테이블();
        orderTable.setEmpty(false);
        return orderTable;
    }
}
