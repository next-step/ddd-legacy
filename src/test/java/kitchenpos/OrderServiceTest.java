package kitchenpos;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
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
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 등록 - 정상적으로 주문 등록 후 반환")
    void createValidOrderReturnsCreatedOrder() {
        // Arrange
        OrderLineItem orderLineItem = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(1000));
        Order request = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, List.of(orderLineItem));
        Menu menu = createMenu("테스트 메뉴", BigDecimal.valueOf(1000), UUID.randomUUID(), true, Collections.emptyList());

        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(new Menu()));
        when(orderRepository.save(any(Order.class))).thenReturn(new Order());

        // Act
        Order createdOrder = orderService.create(request);

        // Assert
        assertThat(createdOrder).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 등록 - 주문 메뉴 수량이 없을 때 예외 발생")
    void createNoOrderLineItemsThrowsIllegalArgumentException() {
        // Arrange
        Order request = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 등록 - 주문 유형이 없을 때 예외 발생")
    void createNoOrderTypeThrowsIllegalArgumentException() {
        // Arrange
        OrderLineItem orderLineItem = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(16000));
        Order request = createOrder(null, OrderStatus.WAITING, List.of(orderLineItem));

        // Act & Assert
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 등록 - 메뉴 아이템을 찾을 수 없을 때 예외 발생")
    void createMenuItemsNotFoundThrowsIllegalArgumentException() {
        // Arrange
        OrderLineItem orderLineItem = createOrderLineItem(UUID.randomUUID(), 2, BigDecimal.valueOf(1000));
        Order request = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, List.of(orderLineItem));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 수락 - 정상적으로 주문 수락 후 반환")
    void acceptValidOrderReturnsAcceptedOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, Collections.emptyList());
        order.setOrderTableId(null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Order acceptedOrder = orderService.accept(orderId);

        // Assert
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문 수락 - 이미 수락된 주문에 대한 수락 시도")
    void acceptAlreadyAcceptedOrderThrowsIllegalStateException() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.accept(orderId))
                .isInstanceOf(IllegalStateException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 수락 - 주문이 대기 상태가 아닌 상태에서 수락 시도")
    void acceptNonWaitingOrderThrowsIllegalStateException() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.TAKEOUT, OrderStatus.SERVED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.accept(orderId))
                .isInstanceOf(IllegalStateException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 접수 - 배달 주문일 때 KitchenridersClient가 제대로 호출되는지 확인")
    void acceptDeliveryOrderCallsKitchenridersClientForDelivery() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING, Collections.emptyList(), "123 Street");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(kitchenridersClient).requestDelivery(eq(orderId), any(BigDecimal.class), anyString());

        // Act
        Order acceptedOrder = orderService.accept(orderId);

        // Assert
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(kitchenridersClient, times(1))
                .requestDelivery(eq(orderId), any(BigDecimal.class), eq(order.getDeliveryAddress()));
    }

    @Test
    @DisplayName("주문 서빙 - 이미 서빙된 주문에 대한 서빙 시도")
    void serveAlreadyServedOrderThrowsIllegalStateException() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.TAKEOUT, OrderStatus.SERVED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.serve(orderId))
                .isInstanceOf(IllegalStateException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 서빙 - 주문 수락되지 않은 상태에서 서빙 시도")
    void serveNonAcceptedOrderThrowsIllegalStateException() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.serve(orderId))
                .isInstanceOf(IllegalStateException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("배달 시작 - 배달 주문이 아닌 주문에 대한 배달 시작 시도")
    void startDeliveryNonDeliveryOrderThrowsIllegalStateException() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.TAKEOUT, OrderStatus.SERVED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.startDelivery(orderId))
                .isInstanceOf(IllegalStateException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("배달 시작 - 주문 서빙되지 않은 상태에서 배달 시작 시도")
    void startDeliveryNonServedOrderThrowsIllegalStateException() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

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
        Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERING, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

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
        Order order = createOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                .isInstanceOf(IllegalStateException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("배달 완료 - 주문이 이미 배달 완료된 상태에서 배달 완료 시도")
    void completeDeliveryAlreadyDeliveredOrderThrowsIllegalStateException() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                .isInstanceOf(IllegalStateException.class);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("주문 완료 - 정상적으로 주문 완료 후 반환")
    void completeValidOrderReturnsCompletedOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Order order = createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED, Collections.emptyList());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        Order completedOrder = orderService.complete(orderId);

        // Assert
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("모든 주문 조회")
    void findAllOrdersReturnsAllOrders() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        orders.add(createOrder(OrderType.TAKEOUT, OrderStatus.COMPLETED));
        orders.add(createOrder(OrderType.DELIVERY, OrderStatus.DELIVERED));

        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<Order> allOrders = orderService.findAll();

        // Assert
        assertThat(allOrders).hasSize(2);
    }
}
