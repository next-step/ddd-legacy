
package kitchenpos.ordertable;

import kitchenpos.common.vo.Name;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.vo.NumberOfGuests;

import java.util.UUID;

public class OrderTableFixture {

    public static OrderTable orderTable() {
        return new OrderTable(UUID.randomUUID(), new Name("주문테이블", false), new NumberOfGuests(1));
    }
}


