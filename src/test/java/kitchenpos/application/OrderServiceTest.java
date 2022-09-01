package kitchenpos.application;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.test.Fixture;
import kitchenpos.test.UnitTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@DisplayName("주문")
class OrderServiceTest extends UnitTestCase {

    @InjectMocks
    private OrderService service;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @DisplayName("등록")
    @Nested
    class Create {

        private Order request;
        private Menu selectedMenu;

        @BeforeEach
        void setUp() {
            request = createOrder(OrderType.EAT_IN);
        }

        private Order createOrder(OrderType orderType) {
            Order order = new Order();
            order.setType(orderType);

            selectedMenu = Fixture.createMenu();
            OrderLineItem item = new OrderLineItem();
            item.setMenu(selectedMenu);
            item.setQuantity(0);
            item.setPrice(selectedMenu.getPrice());
            order.setOrderLineItems(List.of(item));
            return order;
        }

        @DisplayName("배달 주문")
        @Nested
        class DeliveryTest {

            @Captor
            private ArgumentCaptor<Order> captor;
            private Order request;

            @BeforeEach
            void setUp() {
                request = createOrder(OrderType.DELIVERY);
            }

            @DisplayName("배달 주문을 등록한다.")
            @Test
            void success() {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));
                given(menuRepository.findById(any()))
                        .willReturn(Optional.of(selectedMenu));
                given(orderRepository.save(any()))
                        .willReturn(request);

                // when
                String address = "주소";
                request.setDeliveryAddress(address);
                service.create(request);

                // then
                then(orderRepository).should()
                        .save(captor.capture());
                Order actual = captor.getValue();
                assertAll(
                        () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(actual.getOrderDateTime()).isNotNull(),
                        () -> assertThat(actual.getDeliveryAddress()).isEqualTo(address)
                );
            }

