package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private final KitchenridersClient kitchenridersClient = mock(KitchenridersClient.class);

    private final OrderService orderService = new OrderService(orderRepository, menuRepository,
        orderTableRepository, kitchenridersClient);

    @Test
    @DisplayName("주문 유형이 없을 경우 예외를 발생 시킨다.")
    void createWithEmptyOrderType() {
        var request = new Order();
        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 유형이 필요 합니다.")
        ;
    }

    @Test
    @DisplayName("주문 항목이 없을 경우 예외를 발생 시킨다.")
    void createWithEmptyOrderLineItem() {
        var request = new Order();
        request.setType(OrderType.EAT_IN);

        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("1개 이상의 주문 항목이 필요합니다.")
        ;
    }

    @Test
    @DisplayName("주문 항목과 메뉴가 일치하지 않을 경우 예외를 발생 시킨다.")
    void createWithUnknownMenu() {
        var request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(List.of(new OrderLineItem()));

        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("찾을 수 없는 주문 메뉴가 있습니다.")
        ;
    }

    @Test
    @DisplayName("주문 수량이 0 보다 작은 경우 예외를 발생 시킨다.")
    void createWithoutQuantity() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(-1L);

        var request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(new Menu()));

        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("주문 수량은 0 보다 작을 수 없습니다.")
        ;
    }

    @Test
    @DisplayName("메뉴가 숨김 상태인 경우 예외를 발생 시킨다.")
    void createWithHiddenMenu() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1L);

        var menu = new Menu();
        menu.setDisplayed(Boolean.FALSE);

        var request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("주문 가능 한 메뉴가 아닙니다.")
        ;
    }

    @Test
    @DisplayName("메뉴 가격이 주문 가격과 다른 경우 예외를 발생 시킨다.")
    void createWithDifferentPrice() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(20_000L));

        var menu = new Menu();
        menu.setDisplayed(Boolean.TRUE);
        menu.setPrice(BigDecimal.valueOf(30_000L));

        var request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("메뉴 가격이 주문 가격과 다릅니다.")
        ;
    }

    @Test
    @DisplayName("배달 주소가 없는 경우 예외를 발생 시킨다.")
    void createWithEmptyAddress() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(30_000L));

        var menu = new Menu();
        menu.setDisplayed(Boolean.TRUE);
        menu.setPrice(BigDecimal.valueOf(30_000L));

        var request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("배달 주소가 필요합니다.")
        ;
    }

    @Test
    @DisplayName("테이블 이용 전의 경우 예외를 발생 시킨다.")
    void createWithBeforeOccupied() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.valueOf(30_000L));

        var menu = new Menu();
        menu.setDisplayed(Boolean.TRUE);
        menu.setPrice(BigDecimal.valueOf(30_000L));

        var orderTable = new OrderTable();
        orderTable.setOccupied(Boolean.FALSE);

        var request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(List.of(orderLineItem));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        assertThatThrownBy(() -> orderService.create(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("테이블 이용 전 입니다.")
        ;
    }

    @Test
    @DisplayName("주문 상태가 대기중이 아닐 경우 예외를 발생 시킨다.")
    void accept() {
        var order = new Order();
        order.setStatus(OrderStatus.SERVED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
        ;
    }

    @Test
    @DisplayName("주문 상태가 수락됨 아닐 경우 예외를 발생 시킨다.")
    void serve() {
        var order = new Order();
        order.setStatus(OrderStatus.SERVED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
        ;
    }

    @Test
    @DisplayName("주문 유형이 배송이 아닐 경우 예외를 발생 시킨다.")
    void startDelivery() {
        var order = new Order();
        order.setType(OrderType.EAT_IN);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("배송 유형 주문만 배송 시작이 가능 합니다.")
        ;
    }

    @Test
    @DisplayName("배송 시작 할 수 있는 상태가 아닌 경우 예외를 발생 시킨다.")
    void startDeliveryNotServed() {
        var order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("배송 가능한 상태가 아닙니다.")
        ;
    }

    @Test
    @DisplayName("배송 완료 할 수 있는 상태가 아닌 경우 예외를 발생 시킨다.")
    void completeDelivery() {
        var order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
        ;
    }

    @Test
    @DisplayName("배송 주문은 배송 완료 전 주문 완료 시 예외를 발생 시킨다.")
    void completeBeforeDelivered() {
        var order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("배송 완료 전 까지는 주문 완료를 할 수 없습니다.")
        ;
    }

    @Test
    @DisplayName("음식 제공 전 주문 완료 시 예외를 발생 시킨다.")
    void completeBeforeServed() {
        var order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.ACCEPTED);
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("음식 제공 전 까지는 주문 완료를 할 수 없습니다.")
        ;
    }
}