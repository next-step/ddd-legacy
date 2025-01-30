package kitchenpos.application;

import static kitchenpos.builder.TestFixtureFactory.createEmptyOrderTable;
import static kitchenpos.builder.TestFixtureFactory.createMenuWithProductAndGroup;
import static kitchenpos.builder.TestFixtureFactory.createOrder;
import static kitchenpos.builder.TestFixtureFactory.createOrderLineItem;
import static kitchenpos.builder.TestFixtureFactory.createUsingOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.builder.TestFixtureFactory;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        menuRepository = mock(MenuRepository.class);
        orderTableRepository = mock(OrderTableRepository.class);
        kitchenridersClient = mock(KitchenridersClient.class);
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 생성")
    class Create {

        @Test
        @DisplayName("주문은 하나 이상의 주문 내역으로 생성할 수 있다")
        void create_order() {
            // given
            Menu menu = TestFixtureFactory.createMenuWithProductAndGroup();
            Order request = createOrderRequestWithEmptyTable(OrderType.DELIVERY, OrderStatus.WAITING, menu, "서울");
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
            when(orderRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            Order result = orderService.create(request);

            // then
            assertThat(result.getOrderLineItems()).hasSize(1);
        }

        @Test
        @DisplayName("게시되지 않은 메뉴 주문 시 예외가 발생한다.")
        void menu_name_exception() {
            // given
            Menu menu = createMenuWithProductAndGroup(false);
            Order request = createOrderRequestWithEmptyTable(OrderType.DELIVERY, OrderStatus.WAITING, menu, "서울");
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // when // then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달 주문 시 주소가 없으면 예외가 발생한다.")
        void delivery_address_exception() {
            // given
            Menu menu = TestFixtureFactory.createMenuWithProductAndGroup();
            Order request = createOrderRequestWithEmptyTable(OrderType.DELIVERY, OrderStatus.SERVED, menu, null);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(menu));

            // when // then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("이용 중이지 않은 테이블에서 주문 시 예외가 발생한다.")
        void not_occupied_table_order_exception() {
            // given
            Menu menu = TestFixtureFactory.createMenuWithProductAndGroup();
            OrderTable orderTable = createEmptyOrderTable();
            Order request = createOrderRequestWithOccupiedTable(OrderType.EAT_IN, OrderStatus.WAITING, menu, "서울");
            request.setOrderTable(orderTable);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

            // when // then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class ChangeStatus {
        @Test
        @DisplayName("대기 중인 주문만 접수할 수 있다")
        void accept_success() {
            // given
            OrderTable orderTable = createUsingOrderTable();
            Order order = createOrder(createOrderLineItem(TestFixtureFactory.createMenuWithProductAndGroup()),
                    orderTable, OrderType.DELIVERY,
                    OrderStatus.WAITING, "서울");
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            Order result = orderService.accept(order.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        @DisplayName("대기 중이 아닌 주문 시 예외가 발생한다.")
        void accept_exception(OrderStatus status) {
            // given
            OrderTable orderTable = createUsingOrderTable();
            Order order = createOrder(createOrderLineItem(TestFixtureFactory.createMenuWithProductAndGroup()),
                    orderTable, OrderType.DELIVERY, status, "서울");
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when // then
            assertThatThrownBy(() -> orderService.accept(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달 주문 접수 시 배달 요청을 한다")
        void delivery_success() {
            // given
            OrderTable orderTable = createUsingOrderTable();
            Order order = createOrder(createOrderLineItem(TestFixtureFactory.createMenuWithProductAndGroup()),
                    orderTable, OrderType.DELIVERY,
                    OrderStatus.WAITING, "서울");
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            orderService.accept(order.getId());

            // then
            verify(kitchenridersClient).requestDelivery(any(), any(), any());
        }

        @ParameterizedTest
        @CsvSource({
                "DELIVERY, DELIVERED, COMPLETED",
                "TAKEOUT, SERVED, COMPLETED",
                "EAT_IN, SERVED, COMPLETED"
        })
        @DisplayName("주문 유형별 완료 조건이 맞으면 주문을 완료할 수 있다")
        void complete_order(OrderType type, OrderStatus status, OrderStatus expected) {
            // given
            OrderTable orderTable = createUsingOrderTable();
            Order order = createOrder(createOrderLineItem(TestFixtureFactory.createMenuWithProductAndGroup()),
                    orderTable, type, status, "서울");
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // when
            Order result = orderService.complete(order.getId());

            // then
            assertThat(result.getStatus()).isEqualTo(expected);
        }

        @Test
        @DisplayName("매장 식사 주문 완료 시 다른 주문이 없으면 테이블을 비운다")
        void clear_table_with_complete_status() {
            // given
            OrderTable orderTable = createUsingOrderTable();
            Order order = createOrder(createOrderLineItem(TestFixtureFactory.createMenuWithProductAndGroup()),
                    orderTable, OrderType.EAT_IN,
                    OrderStatus.SERVED, "서울");
            order.setOrderTable(orderTable);

            when(orderRepository.findById(any())).thenReturn(Optional.of(order));
            when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

            // when
            orderService.complete(order.getId());

            // then
            assertThat(orderTable.isOccupied()).isFalse();
            assertThat(orderTable.getNumberOfGuests()).isZero();
        }
    }

    private Order createOrderRequestWithOccupiedTable(OrderType type, OrderStatus orderStatus, Menu menu,
                                                      String address) {
        OrderTable orderTable = createUsingOrderTable();
        return createOrder(createOrderLineItem(menu), orderTable, type,
                orderStatus, address);
    }

    private Order createOrderRequestWithEmptyTable(OrderType type, OrderStatus orderStatus, Menu menu,
                                                   String address) {
        OrderTable orderTable = createEmptyOrderTable();
        return createOrder(createOrderLineItem(menu), orderTable, type,
                orderStatus, address);
    }
}
