package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTypeTest {
    @Test
    @DisplayName("주문타입은 배달 주문, 포장 주문, 매장주문을 가진다.")
    void type() {
        assertAll(
                () -> assertThat(OrderType.DELIVERY).isNotNull(),
                () -> assertThat(OrderType.TAKEOUT).isNotNull(),
                () -> assertThat(OrderType.EAT_IN).isNotNull()
        );
    }

}
