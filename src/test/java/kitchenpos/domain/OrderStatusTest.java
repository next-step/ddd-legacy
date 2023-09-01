package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderStatusTest {
    @Test
    @DisplayName("주문상태는 대기, 접수, 주문제공, 배달중, 배달완료, 완료를 가진다.")
    void status() {
        assertAll(
                () -> assertThat(OrderStatus.WAITING).isNotNull(),
                () -> assertThat(OrderStatus.ACCEPTED).isNotNull(),
                () -> assertThat(OrderStatus.SERVED).isNotNull(),
                () -> assertThat(OrderStatus.DELIVERING).isNotNull(),
                () -> assertThat(OrderStatus.DELIVERED).isNotNull(),
                () -> assertThat(OrderStatus.COMPLETED).isNotNull()
        );
    }

}
