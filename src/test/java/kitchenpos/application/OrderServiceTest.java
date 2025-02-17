package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        menuRepository = mock(MenuRepository.class);
        orderTableRepository = mock(OrderTableRepository.class);
        kitchenridersClient = mock(KitchenridersClient.class);
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }


    @Nested
    @DisplayName("주문 생성할 수 있다.")
    class CreateOrder {

        @Nested
        @DisplayName("공통 확인 사항")
        class CommonOrder {

            @Test
            @DisplayName("주문 타입이 비어있으면 안된다.")
            void create_fail_no_order_type() {
                // given
                Order request = createOrderByType(null);

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문할 메뉴가 존재하지 않으면 예외가 발생한다")
            void create_fail_not_exist_menu() {
                // given
                OrderLineItem orderLineItem = createOrderLineItem(1, BigDecimal.valueOf(10000));
                Order request = createOrder(OrderType.DELIVERY, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of());

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문 내역의 메뉴는 전시 상태여야 한다.")
            void create_fail_hide_menu() {
                // given
                Menu menu = createMenu("BBQ치킨", BigDecimal.valueOf(10000), false);
                OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, BigDecimal.valueOf(10000));

                Order request = createOrder(OrderType.DELIVERY, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(List.of(menu.getId())))
                    .willReturn(List.of(menu));
                given(menuRepository.findById(menu.getId()))
                    .willReturn(Optional.of(menu));

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("주문 내역이 비어있으면 안된다.")
            void create_fail_no_orderLineItems() {
                // given
                Order request = createOrder(OrderType.DELIVERY, List.of());

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문 내역의 가격과 메뉴의 가격이 동일해야 한다.")
            void create_fail_different_price() {
                // given
                Menu menu = MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(10000));
                OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, BigDecimal.valueOf(12000));

                Order request = createOrder(OrderType.DELIVERY, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(List.of(menu.getId())))
                    .willReturn(List.of(menu));
                given(menuRepository.findById(menu.getId()))
                    .willReturn(Optional.of(menu));

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
            }

            @ParameterizedTest
            @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
            @DisplayName("주문 내역의 개수가 0개 이상이여야 한다.")
            void create_invalid_quantity(OrderType orderType) {
                // given
                OrderLineItem orderLineItem = createOrderLineItem(-1, BigDecimal.valueOf(10000));

                Order request = createOrder(orderType, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(List.of(orderLineItem.getMenuId())))
                    .willReturn(List.of(MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(10000))));

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        @DisplayName("배달 주문을 생성 한다.")
        class CreateDeliveryOrder {

            @Test
            @DisplayName("배달 주문을 정상 생성한다")
            void create_delivery() {

                // given
                Menu menu = MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(10000));
                OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 2, BigDecimal.valueOf(10000));
                Order request = createDeliveryOrder(List.of(orderLineItem), "분당구");

                given(menuRepository.findAllByIdIn(List.of(menu.getId())))
                    .willReturn(List.of(menu));
                given(menuRepository.findById(menu.getId()))
                    .willReturn(Optional.of(menu));
                given(orderRepository.save(any(Order.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

                // when
                Order created = orderService.create(request);

                // then
                assertThat(created.getId()).isNotNull();
                assertThat(created.getType()).isEqualTo(OrderType.DELIVERY);
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(created.getOrderDateTime()).isNotNull();
                assertThat(created.getDeliveryAddress()).isEqualTo("분당구");
            }

            @DisplayName("배달 주문시 주소가 없으면 예외가 발생한다")
            @ParameterizedTest
            @NullAndEmptySource
            void create_delivery_no_address(final String deliveryAddress) {
                // given
                Order request = createDeliveryOrder(deliveryAddress);

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        @DisplayName("매장 주문을 생성 한다.")
        class CreateEatInOrder {

            @Test
            @DisplayName("매장 주문을 정상 생성 한다.")
            void create_success_eat_in() {
                // given
                UUID tableId = UUID.randomUUID();

                Menu menu = MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(10000));
                OrderTable orderTable = OrderTableFixture.createOccupiedOrderTable(1);
                OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, BigDecimal.valueOf(10000));

                Order request = createEatInOrder(OrderType.EAT_IN, tableId, List.of(orderLineItem));
                request.setOrderTableId(tableId);

                given(menuRepository.findAllByIdIn(List.of(menu.getId())))
                    .willReturn(List.of(menu));
                given(menuRepository.findById(menu.getId()))
                    .willReturn(Optional.of(menu));
                given(orderTableRepository.findById(tableId))
                    .willReturn(Optional.of(orderTable));
                given(orderRepository.save(any(Order.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

                // when
                Order created = orderService.create(request);

                // then
                assertThat(created.getType()).isEqualTo(OrderType.EAT_IN);
                assertThat(created.getOrderTable()).isEqualTo(orderTable);
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(created.getOrderLineItems()).hasSize(1);
            }

            @Test
            @DisplayName("사용 가능한 테이블이 있어야 함.")
            void create_fail_no_table() {
                // given
                UUID tableId = UUID.randomUUID();

                Menu menu = MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(10000));
                OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, BigDecimal.valueOf(10000));
                Order request = createEatInOrder(OrderType.EAT_IN, tableId, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(List.of(menu.getId())))
                    .willReturn(List.of(menu));
                given(menuRepository.findById(menu.getId()))
                    .willReturn(Optional.of(menu));
                given(orderTableRepository.findById(tableId))
                    .willReturn(Optional.empty());

                // when, then
                assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
            }
        }

        @Nested
        @DisplayName("포장 주문을 생성한다")
        class CreateTakeoutOrder {

            @Test
            @DisplayName("포장 주문을 정상 생성한다")
            void create_success() {
                // given
                Menu menu = MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(10000));
                OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 1, BigDecimal.valueOf(10000));

                Order request = createOrder(OrderType.TAKEOUT, List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(List.of(menu.getId())))
                    .willReturn(List.of(menu));
                given(menuRepository.findById(menu.getId()))
                    .willReturn(Optional.of(menu));
                given(orderRepository.save(any(Order.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

                // when
                Order created = orderService.create(request);

                // then
                assertThat(created.getType()).isEqualTo(OrderType.TAKEOUT);
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(created.getOrderLineItems()).hasSize(1);
                assertThat(created.getOrderLineItems().getFirst().getQuantity()).isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("주문 받기")
    class AcceptOrderStatus {

        @Test
        @DisplayName("주문을 수락한다")
        void accept_success_order() {
            // given
            Order order = createOrderByStatus(OrderStatus.WAITING);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when
            Order accepted = orderService.accept(order.getId());

            // then
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("주문 상태가 대기중일 경우에 수락 가능하다.")
        void accept_fail_invalid_order_status() {
            // given
            Order order = createOrderByStatus(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when, then
            assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달 주문은 배달을 요청한다.")
        void accept_success_delivery_order() {
            // given
            Menu menu = MenuFixture.createDisplayedMenu("BBQ치킨", BigDecimal.valueOf(10000));

            OrderLineItem orderLineItem = createOrderLineItem(2, BigDecimal.valueOf(10000));
            orderLineItem.setMenu(menu);

            Order order = createDeliveryOrder(OrderStatus.WAITING, List.of(orderLineItem), "분당구");

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when
            orderService.accept(order.getId());

            // then
            verify(kitchenridersClient).requestDelivery(
                order.getId(),
                BigDecimal.valueOf(20000),
                "분당구"
            );
        }
    }

    @Nested
    @DisplayName("주문을 조리 완료하여 서빙 상태로 변경하기.")
    class ServedOrderStatus {

        @Test
        @DisplayName("주문을 조리 완료하여 서빙 상태로 정상 변경하기.")
        void served_success_order() {
            // given
            Order order = createOrderByStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when
            Order served = orderService.serve(order.getId());

            // then
            assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        @DisplayName("주문 상태가 수락일 경우에 서빙 가능하다.")
        void served_fail_invalid_order_status() {
            // given
            Order order = createOrderByStatus(OrderStatus.WAITING);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when, then
            assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("배달 시작하기.")
    class StartDelivery {

        @Test
        @DisplayName("배달을 시작한다")
        void start_delivery_success() {
            // given
            Order order = createDeliveryOrder(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when
            Order delivering = orderService.startDelivery(order.getId());

            // then
            assertThat(delivering.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        @DisplayName("주문 상태가 서빙 상태여야 배달 시작 가능하다.")
        void start_delivery_fail_invalid_order_status() {
            // given
            Order order = createDeliveryOrder(OrderStatus.WAITING);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when
            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문 타입이 배달이여야 한다.")
        void start_delivery_fail_invalid_order_type() {
            // given
            Order order = createTakeOutOrder(OrderStatus.SERVED);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when, then
            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("배달 완료하기.")
    @Nested
    class CompleteDelivery {

        @Test
        @DisplayName("배달 정상 완료하기.")
        void complete_delivery_success() {

            // given
            Order order = createDeliveryOrder(OrderStatus.DELIVERING);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when
            Order result = orderService.completeDelivery(order.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("주문 상태가 배달중 상태여야 배달 완료 가능하다.")
        void complete_delivery_fail() {

            // given
            Order order = createDeliveryOrder(OrderStatus.WAITING);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when, then
            assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 완료하기.")
    class CompleteOrder {

        @Test
        @DisplayName("매장 주문을 완료한다")
        void complete_eat_in_order_success() {
            // given
            OrderTable orderTable = OrderTableFixture.createOccupiedOrderTable(1);

            Order order = createOrder(OrderType.EAT_IN, OrderStatus.SERVED);
            order.setOrderTable(orderTable);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));
            given(orderTableRepository.findById(orderTable.getId()))
                .willReturn(Optional.of(orderTable));

            // when
            Order completed = orderService.complete(order.getId());

            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("포장 주문을 완료한다")
        void complete_take_out_order_success() {
            Order order = createOrder(OrderType.TAKEOUT, OrderStatus.SERVED);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            Order completed = orderService.complete(order.getId());

            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("서빙 상태여야 주문 완료 가능하다.")
        void complete_order_fail_invalid_order_status() {
            // given
            Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when, then
            assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("매장 주문은 주문 완료 후, 테이블 정리를 추가로 진행한다.")
        void complete_eat_in_order_do_clear_table() {
            // given
            OrderTable orderTable = OrderTableFixture.createOccupiedOrderTable(2);

            Order order = createOrder(OrderType.EAT_IN, OrderStatus.SERVED);
            order.setOrderTable(orderTable);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .willReturn(false);

            // when
            orderService.complete(order.getId());

            // then
            assertThat(orderTable.getNumberOfGuests()).isZero();
            assertThat(orderTable.isOccupied()).isFalse();
        }

        @Test
        @DisplayName("배달 주문 완료")
        void complete_delivery_order_success() {
            // given
            Order order = createDeliveryOrder(OrderStatus.DELIVERED);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when
            Order completed = orderService.complete(order.getId());

            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("배달 완료 상태여야 주문 완료 가능")
        void complete_delivery_order_fail_invalid_order_status() {
            // given
            Order order = createDeliveryOrder(OrderStatus.WAITING);

            given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

            // when, then
            assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("주문 정보를 전부 가져올 수 있다.")
    void findAll() {
        // given
        List<Order> orders = List.of(
            createOrderByType(OrderType.DELIVERY),
            createOrderByType(OrderType.TAKEOUT),
            createOrderByType(OrderType.EAT_IN)
        );
        given(orderRepository.findAll())
            .willReturn(orders);

        // when
        List<Order> result = orderService.findAll();

        // then
        assertThat(result).hasSize(3);
    }
}

