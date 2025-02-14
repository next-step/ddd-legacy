package kitchenpos.application;

import config.UnitTest;
import helper.PriceGenerator;
import kitchenpos.MenuFixture;
import kitchenpos.OrderFixture;
import kitchenpos.OrderTableFixture;
import kitchenpos.application.*;
import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import kitchenpos.infra.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static kitchenpos.MenuFixture.*;
import static kitchenpos.OrderFixture.*;
import static kitchenpos.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@DisplayName("주문 서비스 테스트")
class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        orderRepository = new InmemoryOrderRepository();
        menuRepository = new InmemoryMenuRepository();
        orderTableRepository = new InmemoryOrderTableRepository();
        kitchenridersClient = new FakeKitchenridersClient();
        sut = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrderTests {

        @Test
        @DisplayName("성공: 고객은 메뉴를 선택해 주문할 수 있다.")
        void createOrder_success() {
            // given
            OrderType expectedType = OrderType.EAT_IN;
            OrderStatus expectedStatus = OrderStatus.WAITING;

            Menu menu = menuRepository.save(aMenuRequest().build());
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest()
                    .occupied(true)
                    .numberOfGuests(3)
                    .build());
            OrderLineItem orderLineItem = anOrderLineItemRequest(menu).build();
            Order request = anOrderRequest()
                    .type(expectedType)
                    .orderTable(orderTable)
                    .orderLineItems(List.of(orderLineItem))
                    .build();

            // when
            Order result = sut.create(request);

            // then
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertNotNull(result.getId()),
                    () -> assertEquals(expectedType, result.getType()),
                    () -> assertEquals(expectedStatus, result.getStatus()),
                    () -> assertNotNull(result.getOrderDateTime()),
                    () -> assertEquals(1, result.getOrderLineItems().size())
            );
        }

        @Test
        @DisplayName("실패: 주문 타입을 선택하지 않으면 OrderTypeNotSelectedException이 발생한다.")
        void createOrder_fail_whenOrderTypeNotSelected() {
            //given
            Order request = anOrderRequest().type(null).build();

            //when & then
            assertThrows(OrderTypeNotSelectedException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 주문 항목을 선택하지 않으면 OrderLineItemNotSelectedException이 발생한다.")
        void createOrder_fail_whenNoMenuSelected() {
            //given
            Order request = anOrderRequest().orderLineItems(null).build();

            //when & then
            assertThrows(OrderLineItemNotSelectedException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 주문 항목에 존재하지 않는 메뉴가 포함되어 있으면 OrderLineItemNotMatchedMenuException이 발생한다.")
        void createOrder_fail_whenTakeoutOrDeliveryWithZeroQuantity() {
            // given
            Menu existMenu = menuRepository.save(aMenuRequest().build());
            Menu notExistMenu = aMenuRequest().build();
            Order request = anOrderRequest()
                    .orderLineItems(
                            List.of(
                                    anOrderLineItemRequest(existMenu).build(),
                                    anOrderLineItemRequest(notExistMenu).build())
                    )
                    .build();

            // when & then
            assertThrows(OrderLineItemNotMatchedMenuException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 매장 주문을 제외한 주문 항목의 수량은 0보다 크지 않으면 OrderLineQuantityNegativeException이 발생한다.")
        @MethodSource("kitchenpos.OrderFixture#orderTypeNotEatIn")
        void createOrder_fail_whenTakeoutOrDeliveryWithZeroQuantity(OrderType type) {
            //given
            Menu menu = menuRepository.save(aMenuRequest().build());
            Order request = anOrderRequest()
                    .type(type)
                    .orderLineItems(
                            List.of(
                                    anOrderLineItemRequest(menu).quantity(-1L).build()
                            )
                    )
                    .deliveryAddress("서울시 강남구")
                    .build();


            //when & then
            assertThrows(OrderLineQuantityNegativeException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 메뉴 상태가 표시중이지 않은 메뉴가 포함되어 있으면 MenuNotDisplayedException이 발생한다.")
        void createOrder_fail_whenMenuNotDisplayed() {
            // given
            Menu menu = menuRepository.save(aMenuRequest()
                    .displayed(false)
                    .build());

            Order request = anOrderRequest()
                    .orderLineItems(
                            List.of(
                                    anOrderLineItemRequest(menu).build()
                            )
                    )
                    .build();

            // when & then: 주문 생성 시 MenuNotDisplayedException 예외가 발생하는지 확인
            assertThrows(MenuNotDisplayedException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 주문 항목 각각의 가격이 시스템에 등록된 메뉴와 동일하지 않으면 OrderLineItemPriceMismatchException 발생한다.")
        void createOrder_fail_whenPriceMismatch() {
            // given
            Menu menu = menuRepository.save(aMenuRequest().price(PriceGenerator.of(10000)).build());

            Order request = anOrderRequest()
                    .orderLineItems(
                            List.of(
                                    anOrderLineItemRequest(menu).price(PriceGenerator.of(20000)).build()
                            )
                    )
                    .build();

            // when & then
            assertThrows(OrderLineItemPriceMismatchException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("성공: 주문 최초 생성시 상태는 대기여야 한다.")
        void createOrder_success_withWaitingStatus() {
            // given
            Menu menu = menuRepository.save(aMenuRequest().displayed(true).build());
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest()
                    .occupied(true)
                    .numberOfGuests(3)
                    .build());
            Order request = anOrderRequest()
                    .orderTable(orderTable)
                    .orderLineItems(
                            List.of(
                                    anOrderLineItemRequest(menu).build()
                            )
                    )
                    .build();

            // when
            Order createdOrder = sut.create(request);

            // then: 생성된 주문의 상태가 대기(WAITING) 상태인지 검증
            assertEquals(OrderStatus.WAITING, createdOrder.getStatus());
        }

        @Test
        @DisplayName("실패: 배달 주문일 때, 배달 주소가 입력되지 않으면 OrderDeliveryAddressNotEnteredException이 발생한다.")
        void createOrder_fail_whenDeliveryOrderWithoutAddress() {
            // given
            Menu meun = menuRepository.save(aMenuRequest().build());
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest().build());
            Order request = anOrderRequest()
                    .type(OrderType.DELIVERY)
                    .deliveryAddress(null)
                    .orderTable(orderTable)
                    .orderLineItems(
                            List.of(
                                    anOrderLineItemRequest(meun).build()
                            )
                    )
                    .build();

            // when & then
            assertThrows(OrderDeliveryAddressNotEnteredException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 매장 주문일 때, 사용중이 아닌 테이블에 주문을 생성하면 OrderTableNotOccupiedException이 발생한다.")
        void createOrder_fail_whenDineInWithoutAvailableTable() {
            // given
            Menu menu = menuRepository.save(aMenuRequest().build());
            OrderTable orderTable = orderTableRepository.save(anOrderTableRequest().occupied(false).build());
            Order request = anOrderRequest()
                    .type(OrderType.EAT_IN)
                    .orderTable(orderTable)
                    .orderLineItems(
                            List.of(
                                    anOrderLineItemRequest(menu).build()
                            )
                    )
                    .build();
            // when & then
            assertThrows(OrderTableNotOccupiedException.class, () -> sut.create(request));
        }
    }


    @Nested
    @DisplayName("주문을 접수")
    class AcceptOrderTests {
        @Test
        @DisplayName("성공: 대기 상태의 주문을 접수할 수 있다.")
        void acceptOrder_success_whenWaiting() {
            // given
            Order order = orderRepository.save(anOrderRequest()
                    .status(OrderStatus.WAITING)
                    .build());

            // when
            Order acceptedOrder = sut.accept(order.getId());

            // then
            assertEquals(OrderStatus.ACCEPTED, acceptedOrder.getStatus());

        }

        @ParameterizedTest
        @DisplayName("실패: 대기 상태가 아닌 주문을 접수할 경우 OrderStatusNotWaitingException이 발생한다.")
        @MethodSource("kitchenpos.OrderFixture#orderStatusNotWaiting")
        void acceptOrder_fail_whenNotWaiting(OrderStatus status) {
            // given
            Order order = orderRepository.save(anOrderRequest()
                    .status(status)
                    .build());

            // when & then
            assertThrows(OrderStatusNotWaitingException.class, () -> sut.accept(order.getId()));
        }

        @Test
        @DisplayName("성공: 배달 주문을 접수하면 배달 요청을 보낸다.")
        void acceptOrder_success_whenDeliveryOrder_requestsDelivery() {
            // given
            Order order = orderRepository.save(anOrderRequest()
                    .type(OrderType.DELIVERY)
                    .deliveryAddress("서울시 강남구")
                    .build());

            // when
            Order acceptedOrder = sut.accept(order.getId());

            // then
            assertEquals(OrderStatus.ACCEPTED, acceptedOrder.getStatus());
            assertTrue(((FakeKitchenridersClient) kitchenridersClient).isRequestedDelivery());
        }
    }

    @Nested
    @DisplayName("조리가 완료된 주문을 제공")
    class ServeOrderTests {
        @Test
        @DisplayName("성공: 접수 상태의 주문만 제공할 수 있다.")
        void serveOrder_success_whenAccepted() {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .status(OrderStatus.ACCEPTED)
                    .build());

            //when
            Order servedOrder = sut.serve(order.getId());

            //then
            assertEquals(OrderStatus.SERVED, servedOrder.getStatus());
        }

        @ParameterizedTest
        @DisplayName("실패: 접수 상태가 아닌 주문은 제공할 수 없다.")
        @MethodSource("kitchenpos.OrderFixture#orderStatusNotAccepted")
        void serveOrder_fail_whenNotAccepted(OrderStatus status) {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .status(status)
                    .build());

            //when & then
            assertThrows(OrderStatusNotAcceptedException.class, () -> sut.serve(order.getId()));
        }
    }

    @Nested
    @DisplayName("배달 주문을 시작")
    class StartDeliveryTests {
        @Test
        @DisplayName("성공: 배달 주문이 제공되면 배달이 시작되었음을 기록한다.")
        void serveOrder_success_whenDeliveryOrder_startDelivery() {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .type(OrderType.DELIVERY)
                    .status(OrderStatus.SERVED)
                    .build());

            //when
            Order servedOrder = sut.startDelivery(order.getId());

            //then
            assertAll(
                    () -> assertThat(servedOrder).isNotNull(),
                    () -> assertThat(servedOrder.getType()).isEqualTo(OrderType.DELIVERY),
                    () -> assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING)
            );
        }

        @ParameterizedTest
        @DisplayName("실패: 주문 타입이 배달이 아닌 경우 OrderTypeNotDeliveryException이 발생한다.")
        @MethodSource("kitchenpos.OrderFixture#orderTypeNotDelivery")
        void serveOrder_success_whenDeliveryOrder_startDelivery(OrderType type) {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .type(type)
                    .status(OrderStatus.ACCEPTED)
                    .build());

            //when & then
            assertThrows(OrderTypeNotDeliveryException.class, () -> sut.startDelivery(order.getId()));
        }
    }


    @Nested
    @DisplayName("배달 주문을 완료")
    class CompleteDeliveryTests {
        @Test
        @DisplayName("성공: 배달 중 상태의 주문만 배달 완료할 수 있다.")
        void completeDelivery_success_whenInDelivery() {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .status(OrderStatus.DELIVERING)
                    .type(OrderType.DELIVERY)
                    .build());

            //when
            Order completedOrder = sut.completeDelivery(order.getId());

            //then
            assertEquals(OrderStatus.DELIVERED, completedOrder.getStatus());
        }

        @ParameterizedTest
        @DisplayName("실패: 배달 중이 아닌 상태의 주문은 배달 완료할 수 없다.")
        @MethodSource("kitchenpos.OrderFixture#orderStatusNotDelivering")
        void completeDelivery_fail_whenNotInDelivery(OrderStatus status) {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .status(status)
                    .type(OrderType.DELIVERY)
                    .build());

            //when & then
            assertThrows(OrderStatusNotDeliveringException.class, () -> sut.completeDelivery(order.getId()));
        }
    }

    @Nested
    @DisplayName("주문을 최종 완료")
    class CompleteOrderTests {

        @Test
        @DisplayName("성공: 매장 주문이 완료되면 주문 상태를 완료로 변경하고 테이블을 미사용 상태로 변경한다.")
        void completeOrder_success_whenEatIn() {
            //given

            Order order = orderRepository.save(anOrderRequest()
                            .orderTable(anOrderTableRequest().occupied(true).numberOfGuests(3).build())
                    .type(OrderType.EAT_IN)
                    .status(OrderStatus.SERVED)
                    .build());

            //when
            Order completedOrder = sut.complete(order.getId());

            //then
            assertAll(
                    () -> assertEquals(OrderStatus.COMPLETED, completedOrder.getStatus()),
                    () -> assertFalse(completedOrder.getOrderTable().isOccupied()),
                    () -> assertEquals(0, completedOrder.getOrderTable().getNumberOfGuests())
            );
        }

        @Test
        @DisplayName("성공: 포장 주문은 제공된 상태에서만 완료할 수 있다.")
        void completeOrder_success_whenTakeout() {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .type(OrderType.TAKEOUT)
                    .status(OrderStatus.SERVED)
                    .build());

            //when
            Order completedOrder = sut.complete(order.getId());

            //then
            assertEquals(OrderStatus.COMPLETED, completedOrder.getStatus());
        }

        @ParameterizedTest
        @DisplayName("실패: 배달 주문이 배달 완료 상태가 아니면 OrderStatusNotDeliveredException이 발생한다.")
        @MethodSource("kitchenpos.OrderFixture#orderStatusNotDelivered")
        void completeOrder_success_whenDeliveryCompleted(OrderStatus status) {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .type(OrderType.DELIVERY)
                    .status(status)
                    .deliveryAddress("서울시 강남구")
                    .build());

            //when & then
            assertThrows(OrderStatusNotDeliveredException.class, () -> sut.complete(order.getId()));
        }

        @ParameterizedTest
        @DisplayName("실패: 매장 주문이 제공되지 않은 상태에서 완료하면 OrderStatusNotServedException이 발생한다.")
        @MethodSource("kitchenpos.OrderFixture#orderStatusNotServed")
        void completeOrder_fail_whenNotServed(OrderStatus status) {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .type(OrderType.EAT_IN)
                    .status(status)
                    .build());

            //when & then
            assertThrows(OrderStatusNotServedException.class, () -> sut.complete(order.getId()));
        }

        @ParameterizedTest
        @DisplayName("실패: 포장 주문이 제공되지 않은 상태에서 완료하면 OrderStatusNotServedException이 발생한다.")
        @MethodSource("kitchenpos.OrderFixture#orderStatusNotServed")
        void completeOrder_fail_whenNotServedForTakeout(OrderStatus status) {
            //given
            Order order = orderRepository.save(anOrderRequest()
                    .type(OrderType.TAKEOUT)
                    .status(status)
                    .build());

            //when & then
            assertThrows(OrderStatusNotServedException.class, () -> sut.complete(order.getId()));
        }
    }

    @Nested
    @DisplayName("모든 주문을 조회")
    class GetAllOrdersTests {
        @Test
        @DisplayName("성공: 전체 주문 목록을 반환한다.")
        void getAllOrders_success() {
            //given
            List<Order> orders = List.of(
                    anOrderRequest().build(),
                    anOrderRequest().build(),
                    anOrderRequest().build()
            );
            orders.forEach(orderRepository::save);

            //when
            List<Order> allOrders = sut.findAll();

            //then
            assertEquals(3, allOrders.size());
        }
    }

}