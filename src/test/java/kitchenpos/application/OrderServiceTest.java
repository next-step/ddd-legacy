package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository,
            menuRepository,
            orderTableRepository,
            kitchenridersClient
        );
    }

    @DisplayName("'주문'을 생성할 때 '주문 유형'이 필요하다.")
    @Test
    void createOrderWithoutType() {
        // given
        final Order order = new Order();

        // when & then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("'주문'을 생성할 때 '주문 항목'이 필요하다.")
    @Test
    void createOrderWithoutOrderLineItems() {
        // given
        final Order order = new Order();
        order.setType(OrderType.EAT_IN);

        // when & then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("'주문'을 생성할 때 '주문 항목'에 포함 된 '메뉴'가 존재해야 한다.")
    @Test
    void createOrderWithoutMenus() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());

        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(List.of(orderLineItem));

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        // then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`매장 식사` 유형의 `주문`을 생성할 수 있다.")
    @Test
    void createEatInOrder() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice());

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.EAT_IN);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(true);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.save(any())).thenReturn(order);

        // when
        final Order createdOrder = orderService.create(order);

        // then
        assertThat(createdOrder).isNotNull();
    }

    @DisplayName("`매장 식사` 유형 이외 `주문`은 `주문 항목`의 `수량`이 0보다 커야 한다.")
    @Test
    void createOrderWithNegativeQuantity() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(-1L);
        orderLineItem.setPrice(menu.getPrice());

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.DELIVERY);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

        // when
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`매장 식사` 유형의 `주문`을 생성할 때 `주문 테이블`이 필요하다.")
    @Test
    void createEatInOrderWithoutOrderTable() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice());

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.EAT_IN);

        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setOccupied(false);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        // when
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`배달` 유형의 `주문`을 생성할 수 있다.")
    @Test
    void createDeliveryOrder() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice());

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("서울시 강남구");

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderRepository.save(any())).thenReturn(order);

        // when
        final Order createdOrder = orderService.create(order);

        // then
        assertThat(createdOrder).isNotNull();
    }

    @DisplayName("`배달` 유형의 `주문`을 생성할 때 `배달 주소`가 필요하다")
    @Test
    void createDeliveryOrderWithoutDeliveryAddress() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice());

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.DELIVERY);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`주문`을 생성할 때 해당 `메뉴`는 노출 상태여야 한다")
    @Test
    void createOrderWithNotDisplayedMenu() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(false);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice());

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.DELIVERY);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`주문`을 생성할 때 해당 `메뉴` 의 가격은 `주문 항목`의 가격과 같아야 한다")
    @Test
    void createOrderWithDifferentPriceMenu() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(20_000L));

        final Order order = new Order();
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.DELIVERY);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`주문`을 수락할 수 있다.")
    @Test
    void acceptOrder() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        final Order acceptedOrder = orderService.accept(order.getId());

        // then
        assertThat(acceptedOrder).isNotNull();
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("`주문`을 수락할 때 WAITING 상태가 아닌 경우 예외가 발생한다.")
    @Test
    void acceptOrderWithNotWaitingStatus() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`주문` 유형이 배달(DELIVERY)인 경우, `주문 항목` 들의 가격 총합과 함께 배달 요청을 해야 한다.")
    @Test
    void acceptDeliveryOrder() {
        // given
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("BHC HOT후라이드");
        menu.setPrice(BigDecimal.valueOf(21_000L));
        menu.setDisplayed(true);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(menu.getPrice());

        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        final Order acceptedOrder = orderService.accept(order.getId());

        // then
        assertThat(acceptedOrder).isNotNull();
    }

    @DisplayName("`주문`을 서빙할 수 있다.")
    @Test
    void serveOrder() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        final Order completedOrder = orderService.serve(order.getId());

        // then
        assertThat(completedOrder).isNotNull();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("`주문`을 서빙할 때 ACCEPTED 상태가 아닌 경우 예외가 발생한다.")
    @Test
    void serveOrderWithNotAcceptedStatus() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.serve(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`주문` 배달시작할 수 있다.")
    @Test
    void startDeliveryOrder() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.SERVED);
        order.setType(OrderType.DELIVERY);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        final Order startedOrder = orderService.startDelivery(order.getId());

        // then
        assertThat(startedOrder).isNotNull();
        assertThat(startedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("`주문` 배달시작할 때 배달 타입이 아닌 경우 예외가 발생한다.")
    @Test
    void startDeliveryOrderWithNotDeliveryType() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);
        order.setType(OrderType.TAKEOUT);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`주문` 배달시작할 때 SERVED 상태가 아닌 경우 예외가 발생한다.")
    @Test
    void startDeliveryOrderWithNotServedStatus() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setType(OrderType.DELIVERY);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`주문` 배달완료할 수 있다.")
    @Test
    void completeDeliveryOrder() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.DELIVERING);
        order.setType(OrderType.DELIVERY);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        final Order completedOrder = orderService.completeDelivery(order.getId());

        // then
        assertThat(completedOrder).isNotNull();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("`주문` 배달완료할 때 SERVED 상태가 아닌 경우 예외가 발생한다.")
    @Test
    void completeDeliveryOrderWithNotServedStatus() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setType(OrderType.DELIVERY);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("'배달' 주문 유형의 `주문`을 완료할 수 있다.")
    @Test
    void completeDeliveryOrderWithDeliveryType() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.DELIVERED);
        order.setType(OrderType.DELIVERY);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        final Order completedOrder = orderService.complete(order.getId());

        // then
        assertThat(completedOrder).isNotNull();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("`배달` 주문 유형의 `주문`을 완료할 때 '배달완료' 상태가 아닌 경우 예외가 발생한다.")
    @Test
    void completeDeliveryOrderWithoutDeliveredStatus() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.DELIVERING);
        order.setType(OrderType.DELIVERY);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`매장 식사` 주문 유형의 `주문`을 완료할 수 있다.")
    @Test
    void completeEatInOrder() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.SERVED);
        order.setType(OrderType.EAT_IN);
        order.setOrderTable(new OrderTable());

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

        // when
        final Order completedOrder = orderService.complete(order.getId());

        // then
        assertThat(completedOrder).isNotNull();
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("`매장 식사` 주문 유형의 `주문`을 완료할 때 '서빙됨' 상태가 아닌 경우 예외가 발생한다.")
    @Test
    void completeEatInOrderWithoutOrderTable() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setType(OrderType.EAT_IN);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`주문`을 조회할 수 있다.")
    @Test
    void listOrders() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);

        when(orderRepository.findAll()).thenReturn(List.of(order));

        // when
        final List<Order> orders = orderService.findAll();

        // then
        assertThat(orders).isNotEmpty();
    }
}
