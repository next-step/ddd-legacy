package kitchenpos;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.TestFixtureFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 등록 - 주문 유형이 없을 때 예외 발생")
    void createNoOrderTypeThrowsIllegalArgumentException() {
        // Arrange
        OrderLineItem 수량2개_16000원_주문항목 = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(16000));
        Order request = createOrder(null, OrderStatus.WAITING, List.of(수량2개_16000원_주문항목));

        // Act & Assert
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 등록 - 메뉴를 찾을 수 없을 때 예외 발생")
    void createMenuItemsNotFoundThrowsIllegalArgumentException() {
        // Arrange
        OrderLineItem 수량2개_1000원_주문항목 = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(1000));
        Order request = createOrder(OrderType.EAT_IN, OrderStatus.WAITING, List.of(수량2개_1000원_주문항목));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 등록 - 주문 메뉴 수량이 없을 때 예외 발생")
    void createNoOrderLineItemsThrowsIllegalArgumentException() {
        // Arrange
        Order request = createOrder(OrderType.EAT_IN, OrderStatus.WAITING, Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Nested
    class EatInOrderTest {
        @Test
        @DisplayName("매장식사 주문 등록 - 정상적으로 매장식사 주문 등록 후 반환")
        void createValidOrderReturnsCreatedOrder() {
            // Arrange
            OrderLineItem 수량2개_1000원_주문항목 = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(1000));
            Order request = createOrder(OrderType.EAT_IN, OrderStatus.WAITING, List.of(수량2개_1000원_주문항목));
            OrderTable 주문_1번_테이블 = createOrderTable("1번 테이블", 3, true);
            Menu 주문항목_수량2개_1000원_메뉴 = createMenu("주문항목_수량2개_1000원_메뉴", BigDecimal.valueOf(1000), UUID.randomUUID(), true, Collections.emptyList());

            when(menuRepository.findById(any())).thenReturn(Optional.of(주문항목_수량2개_1000원_메뉴));
            when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(new Menu()));
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(주문_1번_테이블));
            when(orderRepository.save(any(Order.class))).thenReturn(new Order());

            // Act
            Order 등록된_매장식사_주문 = orderService.create(request);

            // Assert
            assertThat(등록된_매장식사_주문).isNotNull();
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("매장식사 주문 완료 - 정상적으로 매장식사 주문 완료 후 반환")
        void completeValidOrderReturnsCompletedOrder() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            OrderLineItem 수량2개_1000원_주문항목 = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(1000));
            OrderTable 주문_1번_테이블 = createOrderTable("1번 테이블", 3, true);
            Order 서빙된_매장식사_주문 = createOrder(OrderType.EAT_IN, OrderStatus.SERVED, List.of(수량2개_1000원_주문항목), 주문_1번_테이블);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(서빙된_매장식사_주문));
            when(orderRepository.existsByOrderTableAndStatusNot(주문_1번_테이블, OrderStatus.COMPLETED)).thenReturn(false);

            // Act
            Order completedOrder = orderService.complete(orderId);

            // Assert
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("매장식사 주문 서빙 - 이미 서빙된 매장식사 주문에 대한 서빙 시도")
        void serveAlreadyServedOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 서빙된_포장_주문 = createOrder(OrderType.EAT_IN, OrderStatus.SERVED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(서빙된_포장_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }
    }

    @Nested
    class DeliveryOrderTest {
        @Test
        @DisplayName("배달 주문 등록 - 정상적으로 배달 주문 등록 후 반환")
        void createValidOrderReturnsCreatedOrder() {
            // Arrange
            OrderLineItem 수량2개_1000원_주문항목 = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(1000));
            Order request = createOrder(OrderType.DELIVERY, OrderStatus.WAITING, List.of(수량2개_1000원_주문항목), "123 Street");
            Menu 주문항목_수량2개_1000원_메뉴 = createMenu("주문항목_수량2개_1000원_메뉴", BigDecimal.valueOf(1000), UUID.randomUUID(), true, Collections.emptyList());

            when(menuRepository.findById(any())).thenReturn(Optional.of(주문항목_수량2개_1000원_메뉴));
            when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(new Menu()));
            when(orderRepository.save(any(Order.class))).thenReturn(new Order());

            // Act
            Order 등록된_배달_주문 = orderService.create(request);

            // Assert
            assertThat(등록된_배달_주문).isNotNull();
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("배달 주문 완료 - 정상적으로 배달 주문 완료 후 반환")
        void completeValidOrderReturnsCompletedOrder() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 배달완료된_배달_주문 = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(배달완료된_배달_주문));

            // Act
            Order completedOrder = orderService.complete(orderId);

            // Assert
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("배달 주문 접수 - 배달 주문일 때 KitchenridersClient가 제대로 호출되는지 확인")
        void acceptDeliveryOrderCallsKitchenridersClientForDelivery() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 대기중_배달_주문 = createOrder(OrderType.DELIVERY, OrderStatus.WAITING, Collections.emptyList(), "123 Street");

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(대기중_배달_주문));
            doNothing().when(kitchenridersClient).requestDelivery(eq(orderId), any(BigDecimal.class), anyString());

            // Act
            Order 수락된_주문 = orderService.accept(orderId);

            // Assert
            assertThat(수락된_주문.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            verify(kitchenridersClient, times(1))
                    .requestDelivery(eq(orderId), any(BigDecimal.class), eq(대기중_배달_주문.getDeliveryAddress()));
        }

        @Test
        @DisplayName("배달 시작 - 배달 주문이 서빙되지 않은 상태에서 배달 시작 시도")
        void startDeliveryNonServedOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 수락된_배달_주문 = createOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(수락된_배달_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("배달 시작 - 이미 배달 시작된 주문에 대한 배달 시작 시도")
        void startDeliveryAlreadyStartedDeliveryThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 배달중_배달_주문 = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(배달중_배달_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("배달 시작 - 배달 주문이 아닌 주문에 대한 배달 시작 시도")
        void startDeliveryNonDeliveryOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 서빙된_포장_주문 = createOrder(OrderType.TAKEOUT, OrderStatus.SERVED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(서빙된_포장_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("배달 완료 - 배달 중이 아닌 주문에 대한 배달 완료 시도")
        void completeDeliveryNonDeliveringOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 수락된_배달_주문 = createOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(수락된_배달_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("배달 완료 - 배달 주문이 이미 배달 완료된 상태에서 배달 완료 시도")
        void completeDeliveryAlreadyDeliveredOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 배달완료된_배달_주문 = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(배달완료된_배달_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }
    }

    @Nested
    class TakeoutOrderTest {
        @Test
        @DisplayName("포장 주문 등록 - 정상적으로 포장 주문 등록 후 반환")
        void createValidOrderReturnsCreatedOrder() {
            // Arrange
            OrderLineItem 수량2개_1000원_주문항목 = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(1000));
            Order request = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, List.of(수량2개_1000원_주문항목));
            Menu 주문항목_수량2개_1000원_메뉴 = createMenu("주문항목_수량2개_1000원_메뉴", BigDecimal.valueOf(1000), UUID.randomUUID(), true, Collections.emptyList());

            when(menuRepository.findById(any())).thenReturn(Optional.of(주문항목_수량2개_1000원_메뉴));
            when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(new Menu()));
            when(orderRepository.save(any(Order.class))).thenReturn(new Order());

            // Act
            Order 등록된_주문 = orderService.create(request);

            // Assert
            assertThat(등록된_주문).isNotNull();
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("포장 주문 수락 - 정상적으로 포장 주문 수락 후 반환")
        void acceptValidOrderReturnsAcceptedOrder() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 대기중_포장_주문 = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, Collections.emptyList());
            대기중_포장_주문.setOrderTableId(null);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(대기중_포장_주문));

            // Act
            Order acceptedOrder = orderService.accept(orderId);

            // Assert
            assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("포장 주문 수락 - 이미 수락된 포장 주문에 대한 수락 시도")
        void acceptAlreadyAcceptedOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 수락된_포장_주문 = createOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(수락된_포장_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("포장 주문 수락 - 포장 주문이 대기 상태가 아닌 상태에서 수락 시도")
        void acceptNonWaitingOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 서빙된_포장_주문 = createOrder(OrderType.TAKEOUT, OrderStatus.SERVED, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(서빙된_포장_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }


        @Test
        @DisplayName("포장 주문 서빙 - 포장 주문이 수락되지 않은 상태에서 서빙 시도")
        void serveNonAcceptedOrderThrowsIllegalStateException() {
            // Arrange
            UUID orderId = UUID.randomUUID();
            Order 대기중_포장_주문 = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, Collections.emptyList());

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(대기중_포장_주문));

            // Act & Assert
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(IllegalStateException.class);
            verify(orderRepository, never()).save(any(Order.class));
        }
    }

    @Test
    @DisplayName("모든 주문 조회")
    void findAllOrdersReturnsAllOrders() {
        // Arrange
        List<Order> 전체_주문목록 = new ArrayList<>();
        전체_주문목록.add(createOrder(OrderType.TAKEOUT, OrderStatus.COMPLETED));
        전체_주문목록.add(createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED));

        when(orderRepository.findAll()).thenReturn(전체_주문목록);

        // Act
        List<Order> allOrders = orderService.findAll();

        // Assert
        assertThat(allOrders).hasSize(2);
    }
}
