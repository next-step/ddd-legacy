package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static kitchenpos.fixture.MenuFixture.MenuBuilder;
import static kitchenpos.fixture.OrderFixture.OrderBuilder;
import static kitchenpos.fixture.OrderLineItemFixture.OrderLineItemBuilder;
import static kitchenpos.fixture.OrderTableFixture.OrderTableBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    OrderService orderService;
    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient 배달요청;

    private static Stream<List<OrderLineItem>> 잘못된_주문구성메뉴_리스트() {
        return Stream.of(
                null,
                new ArrayList<>()
        );
    }

    private static Stream<String> 잘못된_주문주소() {
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName(value = "주문을 등록할 수 있다")
    @Test
    void create_success() throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().price(BigDecimal.valueOf(17000L)).quantity(1L).build();
        Order 등록할주문 = new OrderBuilder().type(OrderType.EAT_IN).orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).build();
        Menu 메뉴 = new MenuBuilder().displayed(true).price(BigDecimal.valueOf(17000L)).build();
        OrderTable 주문테이블 = new OrderTableBuilder().empty(false).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(주문테이블));

        //when
        orderService.create(등록할주문);

        //then
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @DisplayName(value = "주문을 등록할때 반드시 주문형태를 선택해야 한다")
    @Test
    void create_fail_should_contain_type() throws Exception {
        //given
        Order 등록할주문 = new OrderBuilder().build();

        //when,then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문은 반드시 하나 이상의 주문구성메뉴를 포함하고 있어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_주문구성메뉴_리스트")
    void create_fail_should_contain_orderLineItem(final List<OrderLineItem> 주문구성메뉴리스트) throws Exception {
        //given
        Order 등록할주문 = new OrderBuilder().type(OrderType.DELIVERY).orderLineItems(주문구성메뉴리스트).build();

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문의 갯수와 주문구성메뉴의 갯수가 다를 수 없다")
    @Test
    void create_fail_menuList_size_should_same_orderLineItemList_size() throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().build();
        Order 등록할주문 = new OrderBuilder().type(OrderType.DELIVERY).orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(mock(Menu.class), mock(Menu.class))));

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "매장식사가 아닌경우, 주문구성메뉴의 갯수(quantity)는 0 이상이어야 한다")
    @Test
    void create_fail_orderLineItem_quantity_should_gt_0() throws Exception {
        //given
        Menu 메뉴 = new MenuBuilder().build();
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().quantity(-10).menu(메뉴).menuId(메뉴.getId()).build();
        Order 등록할주문 = new OrderBuilder().type(OrderType.DELIVERY).orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "존재하는 메뉴만 주문할 수 있다")
    @Test
    void create_fail_menu_no_exist() throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().quantity(1L).build();
        Order 등록할주문 = new OrderBuilder().orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).type(OrderType.DELIVERY).build();
        Menu 메뉴 = new MenuBuilder().build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(주문구성메뉴.getMenuId())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "판매중인 메뉴만 주문할 수 있다")
    @Test
    void create_fail_menu_should_display() throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().quantity(1L).build();
        Order 등록할주문 = new OrderBuilder().orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).type(OrderType.DELIVERY).build();
        Menu 메뉴 = new MenuBuilder().displayed(false).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "메뉴의 가격과 주문구성메뉴의 가격이 다를 수 없다")
    @Test
    void create_fail_menu_price_should_same_orderLineItem_price() throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().price(BigDecimal.valueOf(17500L)).quantity(1L).build();
        Order 등록할주문 = new OrderBuilder().orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).type(OrderType.DELIVERY).build();
        Menu 메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000L)).displayed(true).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문형태가 배달인 경우 반드시 주문주소를 포함하고 있어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_주문주소")
    void create_fail_when_type_delivery_should_contain_delivery_address(final String 주문주소) throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().price(BigDecimal.valueOf(17000L)).quantity(1L).build();
        Order 등록할주문 = new OrderBuilder().deliveryAddress(주문주소).orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).type(OrderType.DELIVERY).build();
        Menu 메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000L)).displayed(true).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문형태가 매장식사인 경우 반드시 주문 테이블을 포함하고 있어야 한다")
    @Test
    void create_fail_when_type_eat_in_should_contain_order_table() throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().price(BigDecimal.valueOf(17000L)).quantity(1L).build();
        Order 등록할주문 = new OrderBuilder().orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).type(OrderType.EAT_IN).build();
        Menu 메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000L)).displayed(true).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "공석인 주문테이블에 주문을 등록할 수 없다")
    @Test
    void create_fail_table_should_empty() throws Exception {
        //given
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().price(BigDecimal.valueOf(17000L)).quantity(1L).build();
        Menu 메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000L)).displayed(true).build();
        OrderTable 주문테이블 = new OrderTableBuilder().empty(true).build();
        Order 등록할주문 = new OrderBuilder().orderTable(주문테이블).orderTableId(주문테이블.getId()).orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).type(OrderType.EAT_IN).build();

        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));
        given(orderTableRepository.findById(주문테이블.getId())).willReturn(Optional.of(주문테이블));

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할주문))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 주문수락으로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING"})
    void accept_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).build();

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));

        //when
        orderService.accept(UUID.randomUUID());

        //then
        verify(배달요청, times(0)).requestDelivery(any(), any(), any());
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName(value = "주문수락으로 변경 후 주문형태가 배달인 경우 배달 라이더를 요청한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING"})
    void accept_success_call_rider(final OrderStatus 주문상태) throws Exception {
        //given
        Menu 메뉴 = new MenuBuilder().price(BigDecimal.valueOf(17000L)).displayed(true).build();
        OrderLineItem 주문구성메뉴 = new OrderLineItemBuilder().price(BigDecimal.valueOf(17000L)).menu(메뉴).quantity(1L).build();
        Order 주문 = new OrderBuilder().status(주문상태).orderLineItems(new ArrayList<>(Arrays.asList(주문구성메뉴))).type(OrderType.DELIVERY).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when
        orderService.accept(주문.getId());

        //then
        verify(배달요청, times(1)).requestDelivery(any(), any(), any());
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName(value = "존재하는 주문만 주문수락으로 변경할 수 있다")
    @Test
    void accept_no_exist_order() throws Exception {
        //given
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "주문상태가 수락대기인 경우만 주문수락으로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void accept_status_should_waiting(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).type(OrderType.DELIVERY).build();

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));

        //when, then
        assertThatThrownBy(() -> orderService.accept(주문.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 서빙완료로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED"})
    void serve_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when
        orderService.serve(주문.getId());

        //then
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName(value = "존재하는 주문만 서빙완료로 변경할 수 있다")
    @Test
    void serve_no_exist_order() throws Exception {
        //given
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "주문상태가 주문수락인 경우만 서빙완료로 변경 한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void serve_status_should_accept(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when, then
        assertThatThrownBy(() -> orderService.serve(주문.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 배달중으로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"SERVED"})
    void startDelivery_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).type(OrderType.DELIVERY).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when
        orderService.startDelivery(주문.getId());

        //then
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName(value = "존재하는 주문만 서빙완료로 변경할 수 있다")
    @Test
    void startDelivery_no_exist_order() throws Exception {
        //given
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "주문상태가 서빙완료인 경우만 배달중으로 변경한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void startDelivery_status_should_serve(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).type(OrderType.DELIVERY).build();

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));

        //when, then
        assertThatThrownBy(() -> orderService.startDelivery(주문.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문형태가 배달인 경우만 배달중으로 변경한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void startDelivery_type_should_delivery(final OrderType 주문형태) throws Exception {
        Order 주문 = new OrderBuilder().type(주문형태).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when, then
        assertThatThrownBy(() -> orderService.startDelivery(주문.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 배달완료로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"DELIVERING"})
    void completeDelivery_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).type(OrderType.DELIVERY).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when
        orderService.completeDelivery(주문.getId());

        //then
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName(value = "존재하는 주문만 배달완료로 변경할 수 있다")
    @Test
    void completeDelivery_order_no_exist() throws Exception {
        //given
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "주문상태가 배달중인 경우만 배달완료로 변경한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    void completeDelivery_status_should_startDelivery(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when, then
        assertThatThrownBy(() -> orderService.completeDelivery(주문.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 주문종결로 변경할 수 있다 - 배달")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"DELIVERED"})
    void complete_success_delivery(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).type(OrderType.DELIVERY).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when
        orderService.complete(주문.getId());

        //then
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName(value = "주문상태를 주문종결로 변경할 수 있다 - 매장식사")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"SERVED"})
    void complete_success_eat_in(final OrderStatus 주문상태) throws Exception {
        //given
        OrderTable 주문테이블 = new OrderTableBuilder().build();
        Order 주문 = new OrderBuilder().status(주문상태).orderTable(주문테이블).type(OrderType.EAT_IN).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));
        given(orderRepository.existsByOrderTableAndStatusNot(주문테이블, OrderStatus.COMPLETED)).willReturn(false);

        //when
        orderService.complete(주문.getId());

        //then
        assertThat(주문테이블.getNumberOfGuests()).isZero();
        assertThat(주문테이블.isEmpty()).isTrue();
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName(value = "주문상태를 주문종결로 변경할 수 있다 - 테이크아웃")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"SERVED"})
    void complete_success_take_out(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).type(OrderType.TAKEOUT).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when
        orderService.complete(주문.getId());

        //then
        assertThat(주문.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName(value = "주문형태가 배달인 경우, 주문상태가 배달완료인 경우만 주문종결로 변경한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
    void complete_when_type_delivery_status_should_delivering(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = new OrderBuilder().status(주문상태).type(OrderType.DELIVERY).build();

        given(orderRepository.findById(주문.getId())).willReturn(Optional.of(주문));

        //when, then
        assertThatThrownBy(() -> orderService.complete(주문.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문형태가 매장식사 또는 테이크아웃인경우, 주문상태가 서빙완료인경우만 주문종결로 변경한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void complete_when_type_eat_in_or_take_out_status_should_serve(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문1 = new OrderBuilder().status(주문상태).type(OrderType.TAKEOUT).build();
        Order 주문2 = new OrderBuilder().status(주문상태).type(OrderType.EAT_IN).build();

        given(orderRepository.findById(주문1.getId())).willReturn(Optional.of(주문1));
        given(orderRepository.findById(주문2.getId())).willReturn(Optional.of(주문2));

        //when, then
        assertThatThrownBy(() -> orderService.complete(주문1.getId()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> orderService.complete(주문2.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "전체 주문을 조회할 수 있다")
    @Test
    void findAll_success() throws Exception {
        //given, when
        orderService.findAll();

        //verify
        verify(orderRepository, times(1)).findAll();
    }
}
