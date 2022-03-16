package kitchenpos.unit;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static kitchenpos.unit.fixture.MenuFixture.*;
import static kitchenpos.unit.fixture.OrderFixture.*;
import static kitchenpos.unit.fixture.OrderTableFixture.createOrderTable;
import static kitchenpos.unit.fixture.OrderTableFixture.테이블_1번;
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

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("배달 주문을 생성한다")
    @Test
    void createDelivery() {
        // given
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(배달_주문_메뉴_목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));
        when(orderRepository.save(any(Order.class))).thenReturn(배달_주문);

        // when
        Order saveOrder = orderService.create(배달_주문);

        // then
        assertThat(saveOrder.getType()).isEqualTo(DELIVERY);
        assertThat(saveOrder.getStatus()).isEqualTo(WAITING);
        assertThat(saveOrder.getDeliveryAddress()).isEqualTo(배달_주문.getDeliveryAddress());
        assertThat(saveOrder.getOrderTable()).isNull();
    }

    @DisplayName("배달 주문은 주소를 입력하지 않으면 등록되지 않는다")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void createDeliveryInvalidAddress(String deliveryAddress) {
        // given
        Order invalidOrder = createOrderWithMenus(DELIVERY, deliveryAddress, null, 배달_주문_메뉴_목록, WAITING);
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(배달_주문_메뉴_목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));

        // when
        // then
        assertThatThrownBy(() -> orderService.create(invalidOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포장 주문을 생성한다")
    @Test
    void createTakeout() {
        // given
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(포장_주문_메뉴_목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));
        when(menuRepository.findById(두그릇_세트.getId())).thenReturn(Optional.of(두그릇_세트));
        when(orderRepository.save(any(Order.class))).thenReturn(포장_주문);

        // when
        Order saveOrder = orderService.create(포장_주문);

        // then
        assertThat(saveOrder.getType()).isEqualTo(TAKEOUT);
        assertThat(saveOrder.getStatus()).isEqualTo(WAITING);
        assertThat(saveOrder.getDeliveryAddress()).isNull();
        assertThat(saveOrder.getOrderTable()).isNull();
    }

    @DisplayName("매장 내 식사 주문을 생성한다")
    @Test
    void createEatIn() {
        // given
        OrderTable orderTable = createOrderTable("1번", 10, false);

        when(menuRepository.findAllByIdIn(anyList())).thenReturn(매장식사_주문_메뉴_목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));
        when(menuRepository.findById(두그릇_세트.getId())).thenReturn(Optional.of(두그릇_세트));
        when(menuRepository.findById(세그릇_세트.getId())).thenReturn(Optional.of(세그릇_세트));
        when(orderRepository.save(any(Order.class))).thenReturn(매장식사_주문);
        when(orderTableRepository.findById(테이블_1번.getId())).thenReturn(Optional.of(orderTable));

        // when
        Order saveOrder = orderService.create(매장식사_주문);

        // then
        assertThat(saveOrder.getType()).isEqualTo(EAT_IN);
        assertThat(saveOrder.getStatus()).isEqualTo(WAITING);
        assertThat(saveOrder.getDeliveryAddress()).isNull();
        assertThat(saveOrder.getOrderTable()).isNotNull();
    }

    @DisplayName("매장 내 식사 주문은 테이블이 비어있는 상태여야 한다")
    @Test
    void createEatInEmptyTable() {
        // given
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(매장식사_주문_메뉴_목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(한그릇_세트));
        when(menuRepository.findById(두그릇_세트.getId())).thenReturn(Optional.of(두그릇_세트));
        when(menuRepository.findById(세그릇_세트.getId())).thenReturn(Optional.of(세그릇_세트));
        when(orderTableRepository.findById(테이블_1번.getId())).thenReturn(Optional.of(테이블_1번));

        // when
        // then
        assertThatThrownBy(() -> orderService.create(매장식사_주문))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 유형을 입력하지 않으면 주문이 등록되지 않는다")
    @ParameterizedTest
    @NullSource
    void createInvalidType(OrderType orderType) {
        assertThatThrownBy(() -> orderService.create(createOrderWithMenus(orderType, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, WAITING)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 메뉴 목록을 입력하지 않으면 주문이 등록되지 않는다")
    @ParameterizedTest
    @EmptySource
    @NullSource
    void createInvalidItems(List<OrderLineItem> orderLineItems) {
        assertThatThrownBy(() -> orderService.create(createOrder(DELIVERY, DELIVERY_ADDRESS, null, orderLineItems, WAITING)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달과 포장은 주문 메뉴 수량이 0개 이상이어야 한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void createInvalidItemSize(OrderType orderType) {
        // given
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItem(한그릇_세트, -1L));
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(배달_주문_메뉴_목록);

        // when
        // then
        assertThatThrownBy(() -> orderService.create(createOrder(orderType, DELIVERY_ADDRESS, null, orderLineItems, WAITING)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 비공개인 경우 등록되지 않는다")
    @Test
    void createHideMenu() {
        // given
        Menu hideMenu = createMenu(탕수육_세트, "한그릇 세트", BigDecimal.valueOf(14000), 한그릇_세트_상품목록, false);
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(배달_주문_메뉴_목록);
        when(menuRepository.findById(any(UUID.class))).thenReturn(Optional.of(hideMenu));

        // when
        // then
        assertThatThrownBy(() -> orderService.create(배달_주문))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("선택한 메뉴의 가격과 등록된 메뉴의 가격이 다른 경우 등록되지 않는다")
    @Test
    void createDifferentPrice() {
        // given
        when(menuRepository.findAllByIdIn(anyList())).thenReturn(배달_주문_메뉴_목록);
        when(menuRepository.findById(한그릇_세트.getId())).thenReturn(Optional.of(두그릇_세트));

        // when
        // then
        assertThatThrownBy(() -> orderService.create(배달_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문을 수락 상태로 변경한다")
    @Test
    void accept() {
        // given
        Order waitingOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, WAITING);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(waitingOrder));

        // when
        Order acceptedOrder = orderService.accept(UUID.randomUUID());

        // then
        assertThat(acceptedOrder.getStatus()).isEqualTo(ACCEPTED);
    }

    @DisplayName("대기중인 주문만 수락 상태로 변경할 수 있다")
    @Test
    void acceptNotWaiting() {
        // given
        Order acceptedOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, ACCEPTED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(acceptedOrder));

        // when
        // then
        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 제공 상태로 변경한다")
    @Test
    void serve() {
        // given
        Order invalidStatusOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, ACCEPTED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(invalidStatusOrder));

        // when
        Order servedOrder = orderService.serve(UUID.randomUUID());

        // then
        assertThat(servedOrder.getStatus()).isEqualTo(SERVED);
    }

    @DisplayName("수락 상태인 주문만 제공 상태로 변경할 수 있다")
    @Test
    void serveNotAccepted() {
        // given
        Order invalidStatusOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, SERVED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(invalidStatusOrder));

        // when
        // then
        assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 배달 시작 상태로 변경한다")
    @Test
    void startDelivery() {
        // given
        Order servedOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, SERVED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(servedOrder));

        // when
        Order deliveringOrder = orderService.startDelivery(UUID.randomUUID());

        // then
        assertThat(deliveringOrder.getStatus()).isEqualTo(DELIVERING);
    }

    @DisplayName("제공 상태인 주문만 배달 시작 상태로 변경할 수 있다")
    @Test
    void startDeliveryNotServed() {
        // given
        Order invalidStatusOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, ACCEPTED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(invalidStatusOrder));

        // when
        // then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문만 배달 시작 상태로 변경할 수 있다")
    @Test
    void startDeliveryNotDelivery() {
        // given
        Order invalidTypeOrder = createOrderWithMenus(TAKEOUT, null, null, 포장_주문_메뉴_목록, SERVED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(invalidTypeOrder));

        // when
        // then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 배달 완료 상태로 변경한다")
    @Test
    void completeDelivery() {
        // given
        Order deliveringOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, DELIVERING);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(deliveringOrder));

        // when
        Order deliveredOrder = orderService.completeDelivery(UUID.randomUUID());

        // then
        assertThat(deliveredOrder.getStatus()).isEqualTo(DELIVERED);
    }

    @DisplayName("배달 시작 상태인 주문만 배달 완료 상태로 변경할 수 있다")
    @Test
    void startDeliveryNotDelivering() {
        // given
        Order invalidStatusOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, DELIVERED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(invalidStatusOrder));

        // when
        // then
        assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 완료 상태로 변경한다")
    @Test
    void complete() {
        // given
        Order deliveredOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, DELIVERED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(deliveredOrder));

        // when
        Order completedOrder = orderService.complete(UUID.randomUUID());

        // then
        assertThat(completedOrder.getStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("배달 주문이면 배달 완료 상태만 완료 상태로 변경할 수 있다")
    @Test
    void completeNotDelivered() {
        // given
        Order invalidStatusOrder = createOrderWithMenus(DELIVERY, DELIVERY_ADDRESS, null, 배달_주문_메뉴_목록, COMPLETED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(invalidStatusOrder));

        // when
        // then
        assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("포장과 매장 내 식사 주문이면 제공 상태만 완료 상태로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void completeNotDeliveryAndNotServed(OrderType orderType) {
        // given`
        Order invalidStatusOrder = createOrderWithMenus(orderType, null, null, 포장_주문_메뉴_목록, WAITING);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(invalidStatusOrder));

        // when
        // then
        assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 내 식사 주문을 완료 상태로 변경한다")
    @Test
    void completeEatIn() {
        // given
        OrderTable orderTable = createOrderTable("1번", 5, false);
        Order order = createOrderWithMenus(EAT_IN, null, orderTable, 포장_주문_메뉴_목록, SERVED);
        when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, COMPLETED)).thenReturn(false);

        // when
        Order completedOrder = orderService.complete(UUID.randomUUID());

        // then
        assertThat(completedOrder.getStatus()).isEqualTo(COMPLETED);
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isEmpty()).isTrue();
    }
}