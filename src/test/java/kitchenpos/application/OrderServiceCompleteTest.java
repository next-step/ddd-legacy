package kitchenpos.application;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@DisplayName("주문 완료")
public class OrderServiceCompleteTest extends OrderServiceTestSupport {
    @DisplayName("주문이 존재해야 한다.")
    @Test
    void orderNotFound() {
        // given
        final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> testService.complete(orderId))
                // then
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달")
    @Nested
    class Delivery {
        @DisplayName("주문 배달완료 상태여야 한다.")
        @ParameterizedTest(name = "{0} 상태가 아니라 DELIVERED여야 한다.")
        @EnumSource(value = OrderStatus.class, names = "DELIVERED", mode = EnumSource.Mode.EXCLUDE)
        void shouldBeDeliveredStatus(OrderStatus statusBeforeCompleted) {
            // given
            final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setStatus(statusBeforeCompleted);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            assertThatThrownBy(() -> testService.complete(orderId))
                    // then
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문을 완료할 수 있다.")
        @Test
        void complete() {
            // given
            final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERED);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            testService.complete(orderId);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @DisplayName("포장")
    @Nested
    class Takeout {
        @DisplayName("주문 제공됨 상태여야 한다.")
        @ParameterizedTest(name = "{0} 상태가 아니라 SERVED여야 한다.")
        @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
        void shouldBeServedStatus(OrderStatus statusBeforeCompleted) {
            // given
            final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var order = new Order();
            order.setType(OrderType.TAKEOUT);
            order.setStatus(statusBeforeCompleted);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            assertThatThrownBy(() -> testService.complete(orderId))
                    // then
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문을 완료할 수 있다.")
        @Test
        void complete() {
            // given
            final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var order = new Order();
            order.setType(OrderType.TAKEOUT);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            testService.complete(orderId);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @DisplayName("매장식사")
    @Nested
    class EatIn {
        @DisplayName("주문 제공됨 상태여야 한다.")
        @ParameterizedTest(name = "{0} 상태가 아니라 SERVED여야 한다.")
        @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
        void shouldBeServedStatus(OrderStatus statusBeforeCompleted) {
            // given
            final var orderTable = new OrderTable();
            orderTable.setOccupied(true);
            orderTable.setNumberOfGuests(3);

            final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setStatus(statusBeforeCompleted);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            assertThatThrownBy(() -> testService.complete(orderId))
                    // then
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문을 완료할 수 있다.")
        @Test
        void complete() {
            // given
            final var orderTable = new OrderTable();
            orderTable.setOccupied(true);
            orderTable.setNumberOfGuests(3);

            final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setStatus(OrderStatus.SERVED);
            order.setOrderTable(orderTable);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            testService.complete(orderId);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("매장테이블에 완료되지 않은 주문이 있으면 매장테이블을 비우고, 아니면 매장테이블을 비우지 않는다.")
        @ParameterizedTest(name = "테이블 차지여부={0}, 손님수={1}인 테이블에, 다른 주문 존재여부={2}이면 주문 완료 후 차지여부={3}, 손님수={4}가 된다.")
        @CsvSource({
                "true, 3, true, true, 3",
                "true, 3, false, false, 0"
        })
        void complete(
                boolean occupiedBeforeCompleted,
                int numberOfGuestsBeforeCompleted,
                boolean anyCompletedOrderExists,
                boolean occupiedAfterCompleted,
                int numberOfGuestsAfterCompleted
        ) {
            // given
            final var orderTable = new OrderTable();
            orderTable.setOccupied(occupiedBeforeCompleted);
            orderTable.setNumberOfGuests(numberOfGuestsBeforeCompleted);

            final var orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setStatus(OrderStatus.SERVED);
            order.setOrderTable(orderTable);

            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                    .willReturn(anyCompletedOrderExists);

            // when
            testService.complete(orderId);

            // then
            assertAll(
                    () -> assertThat(orderTable.isOccupied()).isEqualTo(occupiedAfterCompleted),
                    () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(numberOfGuestsAfterCompleted)
            );
        }
    }
}
