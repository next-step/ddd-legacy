package kitchenpos.application;

import kitchenpos.application.testFixture.MenuFixture;
import kitchenpos.application.testFixture.OrderFixture;
import kitchenpos.application.testFixture.OrderLineItemFixture;
import kitchenpos.application.testFixture.OrderTableFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Nested
    @DisplayName("주문을 생성시,")
    class Create {

        static Stream<Arguments> negativeQuantity() {
            return Stream.of(
                    Arguments.arguments(OrderFixture.newOne(DELIVERY, List.of(OrderLineItemFixture.newOne(-1))), DELIVERY),
                    Arguments.arguments(OrderFixture.newOne(TAKEOUT, List.of(OrderLineItemFixture.newOne(-1))), TAKEOUT)
            );
        }

        static Stream<Arguments> notFoundMenus() {
            return Stream.of(
                    Arguments.arguments(OrderFixture.newOne(DELIVERY, List.of(OrderLineItemFixture.newOne())), DELIVERY),
                    Arguments.arguments(OrderFixture.newOne(EAT_IN, List.of(OrderLineItemFixture.newOne())), EAT_IN),
                    Arguments.arguments(OrderFixture.newOne(TAKEOUT, List.of(OrderLineItemFixture.newOne())), TAKEOUT)
            );
        }

        static Stream<Arguments> notFoundOrderLineItems() {
            return Stream.of(
                    Arguments.arguments(DELIVERY, null),
                    Arguments.arguments(EAT_IN, Collections.emptyList()),
                    Arguments.arguments(TAKEOUT, null)
            );
        }

        @Test
        @DisplayName("매장 내 주문은 정상 생성된다.")
        void eatInOrderCreatedTest() {
            // given
            var menu = MenuFixture.newOne();
            var orderTable = OrderTableFixture.newOne(UUID.randomUUID(), "1번테이블", 4, true);
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var order = OrderFixture.newOneEatIn(orderTable, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            given(orderRepository.save(any())).willReturn(order);

            // when
            var actual = orderService.create(order);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getOrderTable()).isEqualTo(orderTable);
                softly.assertThat(actual.getType()).isEqualTo(EAT_IN);
                softly.assertThat(actual.getStatus()).isEqualTo(WAITING);
                softly.assertThat(actual.getOrderLineItems()).isEqualTo(orderLineItems);
            });
        }

        @Test
        @DisplayName("배달 주문이 생성된다.")
        void deliveryOrderCreatedTest() {
            // given
            var menu = MenuFixture.newOne();
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var deliveryAddress = "강남 테헤란로 1126-31";
            var order = OrderFixture.newOneDelivery(deliveryAddress, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderRepository.save(any())).willReturn(order);

            // when
            var actual = orderService.create(order);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getType()).isEqualTo(DELIVERY);
                softly.assertThat(actual.getStatus()).isEqualTo(WAITING);
                softly.assertThat(actual.getOrderLineItems()).isEqualTo(orderLineItems);
                softly.assertThat(actual.getDeliveryAddress()).isEqualTo(deliveryAddress);
            });
        }

        @Test
        @DisplayName("포장 주문이 생성된다.")
        void takeOutOrderCreatedTest() {
            // given
            var menu = MenuFixture.newOne();
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var order = OrderFixture.newOneTakeOut(WAITING, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderRepository.save(any())).willReturn(order);

            // when
            var actual = orderService.create(order);

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getType()).isEqualTo(TAKEOUT);
                softly.assertThat(actual.getStatus()).isEqualTo(WAITING);
                softly.assertThat(actual.getOrderLineItems()).isEqualTo(orderLineItems);
            });
        }

        @Test
        @DisplayName("[예외] 매장 내 주문일 경우 테이블의 상태는 '미사용중'이면 예외가 발생한다.")
        void notOccupiedExceptionTest() {
            // given
            var menu = MenuFixture.newOne();
            var orderTable = OrderTableFixture.newOne(UUID.randomUUID(), "1번테이블", 4, false);
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var order = OrderFixture.newOneEatIn(orderTable, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 매장 내 주문일 경우, 주문 테이블이 존재하지 않으면 예외가 발생한다.")
        void notFoundOrderTableExceptionTest() {
            // given
            var menu = MenuFixture.newOne();
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var order = OrderFixture.newOneEatIn(null, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest
        @DisplayName("[예외] 배달 주문일 경우 주소가 존재하지 않으면 예외가 발생한다.")
        @NullSource
        @EmptySource
        void notExistDeliveryAddressExceptionTest(String deliveryAddress) {
            // given
            var menu = MenuFixture.newOne();
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var order = OrderFixture.newOneDelivery(deliveryAddress, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[예외] 주문 아이템 가격의 총합이 메뉴의 가격과 다를 경우 예외가 발생한다.")
        void orderLineItemTotalPriceNotEqualsMenuPriceExceptionTest() {
            // given
            var menu = MenuFixture.newOne(BigDecimal.valueOf(5000));
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 6000));
            var order = OrderFixture.newOne(DELIVERY, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[예외] 주문 아이템의 메뉴가 비노출되어 있을 경우 예외가 발생한다.")
        void notFoundMenuExceptionTest() {
            // given
            var menu = MenuFixture.newOne(false);
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu));
            var order = OrderFixture.newOne(DELIVERY, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 주문 아이템의 메뉴가 비노출되어 있을 경우 예외가 발생한다.")
        void notDisplayedExceptionTest() {
            // given
            var menu = MenuFixture.newOne(false);
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu));
            var order = OrderFixture.newOne(DELIVERY, orderLineItems);
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("negativeQuantity")
        @DisplayName("[예외]배달주문, 포장 주문일 경우 주문 수량이 음수이면 예외가 발생한다.")
        void negativeQuantityExceptionTest(Order order, OrderType orderType) {
            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("notFoundMenus")
        @DisplayName("[예외] 주문 아이템 중 하나라도 메뉴가 존재하지 않으면 예외가 발생한다.")
        void notFoundMenusExceptionTest(Order order, OrderType orderType) {
            // given
            given(menuRepository.findAllByIdIn(any())).willReturn(Collections.emptyList());

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("notFoundOrderLineItems")
        @DisplayName("[예외] 주문 아이템이 존재하지 않을 경우 예외가 발생한다.")
        void notFoundOrderLineItemsExceptionTest(OrderType orderType, List<OrderLineItem> orderLineItems) {
            // given
            var order = OrderFixture.newOne(orderType, orderLineItems);

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[예외] 주문 타입이 존재하지 않을 경우 예외가 발생한다.")
        void nullOrderTypeExceptionTest() {
            // given
            var order = OrderFixture.newOne((OrderType) null);

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("'주문 승낙 완료'(ACCEPTED) 처리시")
    class Accept {

        @Test
        @DisplayName("배달 주문일 경우, 배달 라이더에게 배달을 요청하고 주문의 상태는 '수락'(ACCEPTED)로 변경된다.")
        void notServedTest() {
            // given
            var order = OrderFixture.newOneDelivery(WAITING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            var actual = orderService.accept(UUID.randomUUID());

            // then
            verify(kitchenridersClient, times(1)).requestDelivery(any(), any(), any());
            assertThat(actual.getStatus()).isEqualTo(ACCEPTED);
        }

        @Test
        @DisplayName("[예외] '대기중'(WAITING)가 아니면 예외가 발생한다.")
        void notWaitingExceptionTest() {
            // given
            var order = OrderFixture.newOneTakeOut(SERVED);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
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
            assertThat(actual.getStatus()).isEqualTo(SERVED);
        }

        @Test
        @DisplayName("[예외] '주문 수락 완료'(ACCEPTED)가 아니면 예외가 발생한다.")
        void notAcceptedTest() {
            // given
            var order = OrderFixture.newOneTakeOut(WAITING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
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
            assertThat(actual.getStatus()).isEqualTo(DELIVERING);
        }

        @Test
        @DisplayName("[예외] '서빙완료'(SERVED)가 아니면 예외가 발생한다.")
        void notServedTest() {
            // given
            var order = OrderFixture.newOneDelivery(WAITING);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("notDelivery")
        @DisplayName("[예외] '배달 주문'(DELIVERY)가 아니면 예외가 발생한다.")
        void notDeliveryExceptionTest(Order order, OrderType orderType) {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
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
            assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
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
