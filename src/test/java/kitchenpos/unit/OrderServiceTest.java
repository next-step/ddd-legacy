package kitchenpos.unit;

import kitchenpos.application.OrderService;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Riders;
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

import static kitchenpos.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Menu MENU = aMenu("후라이드 치킨", 10_000, aChickenMenuProduct(10_000, 1));

    @Mock
    OrderRepository orderRepository;

    @Mock
    MenuRepository menuRepository;

    @Mock
    OrderTableRepository orderTableRepository;

    @Mock
    Riders riders;

    @InjectMocks
    OrderService orderService;

    @DisplayName("주문 요청 시 주문 종류를 명시해야한다.")
    @Test
    void create_Illegal_EmptyOrderType() {
        // given
        Order request = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));
        request.setType(null);

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 요청 시 주문 상품들을 명시해야한다.")
    @Test
    void create_Illegal_EmptyOrderLineItems() {
        // given
        Order request = aDeliveryOrder("서울시 어딘가");

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품들은 모두 존재하는 메뉴여야한다.")
    @Test
    void create_Illegal_NotExistingMenus() {
        // given
        Order request = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품들에 해당하는 메뉴는 모두 진열 상태여아한다.")
    @Test
    void create_Illegal_HiddenMenu() {
        // given
        Menu hiddenMenu = aMenu("후라이드 치킨", 10_000, aChickenMenuProduct(10_000, 1));
        hiddenMenu.setDisplayed(false);
        Order request = aDeliveryOrder("서울시 어딘가", anOrderLineItem(hiddenMenu, 1));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(hiddenMenu));
        when(menuRepository.findById(hiddenMenu.getId())).thenReturn(Optional.of(hiddenMenu));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상품들의 가격은 메뉴 가격들의 합과 같아야한다.")
    @Test
    void create_Illegal_NotMatchingPrice() {
        // given
        Menu menu = aMenu("후라이드 치킨", 10_000, aChickenMenuProduct(10_000, 1));
        OrderLineItem orderLineItem = anOrderLineItem(menu, 1);
        orderLineItem.setPrice(BigDecimal.valueOf(20_000));
        Order request = aDeliveryOrder("서울시 어딘가", orderLineItem);

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(menu));
        when(menuRepository.findById(MENU.getId())).thenReturn(Optional.of(MENU));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "배송 주문은 배송 주소를 추가로 명시해야한다. source = {0}")
    @NullAndEmptySource
    void create_Illegal_MissingDeliveryAddress(String deliveryAddress) {
        // given
        Order request = aDeliveryOrder(deliveryAddress, anOrderLineItem(MENU, 1));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(MENU));
        when(menuRepository.findById(MENU.getId())).thenReturn(Optional.of(MENU));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 주문은 점유한 주문 테이블을 추가로 명시해야한다.")
    @Test
    void create_Illegal_OrderTableOccupied() {
        // given
        OrderTable orderTable = anOrderTable(false);
        Order request = anEatInOrder(orderTable, anOrderLineItem(MENU, 1));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(MENU));
        when(menuRepository.findById(MENU.getId())).thenReturn(Optional.of(MENU));
        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));

        // when + then
        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 주문 성공 케이스")
    @Test
    void create_EAT_IN_ORDER() {
        // given
        OrderTable orderTable = anOrderTable(true);
        Order request = anEatInOrder(orderTable, anOrderLineItem(MENU, 1));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(MENU));
        when(menuRepository.findById(MENU.getId())).thenReturn(Optional.of(MENU));
        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderRepository.save(any())).then(i -> i.getArgument(0, Order.class));

        // when
        Order saved = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull(),
                () -> assertThat(saved.getOrderLineItems()).hasSize(1)
        );
    }

    @DisplayName("배송 주문 성공 케이스")
    @Test
    void create_DELIVERY_ORDER() {
        // given
        Order request = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));

        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.singletonList(MENU));
        when(menuRepository.findById(MENU.getId())).thenReturn(Optional.of(MENU));
        when(orderRepository.save(any())).then(i -> i.getArgument(0, Order.class));

        // when
        Order saved = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.DELIVERY),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull(),
                () -> assertThat(saved.getOrderLineItems()).hasSize(1)
        );
    }

    @ParameterizedTest(name = "대기중인 주문만 수락할 수 있다. source = {0}")
    @EnumSource(
            value = OrderStatus.class,
            names = {"WAITING"},
            mode = EnumSource.Mode.EXCLUDE)
    void accept_Illegal_State(OrderStatus orderStatus) {
        // given
        Order order = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));
        order.setStatus(orderStatus);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when + then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배송 주문 수락 시 배송 업체에게 배송 요청을 하고 주문 상태가 수락으로 변경된다.")
    @Test
    void accept_DELIVERY_ORDER() {
        // given
        Order order = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));
        order.setStatus(OrderStatus.WAITING);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when
        Order acceptedOrder = orderService.accept(order.getId());

        // then
        verify(riders).requestDelivery(order.getId(), BigDecimal.valueOf(10_000), "서울시 어딘가");
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @ParameterizedTest(name = "수락 상태의 주문만 서빙 상태로 변경할 수 있다. source = {0}")
    @EnumSource(
            value = OrderStatus.class,
            names = {"ACCEPTED"},
            mode = EnumSource.Mode.EXCLUDE)
    void serve_Illegal_State(OrderStatus orderStatus) {
        // given
        Order order = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));
        order.setStatus(orderStatus);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // when + then
        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("수락 상태의 주문을 서빙 상태로 변경한다.")
    @Test
    void serve() {
        // given
        Order order = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

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
        Order order = aDeliveryOrder("서울시 어딘가", anOrderLineItem(MENU, 1));
        order.setStatus(orderStatus);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

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
        Order order = anEatInOrder(anOrderTable(true), anOrderLineItem(MENU, 1));
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
        Order order = anEatInOrder(orderTable, anOrderLineItem(MENU, 1));
        order.setStatus(OrderStatus.SERVED);

        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(false);

        // when
        Order completedOrder = orderService.complete(order.getId());

        // then
        assertAll(
                () -> assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(orderTable.isOccupied()).isFalse()
        );
    }
}
