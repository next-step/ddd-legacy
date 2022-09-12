package kitchenpos.application;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("주문 제공")
public class OrderServiceServeTest extends OrderServiceTestSupport {
    @DisplayName("주문이 존재해야 한다.")
    @Test
    void orderNotFound() {
        //given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> testService.serve(orderId))
                // then
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("접수된 주문이어야 한다.")
    @ParameterizedTest(name = "주문상태가 {0}이 아닌 ACCEPTED이어야 한다.")
    @EnumSource(value = OrderStatus.class, names = "ACCEPTED", mode = EnumSource.Mode.EXCLUDE)
    void shouldBeWaiting(OrderStatus statusBeforeServed) {
        //given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = createOrderWithStatus(statusBeforeServed);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> testService.serve(orderId))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("접수중인 주문을 제공할 수 있다.")
    @Test
    void serve() {
        //given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var order = createOrderWithStatus(OrderStatus.ACCEPTED);

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        testService.serve(orderId);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }
}
