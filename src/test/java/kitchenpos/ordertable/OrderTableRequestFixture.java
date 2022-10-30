
package kitchenpos.ordertable;

import kitchenpos.ordertable.dto.OrderTableRequest;

public class OrderTableRequestFixture {
    
    public static OrderTableRequest orderTableRequest() {
        return new OrderTableRequest("주문테이블명");
    }
}


