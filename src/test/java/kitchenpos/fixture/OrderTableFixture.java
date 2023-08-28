package kitchenpos.fixture;

import kitchenpos.domain.OrderTable;
import org.aspectj.weaver.ast.Or;

import java.util.UUID;

public class OrderTableFixture {

    private static String ORDER_TABLE_NAME = "주문테이블1";

    public static OrderTable ORDER_TABLE() {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(UUID.randomUUID());
        orderTable.setName(ORDER_TABLE_NAME);
        orderTable.setNumberOfGuests(0);
        orderTable.setOccupied(false);

        return orderTable;
    }
}