            @DisplayName("배송 주소를 반드시 입력되야 된다.")
            @ParameterizedTest
            @NullAndEmptySource
            void error(String actual) {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));
                given(menuRepository.findById(any()))
                        .willReturn(Optional.of(selectedMenu));

                // when
                request.setDeliveryAddress(actual);

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(request));
            }
        }

        @DisplayName("매장 주문")
        @Nested
        class EatInTest {

            @Captor
            private ArgumentCaptor<Order> captor;
            private Order request;

            @BeforeEach
            void setUp() {
                request = createOrder(OrderType.EAT_IN);
            }

            @DisplayName("매장 주문을 등록한다.")
            @Test
            void success() {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));
                given(menuRepository.findById(any()))
                        .willReturn(Optional.of(selectedMenu));
                given(orderRepository.save(any()))
                        .willReturn(request);

                OrderTable orderTable = new OrderTable();
                orderTable.setOccupied(true);
                given(orderTableRepository.findById(any()))
                        .willReturn(Optional.of(orderTable));

                // when
                service.create(request);

                // then
                then(orderRepository).should()
                        .save(captor.capture());
                Order actual = captor.getValue();

                assertAll(
                        () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(actual.getOrderDateTime()).isNotNull(),
                        () -> assertThat(actual.getOrderTable()).isNotNull()
                );
            }

            @DisplayName("배정된 주문 테이블이 존재해야된다.")
            @Test
            void error1() {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));
                given(menuRepository.findById(any()))
                        .willReturn(Optional.of(selectedMenu));

                // when then
                assertAll(
                        () -> {
                            when(orderTableRepository.findById(any()))
                                    .thenReturn(Optional.empty());

                            assertThatThrownBy(() -> service.create(request))
                                    .isInstanceOf(NoSuchElementException.class);

                        },
                        () -> {
                            OrderTable orderTable = new OrderTable();
                            orderTable.setOccupied(false);
                            when(orderTableRepository.findById(any()))
                                    .thenReturn(Optional.of(orderTable));

                            assertThatThrownBy(() -> service.create(request))
                                    .isInstanceOf(IllegalStateException.class);
                        }
                );
            }
        }

        @DisplayName("포장 주문")
        @Nested
        class TakeoutTest {

            @Captor
            private ArgumentCaptor<Order> captor;
            private Order request;

            @BeforeEach
            void setUp() {
                request = createOrder(OrderType.TAKEOUT);
            }

            @DisplayName("포장 주문을 등록한다.")
            @Test
            void success() {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));
                given(menuRepository.findById(any()))
                        .willReturn(Optional.of(selectedMenu));
                given(orderRepository.save(any()))
                        .willReturn(request);

                // when
                service.create(request);

                // then
                then(orderRepository).should()
                        .save(captor.capture());
                Order actual = captor.getValue();

                assertAll(
                        () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(actual.getOrderDateTime()).isNotNull(),
                        () -> assertThat(actual.getOrderTable()).isNull()
                );
            }
        }

        @DisplayName("주문 등록 실패")
        @Nested
        class Error {

            @DisplayName("단 하나의 주문 유형을 선택해야 한다.")
            @Test
            void error1() {
                // given
                request.setType(null);

                // when then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(request));
            }

            @DisplayName("하나 이상의 메뉴를 선택해야 한다.")
            @ParameterizedTest
            @NullAndEmptySource
            void error2(List<OrderLineItem> actual) {
                // given
                request.setOrderLineItems(actual);

                // when then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(request));
            }

            @DisplayName("주문한 메뉴가 존재하지 않는다면 주문할 수 없다.")
            @Test
            void error3() {
                // when
                when(menuRepository.findAllByIdIn(any()))
                        .thenReturn(Collections.emptyList());

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(request));
            }

            @DisplayName("주문할 메뉴 수량은 0개 이상이어야 한다."
                    + "단 매장 주문일 경우는 제외한다.")
            @ParameterizedTest
            @EnumSource(OrderType.class)
            void error4(OrderType orderType) {
                assumeTrue(OrderType.EAT_IN != orderType);

                // given
                Order request = createOrder(orderType);
                List<OrderLineItem> orderLineItems = request.getOrderLineItems();
                OrderLineItem orderLineItem = orderLineItems.get(0);
                Menu selectedMenu = orderLineItem.getMenu();

                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));

                // when
                orderLineItem.setQuantity(-1);

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(request));
            }

            @DisplayName("주문한 메뉴가 존재하지 않다면 주문할 수 없다.")
            @Test
            void error5() {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));

                // when
                when(menuRepository.findById(any()))
                        .thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> service.create(request))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("비활성화된 메뉴를 주문할 수 없다.")
            @Test
            void error6() {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));

                // when
                Menu disabledMenu = new Menu();
                disabledMenu.setDisplayed(false);
                when(menuRepository.findById(any()))
                        .thenReturn(Optional.of(disabledMenu));

                // then
                assertThatThrownBy(() -> service.create(request))
                        .isInstanceOf(IllegalStateException.class);
            }

            @DisplayName("주문한 메뉴의 가격과 등록된 메뉴의 가격이 다를 경우 주문할 수 없다.")
            @Test
            void error7() {
                // given
                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of(selectedMenu));

                // when
                Menu menu = new Menu();
                menu.setDisplayed(true);
                BigDecimal modifiedPrice = selectedMenu.getPrice()
                        .add(BigDecimal.ONE);
                menu.setPrice(modifiedPrice);
                when(menuRepository.findById(any()))
                        .thenReturn(Optional.of(menu));

                // then
                assertThatIllegalArgumentException()
                        .isThrownBy(() -> service.create(request));
            }
        }
    }

    @DisplayName("주문 상태 변경")
    @Nested
    class OrderStatusChangeTest {

        private UUID orderId;

        @BeforeEach
        void setUp() {
            orderId = UUID.randomUUID();
        }

        @DisplayName("수락")
        @Nested
        class AcceptedTest {

            @DisplayName("수락 상태로 변경한다.")
            @ParameterizedTest
            @EnumSource(OrderType.class)
            void success(OrderType orderType) {
                // given
                Order order = createOrder(orderType, OrderStatus.WAITING);

                OrderLineItem item = new OrderLineItem();
                item.setMenu(Fixture.createMenu());
                order.setOrderLineItems(List.of(item));

                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(order));

                // then
                Order actual = service.accept(orderId);
                assertThat(actual.getStatus())
                        .isEqualTo(OrderStatus.ACCEPTED);
            }

            @DisplayName("수락 상태인 경우엔 라이더에게 배달을 요청한다.")
            @Test
            void success_delivery() {
                // given
                Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING);
                order.setDeliveryAddress("s");

                OrderLineItem item = new OrderLineItem();
                item.setMenu(Fixture.createMenu());
                order.setOrderLineItems(List.of(item));

                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(order));

                // when
                Order actual = service.accept(orderId);

                // then
                assertThat(actual.getStatus())
                        .isEqualTo(OrderStatus.ACCEPTED);

                then(kitchenridersClient).should(times(1))
                        .requestDelivery(eq(orderId), any(), any());
            }

            @DisplayName("주문 내역이 존재하지 않다면 변경할 수 없다.")
            @Test
            void error1() {
                // when
                when(orderRepository.findById(any()))
                        .thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> service.accept(orderId))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("주문 대기 상태가 아닌 경우, 수락할 수 없다.")
            @ParameterizedTest
            @EnumSource(OrderStatus.class)
            void error2(OrderStatus actual) {
                assumeTrue(actual != OrderStatus.WAITING);

                // when
                Order order = new Order();
                order.setStatus(actual);
                when(orderRepository.findById(any()))
                        .thenReturn(Optional.of(order));

                // then
                assertThatThrownBy(() -> service.accept(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("제공")
        @Nested
        class ServedTest {

            @DisplayName("수락 상태를 제공으로 변경한다.")
            @Test
            void success() {
                // given
                Order order = new Order();
                order.setStatus(OrderStatus.ACCEPTED);
                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));

                // when
                Order actual = service.serve(orderId);

                // then
                assertThat(actual.getStatus())
                        .isEqualTo(OrderStatus.SERVED);
            }

            @DisplayName("주문 내역이 존재하지 않다면 변경할 수 없다.")
            @Test
            void error1() {
                // when
                when(orderRepository.findById(orderId))
                        .thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> service.serve(orderId))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("수락한 주문이 아니라면 전달 상태로 변경할 수 없다.")
            @ParameterizedTest
            @EnumSource(OrderStatus.class)
            void error2(OrderStatus orderStatus) {
                assumeTrue(orderStatus != OrderStatus.ACCEPTED);

                // given
                Order order = new Order();
                order.setStatus(orderStatus);

                // when
                when(orderRepository.findById(orderId))
                        .thenReturn(Optional.of(order));

                // then
                assertThatThrownBy(() -> service.serve(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }


        @DisplayName("배달")
        @Nested
        class DeliveringTest {

            @DisplayName("배달 주문이면서 배달 상태로 변경한다.")
            @Test
            void success() {
                // given
                Order order = createOrder(OrderType.DELIVERY, OrderStatus.SERVED);

                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(order));

                // when
                Order actual = service.startDelivery(orderId);

                // then
                assertThat(actual.getStatus())
                        .isEqualTo(OrderStatus.DELIVERING);
            }

            @DisplayName("주문 내역이 존재하지 않다면 변경할 수 없다.")
            @Test
            void error1() {
                // when
                when(orderRepository.findById(any()))
                        .thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> service.accept(orderId))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("배달 주문일 경우만, 배달 상태로 변경한다.")
            @ParameterizedTest
            @EnumSource(OrderType.class)
            void error2(OrderType orderType) {
                assumeTrue(orderType != OrderType.DELIVERY);

                // given
                Order order = createOrder(orderType, OrderStatus.SERVED);

                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(order));

                // when then
                assertThatThrownBy(() -> service.startDelivery(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }

            @DisplayName("수락된 주문만 배달 상태로 변경한다.")
            @ParameterizedTest
            @EnumSource(OrderStatus.class)
            void error3(OrderStatus orderStatus) {
                assumeTrue(orderStatus != OrderStatus.SERVED);

                // given
                Order order = createOrder(OrderType.DELIVERY, orderStatus);

                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(order));

                // when then
                assertThatThrownBy(() -> service.startDelivery(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("배달 완료")
        @Nested
        class DeliveredTest {

            @DisplayName("배달 완료 상태로 변경한다.")
            @Test
            void success() {
                // given
                Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(order));

                // when
                Order actual = service.completeDelivery(orderId);

                // then
                assertThat(actual.getStatus())
                        .isEqualTo(OrderStatus.DELIVERED);
            }

            @DisplayName("주문 내역이 존재하지 않다면 변경할 수 없다.")
            @Test
            void error1() {
                // when
                when(orderRepository.findById(any()))
                        .thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> service.completeDelivery(orderId))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("주문 상태가 배달중일 경우 주문 완료 상태로 변경한다.")
            @ParameterizedTest
            @EnumSource(OrderStatus.class)
            void error2(OrderStatus orderStatus) {
                assumeTrue(orderStatus != OrderStatus.DELIVERING);

                // given
                Order order = createOrder(OrderType.DELIVERY, orderStatus);

                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(order));

                // when then
                assertThatThrownBy(() -> service.completeDelivery(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("주문 완료")
        @Nested
        class CompletedTest {

            @DisplayName("배달 주문")
            @Nested
            class DeliveryTest {

                @DisplayName("주문 완료 상태로 변경한다.")
                @Test
                void success() {
                    // given
                    Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);

                    given(orderRepository.findById(any()))
                            .willReturn(Optional.of(order));

                    // when
                    Order actual = service.complete(orderId);

                    // then
                    assertThat(actual.getStatus())
                            .isEqualTo(OrderStatus.COMPLETED);
                }

                @DisplayName("배달 완료 상태일 경우에만 변경 가능하다.")
                @ParameterizedTest
                @EnumSource(OrderStatus.class)
                void error1(OrderStatus orderStatus) {
                    assumeTrue(orderStatus != OrderStatus.DELIVERED);

                    // given
                    Order order = createOrder(OrderType.DELIVERY, orderStatus);

                    given(orderRepository.findById(any()))
                            .willReturn(Optional.of(order));

                    // when then
                    assertThatThrownBy(() -> service.complete(orderId))
                            .isInstanceOf(IllegalStateException.class);
                }
            }

            @DisplayName("포장 주문")
            @Nested
            class TakeOutTest {

                @DisplayName("주문 완료 상태로 변경한다.")
                @Test
                void success() {
                    // given
                    Order order = createOrder(OrderType.TAKEOUT, OrderStatus.SERVED);

                    given(orderRepository.findById(any()))
                            .willReturn(Optional.of(order));

                    // when
                    Order actual = service.complete(orderId);

                    // then
                    assertThat(actual.getStatus())
                            .isEqualTo(OrderStatus.COMPLETED);
                }

                @DisplayName("수락 상태일 경우에만 변경 가능하다.")
                @ParameterizedTest
                @EnumSource(OrderStatus.class)
                void error1(OrderStatus orderStatus) {
                    assumeTrue(orderStatus != OrderStatus.SERVED);

                    // given
                    Order order = createOrder(OrderType.TAKEOUT, orderStatus);

                    given(orderRepository.findById(any()))
                            .willReturn(Optional.of(order));

                    // when then
                    assertThatThrownBy(() -> service.complete(orderId))
                            .isInstanceOf(IllegalStateException.class);
                }
            }

            @DisplayName("매장 주문")
            @Nested
            class EatInTest {

                @DisplayName("주문 완료 상태로 변경한다.")
                @Test
                void success() {
                    // given
                    Order order = createOrder(OrderType.EAT_IN, OrderStatus.SERVED);
                    OrderTable orderTable = new OrderTable();
                    orderTable.setOccupied(true);
                    orderTable.setNumberOfGuests(10);
                    order.setOrderTable(orderTable);

                    given(orderRepository.findById(any()))
                            .willReturn(Optional.of(order));

                    // when
                    when(orderRepository.existsByOrderTableAndStatusNot(orderTable,
                            OrderStatus.COMPLETED))
                            .thenReturn(Boolean.TRUE);
                    Order actual = service.complete(orderId);

                    // then
                    assertThat(actual.getStatus())
                            .isEqualTo(OrderStatus.COMPLETED);

                    assertThat(actual.getOrderTable())
                            .hasFieldOrPropertyWithValue("numberOfGuests", 10)
                            .hasFieldOrPropertyWithValue("occupied", Boolean.TRUE);
                }

                @DisplayName("주문한 테이블의 모든 주문이 완료된 경우,"
                        + "빈 테이블(인원수 0명, 비어있는 상태)로 변경한다.")
                @Test
                void successByCompletedOrders() {
                    // given
                    Order order = createOrder(OrderType.EAT_IN, OrderStatus.SERVED);
                    OrderTable orderTable = new OrderTable();
                    orderTable.setOccupied(true);
                    orderTable.setNumberOfGuests(10);
                    order.setOrderTable(orderTable);

                    given(orderRepository.findById(any()))
                            .willReturn(Optional.of(order));

                    // when
                    when(orderRepository.existsByOrderTableAndStatusNot(orderTable,
                            OrderStatus.COMPLETED))
                            .thenReturn(Boolean.FALSE);
                    Order actual = service.complete(orderId);

                    // then
                    assertThat(actual.getStatus())
                            .isEqualTo(OrderStatus.COMPLETED);

                    assertThat(actual.getOrderTable())
                            .hasFieldOrPropertyWithValue("numberOfGuests", 0)
                            .hasFieldOrPropertyWithValue("occupied", Boolean.FALSE);
                }

                @DisplayName("수락 상태일 경우에만 변경 가능하다.")
                @ParameterizedTest
                @EnumSource(OrderStatus.class)
                void error1(OrderStatus orderStatus) {
                    assumeTrue(orderStatus != OrderStatus.SERVED);

                    // given
                    Order order = createOrder(OrderType.EAT_IN, orderStatus);

                    given(orderRepository.findById(any()))
                            .willReturn(Optional.of(order));

                    // when then
                    assertThatThrownBy(() -> service.complete(orderId))
                            .isInstanceOf(IllegalStateException.class);
                }
            }

            @DisplayName("주문 내역이 존재하지 않다면 변경할 수 없다.")
            @Test
            void error1() {
                // when
                when(orderRepository.findById(any()))
                        .thenReturn(Optional.empty());

                // then
                assertThatThrownBy(() -> service.complete(orderId))
                        .isInstanceOf(NoSuchElementException.class);
            }
        }
    }

    @DisplayName("등록된 주문을 조회할 수 있다.")
    @Test
    void findAll() {
        assertThatCode(() -> service.findAll())
                .doesNotThrowAnyException();
    }

    private Order createOrder(OrderType delivery, OrderStatus delivering) {
        Order order = new Order();
        order.setType(delivery);
        order.setStatus(delivering);
        return order;
    }
}
