package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("타입은 필수이다")
    void requiredType() {
        final var request = new Order();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }

    @ParameterizedTest
    @DisplayName("주문 상품은 null 이거나 비어있으면 안된다")
    @NullAndEmptySource
    void requiredOrderLineItems(List<OrderLineItem> orderLineItems) {
        final var request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(orderLineItems);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("주문 상품의 수와 메뉴의 수는 같아야 한다")
    void orderLineItemsSizeAndMenusSizeAreMustBeSame() {
        final var request = new Order();
        request.setType(OrderType.EAT_IN);
        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        request.setOrderLineItems(List.of(orderLineItem));

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("매장이 아닌 주문의 주문상품 수량은 음수가 될 수 없다")
    void notEatInOrderNegativeOrderLineItem() {
        final var request = new Order();
        request.setType(OrderType.TAKEOUT);

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(-1);
        request.setOrderLineItems(List.of(orderLineItem));

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(new Menu()));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("메뉴 중 비노출이 있다면 주문할 수 없다")
    void requiredAllMenuDisplayed() {
        final var request = new Order();
        request.setType(OrderType.EAT_IN);

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(-1);
        request.setOrderLineItems(List.of(orderLineItem));

        Menu menu = new Menu();
        menu.setDisplayed(false);
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("메뉴의 가격과 주문 상품의 가격이 다를수 없다")
    void menuPriceAndOrderLineItemPriceMustBeSame() {
        final var request = new Order();
        request.setType(OrderType.EAT_IN);

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.ZERO);
        request.setOrderLineItems(List.of(orderLineItem));

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.TEN);
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }

    @ParameterizedTest
    @DisplayName("배달 주문은 배달 주소가 필수")
    @NullAndEmptySource
    void requiresDeliveryAddress(final String deliveryAddress) {
        final var request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setDeliveryAddress(deliveryAddress);

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.TEN);
        request.setOrderLineItems(List.of(orderLineItem));

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.TEN);
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("정상 배달 주문")
    void deliveryOrder() {
        final var request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setDeliveryAddress("서울시");

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.TEN);
        request.setOrderLineItems(List.of(orderLineItem));

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.TEN);
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));

        final var order = orderService.create(request);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getDeliveryAddress()).isEqualTo("서울시");
    }

    @Test
    @DisplayName("매장 주문은 채워진 테이블 주문이여야 한다")
    void requiresOccupiedTable() {
        final var request = new Order();
        request.setType(OrderType.EAT_IN);

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.TEN);
        request.setOrderLineItems(List.of(orderLineItem));

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.TEN);

        OrderTable orderTable = new OrderTable();
        orderTable.setOccupied(false);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("매장 정상 주문")
    void eatInOrder() {
        final var request = new Order();
        request.setType(OrderType.EAT_IN);

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.TEN);
        request.setOrderLineItems(List.of(orderLineItem));

        Menu menu = new Menu();
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.TEN);

        OrderTable orderTable = new OrderTable();
        orderTable.setOccupied(true);

        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.save(any())).then(invocationOnMock -> invocationOnMock.getArgument(0));

        final var order = orderService.create(request);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getOrderTable()).isNotNull();
    }

    @Test
    @DisplayName("주문을 수락하기 위해서는 주문이 대기중이어야 한다")
    void requiresWaitingToAccept() {
        final var order = new Order();
        order.setStatus(OrderStatus.COMPLETED);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.accept(UUID.randomUUID()));
    }

    @Test
    @DisplayName("수락시 배달 주문은 배달 대해사에 배달을 요청한다")
    void acceptDeliveryOrderRequestDelivery() {
        final var order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("서울시");
        order.setStatus(OrderStatus.WAITING);

        final var menu = new Menu();
        menu.setPrice(BigDecimal.TEN);

        final var orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(1);
        orderLineItem.setMenu(menu);

        order.setOrderLineItems(List.of(orderLineItem));

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        Order accepted = orderService.accept(UUID.randomUUID());

        verify(kitchenridersClient, times(1)).requestDelivery(any(UUID.class), any(BigDecimal.class), any(String.class));
        assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("정상 주문 수락")
    void accept() {
        final var order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        Order accepted = orderService.accept(UUID.randomUUID());

        assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }


    @Test
    void serve() {
    }

    @Test
    void startDelivery() {
    }

    @Test
    void completeDelivery() {
    }

    @Test
    void complete() {
    }

    @Test
    void findAll() {
    }
}
