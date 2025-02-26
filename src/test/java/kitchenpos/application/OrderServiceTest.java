package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fake.FakeKitchenridersClient;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderServiceTest {

    private OrderService orderService;

    private OrderRepository orderRepository;

    private OrderTableRepository orderTableRepository;

    private MenuRepository menuRepository;

    private KitchenridersClient kitchenridersClient;

    @BeforeEach
    void setup() {
        orderRepository = new InMemoryOrderRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        menuRepository = new InMemoryMenuRepository();
        kitchenridersClient = new FakeKitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 등록")
    class RegisterOrder {

        @Test
        @DisplayName("주문을 등록한다.")
        void testRegisterOrder() {
            // given
            final Menu menu = menuRepository.save(MenuFixture.createMenu());
            final OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1);
            final Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, List.of(orderLineItem));

            // when
            final Order result = orderService.create(request);

            // then
            final Order found = orderRepository.findById(result.getId()).orElse(null);
            assertThat(found).isNotNull();
            assertAll(
                    () -> assertThat(found.getType()).isEqualTo(request.getType()),
                    () -> assertThat(found.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(found.getOrderLineItems()).hasSize(1)
            );
        }

        @ParameterizedTest
        @DisplayName("주문 유형이 배달주문이면 배달주소를 입력해야한다.")
        @NullSource
        void testRegisterOrderWithDeliveryAddress(final String deliveryAddress) {
            // given
            final Order request = OrderFixture.createDeliveryOrderRequest(deliveryAddress);

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문 유형이 매장식사인 경우 지정한 주문테이블이 사용 중이어야 한다.")
        void testRegisterOrderWithOrderTable() {
            // given
            final OrderTable orderTable = OrderTableFixture.createOrderTable();
            final Order request = OrderFixture.createEatInOrderRequest(orderTable);

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문 항목을 1개 이상 포함해야 한다.")
        void testRegisterOrderWithOrderLineItems() {
            // given
            final List<OrderLineItem> emptyOrderLineItems = List.of();
            final Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, emptyOrderLineItems);

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("등록된 메뉴만 주문 항목에 포함될 수 있다.")
        void testRegisterOrderWithRegisteredMenu() {
            // given
            final Order request = OrderFixture.createTakeOutOrderRequest();

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
        @DisplayName("주문 유형이 배달이나 포장인 경우 주문한 메뉴의 수량은 반드시 1개 이상이어야 한다.")
        void testRegisterOrderWithOrderLineItemQuantity(final OrderType orderType) {
            // given
            final Menu menu = menuRepository.save(MenuFixture.createMenu());
            final OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, -1);
            final Order request = OrderFixture.createOrderRequest(orderType, List.of(orderLineItem));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("노출 중이 아닌 메뉴는 주문할 수 없다.")
        void testRegisterOrderWithVisibleMenu() {
            // given
            final Menu hiddenMenu = menuRepository.save(MenuFixture.createHiddenMenu());
            final OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(hiddenMenu);
            final Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, List.of(orderLineItem));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문한 메뉴의 가격과 등록된 메뉴의 가격이 다르면 예외가 발생한다.")
        void testRegisterOrderWithOrderLineItemPrice() {
            // given
            final Menu orderedMenu = menuRepository.save(MenuFixture.createMenu(BigDecimal.valueOf(10_000)));
            menuRepository.save(MenuFixture.createMenu(BigDecimal.valueOf(16_000)));
            final OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(orderedMenu, 1, BigDecimal.valueOf(20_000));
            final Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, List.of(orderLineItem));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("주문 수락")
    class AcceptOrder {

        @Test
        @DisplayName("주문 상태를 수락(ACCEPTED)으로 변경한다.")
        void testAcceptOrder() {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderStatus.WAITING));

            // when
            final Order result = orderService.accept(request.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @ParameterizedTest
        @DisplayName("대기 중인 주문이 아닌 경우 수락할 수 없다.")
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void testAcceptOrderWithStatus(final OrderStatus status) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(status));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.accept(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문을 수락하면 배달 라이더에게 주문을 전달한다.")
        void testAcceptOrderWithDelivery() {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderType.DELIVERY, OrderStatus.WAITING));

            // when
            final Order result = orderService.accept(request.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            assertThat(((FakeKitchenridersClient) kitchenridersClient).getRequestDeliveryCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("주문 제공")
    class ServeOrder {

        @Test
        @DisplayName("주문 상태를 제공(SERVED)으로 변경한다.")
        void testServeOrder() {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderStatus.ACCEPTED));

            // when
            final Order result = orderService.serve(request.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @ParameterizedTest
        @DisplayName("수락(ACCEPTED)된 주문만 변경할 수 있다.")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void testServeOrderWithStatus(final OrderStatus status) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(status));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.serve(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 배달 시작")
    class DeliveryOrder {

        @Test
        @DisplayName("주문 상태를 배달중(DELIVERING)으로 변경한다.")
        void testDeliveryOrder() {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderStatus.SERVED));

            // when
            final Order result = orderService.startDelivery(request.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @ParameterizedTest
        @DisplayName("주문 유형이 배달주문이 아니면 오류가 발생한다.")
        @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
        void testDeliveryOrderWithOrderType(final OrderType orderType) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrderRequest(orderType, List.of(OrderFixture.createOrderLineItem())));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.startDelivery(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }


        @ParameterizedTest
        @DisplayName("수락(SERVED)된 주문만 배달할 수 있다.")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void testDeliveryOrderWithStatus(final OrderStatus status) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(status));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.startDelivery(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 배달 완료")
    class CompleteDelivery {

        @Test
        @DisplayName("주문 상태를 배달완료(DELIVERED)으로 변경한다.")
        void testCompleteDelivery() {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderStatus.DELIVERING));

            // when
            final Order result = orderService.completeDelivery(request.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @ParameterizedTest
        @DisplayName("배달중(DELIVERING)인 주문만 배달 완료할 수 있다.")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
        void testCompleteDeliveryWithStatus(final OrderStatus status) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(status));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.completeDelivery(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 완료")
    class CompleteOrder {

        @Test
        @DisplayName("주문 상태를 완료(COMPLETED)로 변경한다.")
        void testCompleteOrder() {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED));

            // when
            final Order result = orderService.complete(request.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @ParameterizedTest
        @DisplayName("주문 유형이 배달주문이면 배달완료(DELIVERED) 상태일 경우에만 변경할 수 있다.")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
        void testCompleteOrderWithStatus(final OrderStatus status) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderType.DELIVERY, status));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.complete(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest
        @DisplayName("주문 유형이 포장주문인 경우 제공완료(SERVED) 상태일 경우에만 변경할 수 있다.")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void testCompleteOrderWithTakeOut(final OrderStatus status) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderType.TAKEOUT, status));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.complete(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @ParameterizedTest
        @DisplayName("주문 유형이 매장식사인 경우 제공완료(SERVED) 상태일 경우에만 변경할 수 있다.")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void testCompleteOrderWithDeliveryAndTakeOut(final OrderStatus status) {
            // given
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderType.EAT_IN, status));

            // when & then
            assertThatException()
                    .isThrownBy(() -> orderService.complete(request.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문 유형이 매장식사일 때 주문 테이블을 정리한다.")
        void testCompleteOrderWithOrderTable() {
            // given
            final Menu menu = menuRepository.save(MenuFixture.createMenu());
            final OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 1);
            final OrderTable orderTable = OrderTableFixture.createOrderTable();
            final Order request = orderRepository.save(OrderFixture.createOrder(OrderStatus.SERVED, orderTable, List.of(orderLineItem)));

            // when
            final Order result = orderService.complete(request.getId());

            // then
            assertAll(
                    () -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero(),
                    () -> assertThat(result.getOrderTable().isOccupied()).isFalse()
            );
        }
    }

}
