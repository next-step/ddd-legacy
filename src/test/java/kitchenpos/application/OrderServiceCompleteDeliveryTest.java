package kitchenpos.application;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("주문 배달완료")
public class OrderServiceCompleteDeliveryTest extends OrderServiceTestSupport {
    @DisplayName("주문이 존재해야 한다.")
    @Test
    void orderNotFound() {
        // given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> testService.completeDelivery(orderId))
                // then
                .isInstanceOf(NoSuchElementException.class);
    }

    // TODO: 구현에 검증 코드를 넣을지, 이 테스트 코드를 지울지 결정해야 합니다.
    @Disabled
    @DisplayName("배달 주문이어야 한다.")
    @ParameterizedTest(name = "주문타입이 {0}이 아닌 DELIVERY여야 한다.")
    @EnumSource(value = OrderType.class, names = "DELIVERY", mode = EnumSource.Mode.EXCLUDE)
    void shouldBeDeliveryType(OrderType type) {
        // given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = new Order();
        order.setType(type);
        order.setStatus(OrderStatus.DELIVERING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> testService.completeDelivery(orderId))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달중인 주문이어야 한다.")
    @ParameterizedTest(name = "주문상태가 {0}이 아닌 DELIVERING여야 한다.")
    @EnumSource(value = OrderStatus.class, names = "DELIVERING", mode = EnumSource.Mode.EXCLUDE)
    void shouldBeDelivering(OrderStatus statusBeforeDeliveryCompleted) {
        //given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(statusBeforeDeliveryCompleted);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> testService.completeDelivery(orderId))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달중인 주문은 배달을 완료할 수 있다.")
    @Test
    void completeDelivery() {
        // given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        testService.completeDelivery(orderId);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }
}
