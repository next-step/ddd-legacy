package kitchenpos.application;

import kitchenpos.application.testFixture.OrderFixture;
import kitchenpos.application.testFixture.OrderTableFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("주문(Order) 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test
    void create() {
    }

    @Test
    void accept() {
    }

    @Nested
    @DisplayName("'서빙완료'(SERVED) 처리시")
    class Serve {

        @Test
        @DisplayName("'서빙완료'(SERVED)로 주문 상태가 변경 된다.")
        void servedTest() {
            // given
            var order = OrderFixture.newOneTakeOut(ACCEPTED);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when
            var actual = orderService.serve(UUID.randomUUID());

            // then
            Assertions.assertThat(actual.getStatus()).isEqualTo(SERVED);
        }

        @Test
        @DisplayName("[예외] '주문 수락 완료'(ACCEPTED)가 아니면 예외가 발생한다.")
        void notServedTest() {
            // given
            var order = OrderFixture.newOneTakeOut(WAITING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("배달 시작 처리시")
    class StartDelivery {

        static Stream<Arguments> notDelivery() {
            var orderTable = OrderTableFixture.newOne("주문 테이블 1번");
            return Stream.of(
                    Arguments.arguments(OrderFixture.newOneTakeOut(SERVED), TAKEOUT),
                    Arguments.arguments(OrderFixture.newOneEatIn(orderTable, SERVED), EAT_IN)
            );
        }

        @Test
        @DisplayName("'배송중'(DELIVERING)으로 배송 상태가 변경된다.")
        void changedOrderStatusTest() {
            // given
            var order = OrderFixture.newOneDelivery(SERVED);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when
            var actual = orderService.startDelivery(UUID.randomUUID());

            // then
            Assertions.assertThat(actual.getStatus()).isEqualTo(DELIVERING);
        }

        @Test
        @DisplayName("[예외] '서빙완료'(SERVED)가 아니면 예외가 발생한다.")
        void notServedTest() {
            // given
            var order = OrderFixture.newOneDelivery(WAITING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("notDelivery")
        @DisplayName("[예외] '배달 주문'(DELIVERY)가 아니면 예외가 발생한다.")
        void notDeliveryExceptionTest(Order order, OrderType orderType) {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("'배달완료'(DELIVERED) 처리시,")
    class CompleteDelivery {

        @Test
        @DisplayName("'배달완료'(DELIVERED)로 주문 상태가 변경된다.")
        void changedOrderStatusTest() {
            // given
            var order = OrderFixture.newOneDelivery(DELIVERING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when
            var actual = orderService.completeDelivery(UUID.randomUUID());

            // then
            assertThat(actual.getStatus()).isEqualTo(DELIVERED);
        }

        @Test
        @DisplayName("[예외] '배달중'(DELIVERING) 주문 상태가 아니면 예외가 발생한다.")
        void orderStatusDeliveringExceptionTest() {
            // given
            var order = OrderFixture.newOneDelivery(WAITING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("주문 전체를 조회한다.")
    @Test
    void findAll() {
        // given
        var orderTable_1번 = OrderTableFixture.newOne("1번 테이블");
        var orderTable_2번 = OrderTableFixture.newOne("2번 테이블");
        var order_1번 = OrderFixture.newOneEatIn(orderTable_1번, WAITING);
        var order_2번 = OrderFixture.newOneEatIn(orderTable_2번, SERVED);
        given(orderRepository.findAll()).willReturn(List.of(order_1번, order_2번));

        // when
        var actual = orderService.findAll();

        // then
        assertThat(actual).containsAll(List.of(order_1번, order_2번));
    }

    @Nested
    @DisplayName("주문 처리 완료(Completed) 처리시,")
    class Complete {

        static Stream<Arguments> changedOrderStatus() {
            return Stream.of(
                    Arguments.arguments(OrderFixture.newOneTakeOut(SERVED), TAKEOUT),
                    Arguments.arguments(OrderFixture.newOneDelivery(DELIVERED), DELIVERY)
            );
        }

        static Stream<Arguments> notServed() {
            var orderTable = OrderTableFixture.newOne("1번 테이블");
            return Stream.of(
                    Arguments.arguments(OrderFixture.newOneEatIn(orderTable, WAITING), EAT_IN),
                    Arguments.arguments(OrderFixture.newOneTakeOut(WAITING), TAKEOUT)
            );
        }

        @DisplayName("포장주문과 배달주문은 '주문 처리 완료'(Completed)로 주문 상태가 변경된다.")
        @ParameterizedTest(name = "{1}")
        @MethodSource("changedOrderStatus")
        void completedOrderStatusTest(Order order, OrderType orderType) {
            // given
            var orderId = UUID.randomUUID();
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when
            var actual = orderService.complete(orderId);

            // then
            assertThat(actual.getStatus()).isEqualTo(COMPLETED);
        }

        @DisplayName("매장내 주문일 경우 테이블의 손님 수는 0명, 테이블 상태는 '미사용'중, 주문상태는 '주문 처리 완료'로 초기화된다.")
        @Test
        void initEatInOrderTest() {
            // given
            var orderTableId = UUID.randomUUID();
            var orderTable = OrderTableFixture.newOne(orderTableId, "1번 테이블", 4, true);
            var orderId = UUID.randomUUID();
            var order = OrderFixture.newOneEatIn(orderId, orderTable, SERVED);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            // when
            var actual = orderService.complete(orderId);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getOrderTable().getNumberOfGuests()).isZero();
                softly.assertThat(actual.getOrderTable().isOccupied()).isFalse();
                softly.assertThat(actual.getStatus()).isEqualTo(COMPLETED);
            });
        }

        @DisplayName("[예외] 미존재하는 주문이면 예외가 발생한다.")
        @Test
        void notFoundOrderTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 배달 주문은, '배달완료(Delivered)' 상태가 아니면 예외가 발생한다.")
        @Test
        void notDeliveredDeliveryTest() {
            // given
            var order = OrderFixture.newOneDelivery(WAITING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[예외] 포장 주문과 매장내 식사 주문은, '서빙완료(Served)' 상태가 아니면 예외가 발생한다.")
        @ParameterizedTest(name = "{1}")
        @MethodSource("notServed")
        void notServedTest(Order order, OrderType orderType) {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
