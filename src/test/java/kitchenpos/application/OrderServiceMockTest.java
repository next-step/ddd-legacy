package kitchenpos.application;

import kitchenpos.domain.*;
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
import java.util.*;

import static kitchenpos.fixture.MenuFixture.메뉴_생성;
import static kitchenpos.fixture.OrderFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceMockTest {
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

    @DisplayName("주문 요청 시, 주문 형태를 선택하지 않으면 주문을 실패한다")
    @Test
    void create_type_exception() {
        //given
        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(new Order()));
    }

    @DisplayName("주문 요청 시, 주문 상품을 선택하지 않으면 주문을 실패한다")
    @Test
    void create_orderLineItem_exception() {
        //given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 요청 시, 선택한 주문 상품 중 메뉴에 존재하지 않으면 주문을 실패한다")
    @Test
    void create_menu_exception() {
        //given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        Order order = 배달_주문_생성("가짜주소", List.of(orderLineItem));

        //when
        Menu fakeMenu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu, fakeMenu));

        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 요청 시, 선택한 주문 상품의 메뉴 노출여부가 숨김 상태면 주문을 실패한다")
    @Test
    void create_menu_displayed_exception() {
        //given
        Menu menu = 메뉴_생성(UUID.randomUUID(), false, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        Order order = 배달_주문_생성("가짜주소", List.of(orderLineItem));
        List<Menu> menus = order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        //when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }

        //then
        assertThatIllegalStateException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 요청 시, 선택한 주문 상품의 가격과 메뉴의 가격이 일치하지 않으면 주문을 실패한다")
    @Test
    void create_menu_price_exception() {
        //given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, BigDecimal.valueOf(50_000), 1);
        Order order = 배달_주문_생성("가짜주소", List.of(orderLineItem));
        List<Menu> menus = order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        //when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }

        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("DELIVERY 주문을 생성한다")
    @Test
    void create_delivery() {
        // given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        Order deliveryOrder = 배달_주문_생성("가짜주소", List.of(orderLineItem));
        List<Menu> menus = deliveryOrder.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }
        when(orderRepository.save(any())).thenReturn(deliveryOrder);

        Order order = orderService.create(deliveryOrder);

        // then
        assertThat(order.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        then(menuRepository).should(times(1)).findAllByIdIn(any());
        then(menuRepository).should(times(menus.size())).findById(any());
        then(orderRepository).should(times(1)).save(any());
    }

    @DisplayName("DELIVERY 주문 요청 시, 선택한 주문 상품의 수량이 0 미만이면 주문을 실패한다")
    @Test
    void create_delivery_quantity_exception() {
        //given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, BigDecimal.valueOf(10_000), -1);
        Order order = 배달_주문_생성("가짜주소", List.of(orderLineItem));
        List<Menu> menus = order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        //when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);

        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("DELIVERY 주문 요청 시, 배달 주소를 입력하지 않으면 주문을 실패한다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_delivery_address_exception(String address) {
        //given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        Order deliveryOrder = 배달_주문_생성(address, List.of(orderLineItem));
        List<Menu> menus = deliveryOrder.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }

        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(deliveryOrder));
    }

    @DisplayName("EAT_IN 주문을 생성한다")
    @Test
    void create_eat_in() {
        // given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        OrderTable orderTable = 주문_테이블_생성(UUID.randomUUID(), "테이블A", 1, true);
        Order eatInOrder = 매장_주문_생성(orderTable, List.of(orderLineItem));
        List<Menu> menus = eatInOrder.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.save(any())).thenReturn(eatInOrder);

        Order order = orderService.create(eatInOrder);

        // then
        assertThat(order.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        then(menuRepository).should(times(1)).findAllByIdIn(any());
        then(menuRepository).should(times(menus.size())).findById(any());
        then(orderTableRepository).should(times(1)).findById(any());
        then(orderRepository).should(times(1)).save(any());
    }

    @DisplayName("EAT_IN 주문 요청 시, 주문 테이블이 존재하지 않으면 주문을 실패한다")
    @Test
    void create_eat_in_orderTable_exception() {
        // given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        OrderTable orderTable = 주문_테이블_생성(UUID.randomUUID(), "테이블A", 1, true);
        Order eatInOrder = 매장_주문_생성(orderTable, List.of(orderLineItem));
        List<Menu> menus = eatInOrder.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> orderService.create(eatInOrder))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("EAT_IN 주문 요청 시, 주문 테이블이 미사용중 상태면 주문을 실패한다")
    @Test
    void create_eat_in_orderTable_occupied_exception() {
        // given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        OrderTable orderTable = 주문_테이블_생성(UUID.randomUUID(), "테이블A", 0, false);
        Order eatInOrder = 매장_주문_생성(orderTable, List.of(orderLineItem));
        List<Menu> menus = eatInOrder.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        //then
        assertThatIllegalStateException().isThrownBy(() -> orderService.create(eatInOrder));
    }

    @DisplayName("TAKEOUT 주문을 생성한다")
    @Test
    void create_takeout() {
        // given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        Order takeoutOrder = 포장_주문_생성(List.of(orderLineItem));
        List<Menu> menus = takeoutOrder.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);
        for (Menu m : menus) {
            when(menuRepository.findById(m.getId())).thenReturn(Optional.of(m));
        }
        when(orderRepository.save(any())).thenReturn(takeoutOrder);

        Order order = orderService.create(takeoutOrder);

        // then
        assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        then(menuRepository).should(times(1)).findAllByIdIn(any());
        then(menuRepository).should(times(menus.size())).findById(any());
        then(orderRepository).should(times(1)).save(any());
    }

    @DisplayName("TAKEOUT 주문 요청 시, 선택한 주문 상품의 수량이 0 미만이면 주문을 실패한다")
    @Test
    void create_takeout_quantity_exception() {
        //given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), -1);
        Order order = 포장_주문_생성(List.of(orderLineItem));
        List<Menu> menus = order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .toList();

        //when
        when(menuRepository.findAllByIdIn(any())).thenReturn(menus);

        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문을 수락한다")
    @EnumSource(value = OrderType.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"DELIVERY"})
    @ParameterizedTest(name = "{0} {displayName}")
    void accept(OrderType orderType) {
        // given
        Order order = new Order();
        order.setType(orderType);
        order.setStatus(OrderStatus.WAITING);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        Order changedOrder = orderService.accept(UUID.randomUUID());

        // then
        assertThat(changedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("DELIVERY 주문을 수락한다")
    @Test
    void accept() {
        // given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        Order order = 배달_주문_생성("가짜주소", List.of(orderLineItem));
        order.setStatus(OrderStatus.WAITING);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        doNothing().when(kitchenridersClient).requestDelivery(any(), any(), any());
        Order changedOrder = orderService.accept(UUID.randomUUID());

        // then
        assertThat(changedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        then(orderRepository).should(times(1)).findById(any());
        then(kitchenridersClient).should(times(1)).requestDelivery(any(), any(), any());
    }

    @DisplayName("주문 상태를 ACCEPTED로 변경 시, 존재하지 않는 주문이면 상태 변경을 실패한다")
    @Test
    void accept_order_exception() {
        // when
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 상태를 ACCEPTED로 변경 시, 주문 상태가 WAITING이 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"WAITING"})
    void accept_order_status_exception(OrderStatus status) {
        // given
        Order order = new Order();
        order.setStatus(status);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.accept(UUID.randomUUID()));
    }

    @DisplayName("주문 상태를 SERVED로 변경한다")
    @Test
    void serve() {
        // given
        Order order = new Order();
        order.setStatus(OrderStatus.ACCEPTED);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        Order changedOrder = orderService.serve(UUID.randomUUID());

        // then
        assertThat(changedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 상태를 SERVED로 변경 시, 존재하지 않는 주문이면 상태 변경을 실패한다")
    @Test
    void serve_order_exception() {
        // when
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 상태를 SERVED로 변경 시, 주문 상태가 ACCEPTED가 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"ACCEPTED"})
    void serve_order_status_exception(OrderStatus status) {
        // given
        Order order = new Order();
        order.setStatus(status);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.serve(UUID.randomUUID()));
    }

    @DisplayName("주문 상태를 DELIVERING로 변경한다")
    @Test
    void startDelivery() {
        // given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        Order changedOrder = orderService.startDelivery(UUID.randomUUID());

        // then
        assertThat(changedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 상태를 DELIVERING로 변경 시, 존재하지 않는 주문이면 상태 변경을 실패한다")
    @Test
    void startDelivery_order_exception() {
        // when
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 상태를 DELIVERING로 변경 시, 주문 형태가 DELIVERY가 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"DELIVERY"})
    void startDelivery_order_type_exception(OrderType type) {
        // given
        Order order = new Order();
        order.setType(type);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.startDelivery(UUID.randomUUID()));
    }

    @DisplayName("주문 상태를 DELIVERING로 변경 시, 주문 상태가 SERVED가 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"SERVED"})
    void startDelivery_order_status_exception(OrderStatus status) {
        // given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(status);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.startDelivery(UUID.randomUUID()));
    }

    @DisplayName("주문 상태를 DELIVERED로 변경한다")
    @Test
    void completeDelivery() {
        // given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        Order changedOrder = orderService.completeDelivery(UUID.randomUUID());

        // then
        assertThat(changedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 상태를 DELIVERED로 변경 시, 존재하지 않는 주문이면 상태 변경을 실패한다")
    @Test
    void completeDelivery_order_exception() {
        // when
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 상태를 DELIVERED로 변경 시, 주문 상태가 DELIVERING이 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"DELIVERING"})
    void completeDelivery_order_status_exception(OrderStatus status) {
        // given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(status);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()));
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 상태를 DELIVERED로 변경 시, 주문 형태가 DELIVERY가 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"DELIVERY"})
    void completeDelivery_order_type_exception(OrderType type) {
        // given
        Order order = new Order();
        order.setType(type);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()));
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 상태를 COMPLETED 변경 시, 존재하지 않는 주문이면 상태 변경을 실패한다")
    @Test
    void complete_order_exception() {
        // when
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("DELIVERY 주문의 상태를 COMPLETED로 변경한다")
    @Test
    void complete_delivery() {
        // given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERED);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        Order completedOrder = orderService.complete(UUID.randomUUID());

        // then
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("DELIVERY 주문의 상태를 COMPLETED로 변경 시, 주문 상태가 DELIVERED가 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"DELIVERED"})
    void complete_delivery_order_status_exception(OrderStatus status) {
        // given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(status);
        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(UUID.randomUUID()));
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("EAT_IN 주문의 상태를 COMPLETED로 변경한다")
    @Test
    void complete_eat_in() {
        // given
        Menu menu = 메뉴_생성(UUID.randomUUID(), true, BigDecimal.valueOf(10_000));
        OrderLineItem orderLineItem = 주문_상품_생성(menu, menu.getPrice(), 1);
        OrderTable orderTable = 주문_테이블_생성(UUID.randomUUID(), "테이블A", 1, true);
        Order order = 매장_주문_생성(orderTable, List.of(orderLineItem));
        order.setStatus(OrderStatus.SERVED);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);
        Order completedOrder = orderService.complete(UUID.randomUUID());

        // then
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        OrderTable clearedOrderTable = completedOrder.getOrderTable();
        assertThat(clearedOrderTable.getNumberOfGuests()).isZero();
        assertThat(clearedOrderTable.isOccupied()).isFalse();
        then(orderRepository).should(times(1)).findById(any());
        then(orderRepository).should(times(1)).existsByOrderTableAndStatusNot(any(), any());
    }

    @DisplayName("EAT_IN 주문의 상태를 COMPLETED로 변경 시, 주문 상태가 SERVED가 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"SERVED"})
    void complete_eat_in_order_status_exception(OrderStatus status) {
        // given
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setStatus(status);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(UUID.randomUUID()));
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("TAKEOUT 주문의 상태를 COMPLETED로 변경한다")
    @Test
    void complete_takeout() {
        // given
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setStatus(OrderStatus.SERVED);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        Order completedOrder = orderService.complete(UUID.randomUUID());

        // then
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("TAKEOUT 주문의 상태를 COMPLETED로 변경 시, 주문 상태가 SERVED가 아니면 상태 변경을 실패한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class,
            mode = EnumSource.Mode.EXCLUDE,
            names = {"SERVED"})
    void complete_takeout_order_status_exception(OrderStatus status) {
        // given
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setStatus(status);

        // when
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        // then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(UUID.randomUUID()));
        then(orderRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 목록을 조회한다")
    @Test
    void findAll() {
        // given
        // when
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        List<Order> orders = orderService.findAll();
        // then
        assertThat(orders).hasSize(0);
    }
}
