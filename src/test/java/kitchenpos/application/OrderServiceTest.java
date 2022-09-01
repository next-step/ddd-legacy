package kitchenpos.application;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.application.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MenuRepository menuRepository;

    @Mock
    OrderTableRepository orderTableRepository;

    @Mock
    KitchenridersClient kitchenridersClient;

    @InjectMocks
    OrderService orderService;

    @DisplayName("주문 요청 시 주문 종류를 명시해야한다.")
    @Test
    void create_Illegal_EmptyOrderType() {
        // given
        Order request = new Order();

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 요청 시 주문 상품들을 명시해야한다.")
    @Test
    void create_Illegal_EmptyOrderLineItems() {
        // given
        Order request = new Order();
        request.setType(OrderType.DELIVERY);

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품들은 모두 존재하는 메뉴여야한다.")
    @Test
    void create_Illegal_NotExistingMenus() {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(10_000));

        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(Collections.singletonList(oli));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품들에 해당하는 메뉴는 모두 진열 상태여아한다.")
    @Test
    void create_Illegal_HiddenMenu() {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));
        menu.setDisplayed(false);

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(10_000));

        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(Collections.singletonList(oli));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상품들의 가격은 메뉴 가격들의 합과 같아야한다.")
    @Test
    void create_Illegal_NotMatchingPrice() {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(20_000));

        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(Collections.singletonList(oli));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "배송 주문은 배송 주소를 추가로 명시해야한다. source = {0}")
    @NullAndEmptySource
    void create_Illegal_MissingDeliveryAddress(String deliveryAddress) {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(10_000));

        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(Collections.singletonList(oli));
        request.setDeliveryAddress(deliveryAddress);

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 주문은 점유한 주문 테이블을 추가로 명시해야한다.")
    @Test
    void create_Illegal_OrderTableOccupied() {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(10_000));

        OrderTable orderTable = anOrderTable(false);

        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(Collections.singletonList(oli));
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 주문 성공 케이스")
    @Test
    void create_EAT_IN_ORDER() {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(10_000));

        OrderTable orderTable = anOrderTable(true);

        Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(Collections.singletonList(oli));
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderRepository.save(any())).then(i -> i.getArgument(0, Order.class));

        // when
        Order saved = orderService.create(request);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(saved.getOrderDateTime()).isNotNull();
        assertThat(saved.getOrderLineItems()).hasSize(1);
    }

    @DisplayName("배송 주문 성공 케이스")
    @Test
    void create_DELIVERY_ORDER() {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aMenuProduct(10_000, 1));

        OrderLineItem oli = anOrderLineItem(menu, 1);
        oli.setPrice(BigDecimal.valueOf(10_000));

        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(Collections.singletonList(oli));
        request.setDeliveryAddress("서울시 어딘가");

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderRepository.save(any())).then(i -> i.getArgument(0, Order.class));

        // when
        Order saved = orderService.create(request);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(saved.getOrderDateTime()).isNotNull();
        assertThat(saved.getOrderLineItems()).hasSize(1);
    }

    @ParameterizedTest(name = "대기중인 주문만 수락할 수 있다. source = {0}")
    @EnumSource(
            value = OrderStatus.class,
            names = {"WAITING"},
            mode = EnumSource.Mode.EXCLUDE)
    void accept_Illegal_State(OrderStatus orderStatus) {
        // given
        Order order = anOrder(OrderType.DELIVERY);
        order.setStatus(orderStatus);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when + then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배송 주문 수락 시 배송 업체에게 배송 요청을 하고 주문 상태가 수락으로 변경된다.")
    @Test
    void accept_DELIVERY_ORDER() {
        // given
        Order order = anOrder(OrderType.DELIVERY);
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setDeliveryAddress("서울시 어딘가");

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        Order acceptedOrder = orderService.accept(order.getId());

        // then
        verify(kitchenridersClient).requestDelivery(order.getId(), BigDecimal.valueOf(10_000), "서울시 어딘가");
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @ParameterizedTest(name = "수락 상태의 주문만 서빙 상태로 변경할 수 있다. source = {0}")
    @EnumSource(
            value = OrderStatus.class,
            names = {"ACCEPTED"},
            mode = EnumSource.Mode.EXCLUDE)
    void serve_Illegal_State(OrderStatus orderStatus) {
        // given
        Order order = anOrder(OrderType.DELIVERY);
        order.setStatus(orderStatus);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when + then
        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("수락 상태의 주문을 서빙 상태로 변경한다.")
    @Test
    void serve() {
        // given
        Order order = anOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        orderService.serve(order.getId());

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @ParameterizedTest(name = "주문 배송은 배송됨 상태에서만 완료 할 수 있다. source = {0}")
    @EnumSource(
            value = OrderStatus.class,
            names = {"DELIVERED"},
            mode = EnumSource.Mode.EXCLUDE)
    void complete_DELIVERY_ORDER_Illegal_State(OrderStatus orderStatus) {
        // given
        Order order = anOrder(OrderType.DELIVERY);
        order.setStatus(orderStatus);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "매장 주문은 서빙된 상태에서만 완료 할 수 있다. source = {0}")
    @EnumSource(
            value = OrderStatus.class,
            names = {"SERVED"},
            mode = EnumSource.Mode.EXCLUDE)
    void complete_EAT_IN_ORDER_Illegal_State(OrderStatus orderStatus) {
        // given
        Order order = anOrder(OrderType.EAT_IN);
        order.setStatus(orderStatus);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 주문 완료시 주문 상태가 바뀌고 점유한 테이블이 비워진다.")
    @Test
    void complete_EAT_IN_ORDER() {
        // given
        OrderTable orderTable = anOrderTable(true);

        Order order = anOrder(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderTable(orderTable);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(false);

        // when
        Order completedOrder = orderService.complete(order.getId());

        // then
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }
}
