package kitchenpos.order.domain;

import kitchenpos.domain.Name;
import kitchenpos.ordertable.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

@DisplayName("주문 테이블")
class OrderTableTest {

    @DisplayName("주문 테이블 생성 시 주문 테이블명을 입력받는다.")
    @Test
    void createOrderTable() {
        assertThatNoException().isThrownBy(() -> new OrderTable(new Name("주문테이블명", false)));
    }
}
