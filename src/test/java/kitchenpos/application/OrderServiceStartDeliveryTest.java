package kitchenpos.application;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("주문 배달시작")
public class OrderServiceStartDeliveryTest extends OrderServiceTestSupport {
    @DisplayName("주문이 존재해야 한다.")
    @Test
    void orderNotFound() {
        // given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> testService.startDelivery(orderId))
                // then
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달 주문이어야 한다.")
    @ParameterizedTest(name = "주문타입이 {0}이 아닌 DELIVERY여야 한다.")
    @EnumSource(value = OrderType.class, names = "DELIVERY", mode = EnumSource.Mode.EXCLUDE)
    void shouldBeDeliveryType(OrderType type) {
        // given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = createOrderOfType(type);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> testService.startDelivery(orderId))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제공된 주문이어야 한다.")
    @ParameterizedTest(name = "주문상태가 {0}이 아닌 SERVED여야 한다.")
    @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
    void shouldBeServed(OrderStatus statusBeforeStartingDelivery) {
        //given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = createOrderBy(OrderType.DELIVERY, statusBeforeStartingDelivery);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> testService.startDelivery(orderId))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제공된 주문은 배달을 시작할 수 있다.")
    @Test
    void startDelivery() {
        // given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = createOrderBy(OrderType.DELIVERY, OrderStatus.SERVED);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        testService.startDelivery(orderId);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }
}
