package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderLineItemFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.spy.SpyOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private OrderService orderService;

    @Spy
    private SpyOrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @BeforeEach
    void beforeEach() {
        orderService = new OrderService(orderRepository,
                menuRepository,
                orderTableRepository,
                kitchenridersClient);
    }

    @DisplayName("주문 등록")
    @Nested
    class CreateTestGroup {

        @DisplayName("주문 유형이 없을 때 예외 발생")
        @Test
        void createTest1() {

            // given
            Order request = OrderFixture.createOrderWithType(null);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("메뉴 항목이 없으면 예외 발생")
        @ParameterizedTest(name = "메뉴 항목: {0}")
        @NullAndEmptySource
        void createTest2(List<OrderLineItem> orderLineItems) {

            // given
            Order request = OrderFixture.createOrderWithOrderLineItems(orderLineItems);

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("주문 할 메뉴 항목이 등록 된 메뉴와 맞지 않으면 예외 발생")
        @Test
        void createTest3() {

            // given
            Order request = OrderFixture.createOrder();

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(Collections.emptyList());

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("매장(EAT_IN) 주문을 제외 한 주문 유형의 경우, 주문 항목의 수량이 음수 값이면 예외 발생")
        @ParameterizedTest(name = "주문 유형: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"EAT_IN"})
        void createTest4(OrderType type) {

            // given
            Menu menu = MenuFixture.createMenu();
            OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItemWithMenuAndQuantity(menu, -1);
            Order request = OrderFixture.createOrderWithTypeAndOrderLineItems(type, List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("등록된 메뉴가 아니면 예외 발생")
        @Test
        void createTest4() {

            // given
            Menu menu = MenuFixture.createMenu();
            Order request = OrderFixture.createOrder();

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("주문 한 메뉴가 숨겨진 메뉴면 예외 발생")
        @Test
        void createTest5() {

            // given
            Menu menu = MenuFixture.createMenuWithDisplayed(false);
            Order request = OrderFixture.createOrder();

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("메뉴의 가격과 수량을 곱한 가격과 다르면 예외 발생")
        @Test
        void createTest6() {

            // given
            Menu menu = MenuFixture.createMenuWithPrice(500);
            Menu orderMenu = MenuFixture.createMenuWithPrice(400);
            OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItemWithMenuAndQuantity(orderMenu, 1);
            Order request = OrderFixture.createOrderWithOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("배달(DELIVERY) 주문이 배달 주소가 없다면 예외 발생")
        @ParameterizedTest(name = "배달 주소: {0}")
        @NullAndEmptySource
        void createTest7(String deliveryAddress) {

            // given
            Menu menu = MenuFixture.createMenu();
            Order request = OrderFixture.createOrderWithTypeAndDeliveryAddress(OrderType.DELIVERY, deliveryAddress);

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));

            // when + then
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("매장(EAT_IN) 주문의 등록된 테이블이 없다면 예외 발생")
        @Test
        void createTest8() {

            // given
            Menu menu = MenuFixture.createMenu();
            OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItemWithMenu(menu);
            Order request = OrderFixture.createOrderWithOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("매장(EAT_IN) 주문의 테이블에 앉아 있지 않으면 예외 발생")
        @Test
        void createTest9() {

            // given
            OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(false);
            Menu menu = MenuFixture.createMenu();
            OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItemWithMenu(menu);
            Order request = OrderFixture.createOrderWithTypeAndOrderLineItems(OrderType.EAT_IN, List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.create(request));
        }

        @DisplayName("주문을 등록하고 대기(WAITING) 함")
        @Test
        void createTest10() {

            // given
            OrderTable orderTable = OrderTableFixture.createOrderTableWithIsOccupied(true);
            Menu menu = MenuFixture.createMenu();
            OrderLineItem orderLineItem = OrderLineItemFixture.createOrderLineItemWithMenu(menu);
            Order request = OrderFixture.createOrderWithOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(any()))
                    .willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(orderTable));

            // when
            Order actual = orderService.create(request);

            // then
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }
    }

    @DisplayName("주문 접수(ACCEPTED)")
    @Nested
    class AcceptTestGroup {

        @DisplayName("등록한 주문이 아니면 예외 발생")
        @Test
        void acceptTest1() {

            // given
            final UUID orderId = UUID.randomUUID();

            given(orderRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.accept(orderId));
        }

        @DisplayName("대기(WAITING) 중인 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"WAITING"})
        void acceptTest2(OrderStatus status) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithStatus(status);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.accept(orderId));
        }

        @DisplayName("배달(DELIVERY) 주문은 배달 대행사를 호출 후 접수(ACCEPTED) 함")
        @Test
        void acceptTest3() {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.DELIVERY, OrderStatus.WAITING);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when
            Order actual = orderService.accept(orderId);

            // then
            then(kitchenridersClient)
                    .should(times(1))
                    .requestDelivery(any(), any(), any());
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("배달(DELIVERY) 주문이 아니면 배달 대행사를 호출하지 않고 접수(ACCEPTED) 함")
        @ParameterizedTest(name = "주문 유형: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERY"})
        void acceptTest4(OrderType type) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(type, OrderStatus.WAITING);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when
            Order actual = orderService.accept(orderId);

            // then
            then(kitchenridersClient)
                    .should(times(0))
                    .requestDelivery(any(), any(), any());
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }

    @DisplayName("서빙(SERVED)")
    @Nested
    class ServeTestGroup {

        @DisplayName("접수(ACCEPTED)된 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"ACCEPTED"})
        void serveTest1(OrderStatus status) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithStatus(status);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.serve(orderId));
        }

        @DisplayName("접수(ACCEPTED)된 주문을 서빙(SERVED) 함")
        @Test
        void serveTest2() {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when
            Order actual = orderService.serve(orderId);

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @DisplayName("배달 시작(DELIVERING)")
    @Nested
    class StartDeliveryTestGroup {

        @DisplayName("배달(DELIVERY) 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 유형: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERY"})
        void startDeliveryTest1(OrderType type) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithType(type);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.startDelivery(orderId));
        }

        @DisplayName("서빙(SERVED)된 배달(DELIVERY) 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
        void startDeliveryTest2(OrderStatus status) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.DELIVERY, status);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.startDelivery(orderId));
        }

        @DisplayName("서빙(SERVED)된 주문을 배달 시작(DELIVERING) 함")
        @Test
        void startDeliveryTest3() {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.DELIVERY, OrderStatus.SERVED);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when
            Order actual = orderService.startDelivery(orderId);

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }
    }

    @DisplayName("배달 완료(DELIVERED)")
    @Nested
    class CompleteDeliveryTestGroup {

        @DisplayName("등록된 주문이 아니면 예외 발생")
        @Test
        void completeDeliveryTest1() {

            // given
            final UUID orderId = UUID.randomUUID();

            given(orderRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.completeDelivery(orderId));
        }

        @DisplayName("배달 시작(DELIVERING)된 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERING"})
        void completeDeliveryTest2(OrderStatus status) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithStatus(status);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.completeDelivery(orderId));
        }

        @DisplayName("배달 시작(DELIVERING)된 주문 배달 완료(DELIVERED) 함")
        @Test
        void completeDeliveryTest3() {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.DELIVERY, OrderStatus.DELIVERING);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when
            Order actual = orderService.completeDelivery(orderId);

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @DisplayName("주문 완료(COMPLETED)")
    @Nested
    class CompleteTestGroup {

        @DisplayName("등록된 주문이 아니면 예외 발생")
        @Test
        void completeTest1() {

            // given
            final UUID orderId = UUID.randomUUID();

            given(orderRepository.findById(any()))
                    .willReturn(Optional.empty());

            // when + then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.complete(orderId));
        }

        @DisplayName("배달 완료(DELIVERED)된 배달(DELIVERY) 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"DELIVERED"})
        void completeTest2(OrderStatus status) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.DELIVERY, status);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.complete(orderId));
        }

        @DisplayName("서빙(SERVED)된 포장(TAKEOUT) 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
        void completeTest3(OrderStatus status) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.TAKEOUT, status);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.complete(orderId));
        }

        @DisplayName("서빙(SERVED)된 매장(EAT_IN) 주문이 아니면 예외 발생")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = {"SERVED"})
        void completeTest4(OrderStatus status) {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.EAT_IN, status);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));

            // when + then
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.complete(orderId));
        }

        @DisplayName("완료(COMPLETED)된 매장(EAT_IN) 주문은 빈 테이블로 변경")
        @Test
        void completeTest5() {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.EAT_IN, OrderStatus.SERVED);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                    .willReturn(false);

            // when
            Order actual = orderService.complete(orderId);

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(actual.getOrderTable().getNumberOfGuests()).isZero();
            assertThat(actual.getOrderTable().isOccupied()).isFalse();
        }

        @DisplayName("완료(COMPLETED)되지 않은 매장(EAT_IN) 주문이 있다면 빈 테이블로 변경 불가")
        @Test
        void completeTest6() {

            // given
            final UUID orderId = UUID.randomUUID();
            final Order order = OrderFixture.createOrderWithTypeAndStatus(OrderType.EAT_IN, OrderStatus.SERVED);

            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                    .willReturn(true);

            // when
            Order actual = orderService.complete(orderId);

            // then
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(actual.getOrderTable().getNumberOfGuests()).isZero();
            assertThat(actual.getOrderTable().isOccupied()).isFalse();
        }
    }

    @DisplayName("등록된 주문을 모두 조회")
    @Test
    void findAllTest() {

        // given
        final Order order = OrderFixture.createOrder();

        given(orderRepository.findAll())
                .willReturn(List.of(order));

        // when
        List<Order> actual = orderRepository.findAll();

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.size()).isOne();
    }
}