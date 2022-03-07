package kitchenpos.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.domain.OrderStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @DisplayName("주문 상태에는 대기, 승인, 조리, 배달중, 배달중, 배달완료, 주문완료가 있다.")
    @Test
    void status() {
        assertThat(OrderStatus.values())
                .containsExactlyInAnyOrder(WAITING, ACCEPTED, SERVED, DELIVERING, DELIVERED, COMPLETED);
    }
}