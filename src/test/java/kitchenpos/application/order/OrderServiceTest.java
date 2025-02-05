package kitchenpos.application.order;

import kitchenpos.application.OrderService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.repository.InMemoryOrderRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;


import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private OrderService orderService;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = mock(MenuRepository.class);
        orderTableRepository = mock(OrderTableRepository.class);
        KitchenridersClient kitchenridersClient = mock(KitchenridersClient.class);
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {
        @Test
        @DisplayName("배달 주문 성공")
        void createDeliveryOrderSuccess() {
            // given
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(),10000, UUID.randomUUID());
            menu.setId(UUID.randomUUID());
            menu.setDisplayed(true);

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order request = OrderFixture.deliveryOrder("서울시 강남구", List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // when
            Order created = orderService.create(request);

            // then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getType()).isEqualTo(OrderType.DELIVERY);
            assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(created.getDeliveryAddress()).isEqualTo("서울시 강남구");
        }

        @Test
        @DisplayName("매장 주문 성공")
        void createEatInOrderSuccess() {
            // given
            OrderTable orderTable = OrderTableFixture.orderTable("1번 테이블", 4, true);
            orderTable.setId(UUID.randomUUID());

            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(),10000, UUID.randomUUID());
            menu.setId(UUID.randomUUID());
            menu.setDisplayed(true);

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order request = OrderFixture.eatInOrder(orderTable.getId(), List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

            // when
            Order created = orderService.create(request);

            // then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getType()).isEqualTo(OrderType.EAT_IN);
            assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(created.getOrderTable()).isEqualTo(orderTable);
        }

        @Test
        @DisplayName("주문 유형이 없으면 실패")
        void failWithoutOrderType() {
            Order request = new Order();
            request.setOrderLineItems(List.of(new OrderLineItem()));

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문 상품이 없으면 실패")
        void failWithoutOrderLineItems() {
            Order request = new Order();
            request.setType(OrderType.TAKEOUT);

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴가 존재하지 않으면 실패")
        void failWithNonExistentMenu() {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setMenuId(UUID.randomUUID());

            Order request = new Order();
            request.setType(OrderType.TAKEOUT);
            request.setOrderLineItems(List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of());

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class ChangeOrderStatus {
        @Test
        @DisplayName("주문 접수 성공")
        void acceptOrderSuccess() {
            // given
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setType(OrderType.TAKEOUT);
            order.setStatus(OrderStatus.WAITING);
            orderRepository.save(order);

            // when
            Order accepted = orderService.accept(order.getId());

            // then
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("대기 상태가 아닌 주문은 접수할 수 없다")
        void cannotAcceptNonWaitingOrder() {
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.accept(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문 서빙 성공")
        void serveOrderSuccess() {
            // given
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            // when
            Order served = orderService.serve(order.getId());

            // then
            assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        @DisplayName("접수 상태가 아닌 주문은 서빙할 수 없다")
        void cannotServeNonAcceptedOrder() {
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setStatus(OrderStatus.WAITING);
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.serve(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("배달 주문 처리")
    class HandleDeliveryOrder {
        @Test
        @DisplayName("배달 시작 성공")
        void startDeliverySuccess() {
            // given
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.SERVED);
            orderRepository.save(order);

            // when
            Order delivering = orderService.startDelivery(order.getId());

            // then
            assertThat(delivering.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        @DisplayName("배달 완료 성공")
        void completeDeliverySuccess() {
            // given
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERING);
            orderRepository.save(order);

            // when
            Order delivered = orderService.completeDelivery(order.getId());

            // then
            assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Test
    @DisplayName("주문 목록 조회")
    void findAllOrders() {
        // given
        Order order1 = new Order();
        order1.setType(OrderType.TAKEOUT);
        Order order2 = new Order();
        order2.setType(OrderType.DELIVERY);
        orderRepository.save(order1);
        orderRepository.save(order2);

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders).hasSize(2);
    }
}