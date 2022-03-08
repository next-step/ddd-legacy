package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTypeTest {

    @DisplayName("포장, 주문, 식당식사를 선택할 수 있다.")
    @Test
    void order_type() {
        assertThat(OrderType.values()).containsExactlyInAnyOrder(
                OrderType.TAKEOUT, OrderType.EAT_IN, OrderType.DELIVERY
        );
    }
}
