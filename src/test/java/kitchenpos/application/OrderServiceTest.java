package kitchenpos.application;

import kitchenpos.application.testfixture.MenuFixture;
import kitchenpos.application.testfixture.OrderFixture;
import kitchenpos.application.testfixture.OrderLineItemFixture;
import kitchenpos.application.testfixture.OrderTableFixture;
import kitchenpos.domain.*;
import kitchenpos.domain.testfixture.KitchenridersFakeClient;
import kitchenpos.domain.testfixture.MenuFakeRepository;
import kitchenpos.domain.testfixture.OrderFakeRepository;
import kitchenpos.domain.testfixture.OrderTableFakeRepository;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문(Order) 서비스 테스트")
class OrderServiceTest {

    private OrderRepository orderRepository;

    private MenuRepository menuRepository;

    private OrderTableRepository orderTableRepository;

    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderFakeRepository();
        menuRepository = new MenuFakeRepository();
        orderTableRepository = new OrderTableFakeRepository();
        kitchenridersClient = new KitchenridersFakeClient();
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
            var order = given_for_매장_내_주문은_정상_생성된다();

            // when
            var actual = orderService.create(order);

            // then
            assertThat(actual).isNotNull();
        }

        private Order given_for_매장_내_주문은_정상_생성된다() {
            var menu = menuRepository.save(MenuFixture.newOne());

            var orderTableToSave = OrderTableFixture.newOne(UUID.randomUUID(), "1번테이블", 4, true);
            var orderTable = orderTableRepository.save(orderTableToSave);

            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));

            return OrderFixture.newOneEatIn(orderTable, orderLineItems);
        }

        @Test
        @DisplayName("배달 주문이 생성된다.")
        void deliveryOrderCreatedTest() {
            // given
            var order = given_for_배달주문이_생성된다();

            // when
            var actual = orderService.create(order);

            // then
            assertThat(actual).isNotNull();
        }

        private Order given_for_배달주문이_생성된다() {
            var menu = MenuFixture.newOne();
            menuRepository.save(menu);
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var deliveryAddress = "강남 테헤란로 1126-31";
            return OrderFixture.newOneDelivery(deliveryAddress, orderLineItems);
        }

        @Test
        @DisplayName("포장 주문이 생성된다.")
        void takeOutOrderCreatedTest() {
            // given
            var order = given_for_포장_주문이_생성된다();

            // when
            var actual = orderService.create(order);

            // then
            assertThat(actual).isNotNull();
        }

        private Order given_for_포장_주문이_생성된다() {
            var menu = menuRepository.save(MenuFixture.newOne());
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var order = OrderFixture.newOneTakeOut(WAITING, orderLineItems);
            return orderRepository.save(order);
        }

        @Test
        @DisplayName("[예외] 매장 내 주문일 경우 테이블의 상태는 '미사용중'이면 예외가 발생한다.")
        void notOccupiedExceptionTest() {
            // given
            var order = given_테이블상태가_미사용중();

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        private Order given_테이블상태가_미사용중() {
            var menu = menuRepository.save(MenuFixture.newOne());

            var orderTable = orderTableRepository.save(
                    OrderTableFixture.newOne(UUID.randomUUID(), "1번테이블", 4, false));

            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            var order = OrderFixture.newOneEatIn(orderTable, orderLineItems);
            return orderRepository.save(order);
        }

        @Test
        @DisplayName("[예외] 매장 내 주문일 경우, 주문 테이블이 존재하지 않으면 예외가 발생한다.")
        void notFoundOrderTableExceptionTest() {
            // given
            var order = given_주문테이블이_존재하지_않으면();

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(NoSuchElementException.class);
        }

        private Order given_주문테이블이_존재하지_않으면() {
            var menu = MenuFixture.newOne();
            menuRepository.save(menu);
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            return OrderFixture.newOneEatIn(null, orderLineItems);
        }

        @ParameterizedTest
        @DisplayName("[예외] 배달 주문일 경우 주소가 존재하지 않으면 예외가 발생한다.")
        @NullSource
        @EmptySource
        void notExistDeliveryAddressExceptionTest(String deliveryAddress) {
            // given
            var order = given_배달주소가_존재하지않으면(deliveryAddress);

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        private Order given_배달주소가_존재하지않으면(String deliveryAddress) {
            var menu = menuRepository.save(MenuFixture.newOne());
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 5000));
            return OrderFixture.newOneDelivery(deliveryAddress, orderLineItems);
        }

        @Test
        @DisplayName("[예외] 주문 아이템 가격의 총합이 메뉴의 가격과 다를 경우 예외가 발생한다.")
        void orderLineItemTotalPriceNotEqualsMenuPriceExceptionTest() {
            // given
            var order = given_주문아이템의_가격의_총합이_메뉴의_가격과_다를_경우();

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        private Order given_주문아이템의_가격의_총합이_메뉴의_가격과_다를_경우() {
            var menu = menuRepository.save(MenuFixture.newOne(BigDecimal.valueOf(5000)));
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu, 6000));
            return OrderFixture.newOne(DELIVERY, orderLineItems);
        }

        @Test
        @DisplayName("[예외] 주문 아이템의 메뉴가 비노출되어 있을 경우 예외가 발생한다.")
        void notDisplayedExceptionTest() {
            // given
            var order = given_주문아이템의_메뉴가_비노출되어_있을경우();

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        private Order given_주문아이템의_메뉴가_비노출되어_있을경우() {
            var menu = menuRepository.save(MenuFixture.newOne(false));
            var orderLineItems = List.of(OrderLineItemFixture.newOne(menu));
            return OrderFixture.newOne(DELIVERY, orderLineItems);
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
            var order = OrderFixture.newOne(null);

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("'주문 승낙 완료'(ACCEPTED) 처리시")
    class Accept {

        @Test
        @DisplayName("배달 주문일 경우, 주문의 상태는 '수락'(ACCEPTED)로 변경된다.")
        void notServedTest() {
            // given
            var order = orderRepository.save(OrderFixture.newOneDelivery(WAITING));

            // when & then
            var actual = orderService.accept(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(ACCEPTED);
        }

        @Test
        @DisplayName("[예외] '대기중'(WAITING)가 아니면 예외가 발생한다.")
        void notWaitingExceptionTest() {
            // given
            var order = orderRepository.save(OrderFixture.newOneTakeOut(SERVED));

            // when & then
            assertThatThrownBy(() -> orderService.accept(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
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
            var order = orderRepository.save(OrderFixture.newOneTakeOut(ACCEPTED));

            // when
            var actual = orderService.serve(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(SERVED);
        }

        @Test
        @DisplayName("[예외] '주문 수락 완료'(ACCEPTED)가 아니면 예외가 발생한다.")
        void notAcceptedTest() {
            // given
            var order = orderRepository.save(OrderFixture.newOneTakeOut(WAITING));

            // when & then
            assertThatThrownBy(() -> orderService.serve(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
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
            var order = orderRepository.save(OrderFixture.newOneDelivery(SERVED));

            // when
            var actual = orderService.startDelivery(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(DELIVERING);
        }

        @Test
        @DisplayName("[예외] '서빙완료'(SERVED)가 아니면 예외가 발생한다.")
        void notServedTest() {
            // given
            var order = orderRepository.save(OrderFixture.newOneDelivery(WAITING));

            // when & then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest(name = "{1}")
        @MethodSource("notDelivery")
        @DisplayName("[예외] '배달 주문'(DELIVERY)가 아니면 예외가 발생한다.")
        void notDeliveryExceptionTest(Order order, OrderType orderType) {
            // given
            var savedOrder = orderRepository.save(order);

            // when & then
            assertThatThrownBy(() -> orderService.startDelivery(savedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
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
            var order = orderRepository.save(OrderFixture.newOneDelivery(DELIVERING));

            // when
            var actual = orderService.completeDelivery(order.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(DELIVERED);
        }

        @Test
        @DisplayName("[예외] '배달중'(DELIVERING) 주문 상태가 아니면 예외가 발생한다.")
        void orderStatusDeliveringExceptionTest() {
            // given
            var order = orderRepository.save(OrderFixture.newOneDelivery(WAITING));

            // when & then
            assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("[예외] 존재하지 않는 주문일 경우 예외가 발생한다.")
        void notFoundOrderExceptionTest() {
            // when & then
            assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("주문 전체를 조회한다.")
    @Test
    void findAll() {
        // given
        var orderTable_1번 = OrderTableFixture.newOne("1번 테이블", 4, true);
        var orderTable_2번 = OrderTableFixture.newOne("2번 테이블", 4, true);
        List.of(orderTable_1번, orderTable_2번).forEach(orderTable -> orderTableRepository.save(orderTable));

        var menu_양념치킨 = MenuFixture.newOne("양념치킨");
        var menu_후라이드치킨 = MenuFixture.newOne("후라이드 치킨");
        List.of(menu_후라이드치킨, menu_양념치킨).forEach(menu-> menuRepository.save(menu));

        var orderLineItems_양념치킨 = List.of(OrderLineItemFixture.newOne(menu_양념치킨));
        var orderLineItems_후라이드치킨 = List.of(OrderLineItemFixture.newOne(menu_후라이드치킨));

        var order_1번 = OrderFixture.newOneEatIn(orderTable_1번, orderLineItems_양념치킨);
        var order_2번 = OrderFixture.newOneEatIn(orderTable_2번, orderLineItems_후라이드치킨);

        List.of(order_1번, order_2번).forEach(order -> orderService.create(order));

        // when
        var actual = orderService.findAll();

        // then
        assertThat(actual).hasSize(2);
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
            var savedOrder = orderRepository.save(order);

            // when
            var actual = orderService.complete(savedOrder.getId());

            // then
            assertThat(actual.getStatus()).isEqualTo(COMPLETED);
        }

        @DisplayName("매장내 주문일 경우 테이블의 손님 수는 0명, 테이블 상태는 '미사용'중, 주문상태는 '주문 처리 완료'로 초기화된다.")
        @Test
        void initEatInOrderTest() {
            // given
            var order = given_매장내주문일때();

            // when
            var actual = orderService.complete(order.getId());

            // then
            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(actual.getOrderTable().getNumberOfGuests()).isZero();
                softly.assertThat(actual.getOrderTable().isOccupied()).isFalse();
                softly.assertThat(actual.getStatus()).isEqualTo(COMPLETED);
            });
        }

        private Order given_매장내주문일때() {
            var orderTableId = UUID.randomUUID();
            var orderTable = OrderTableFixture.newOne(orderTableId, "1번 테이블", 4, true);
            var order = OrderFixture.newOneEatIn(UUID.randomUUID(), orderTable, SERVED);
            return orderRepository.save(order);
        }

        @DisplayName("[예외] 미존재하는 주문이면 예외가 발생한다.")
        @Test
        void notFoundOrderTest() {
            // when & then
            assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 배달 주문은, '배달완료(Delivered)' 상태가 아니면 예외가 발생한다.")
        @Test
        void notDeliveredDeliveryTest() {
            // given
            var savedOrder = orderRepository.save(OrderFixture.newOneDelivery(WAITING));

            // when & then
            assertThatThrownBy(() -> orderService.complete(savedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[예외] 포장 주문과 매장내 식사 주문은, '서빙완료(Served)' 상태가 아니면 예외가 발생한다.")
        @ParameterizedTest(name = "{1}")
        @MethodSource("notServed")
        void notServedTest(Order order, OrderType orderType) {
            // given
            var savedOrder = orderRepository.save(order);

            // when & then
            assertThatThrownBy(() -> orderService.complete(savedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
