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

    @DisplayName(value = "주문을 등록할 수 있다")
    @Test
    void create_success() throws Exception {
        //given
        BigDecimal 메뉴가격 = BigDecimal.valueOf(17000L);
        BigDecimal 주문구성메뉴가격 = BigDecimal.valueOf(17000L);
        OrderType 매장식사 = OrderType.EAT_IN;
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(매장식사);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(1L);
        given(주문구성메뉴.getPrice()).willReturn(주문구성메뉴가격);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(메뉴.isDisplayed()).willReturn(true);
        given(메뉴.getPrice()).willReturn(메뉴가격);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));
        OrderTable 주문테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(주문테이블));
        given(주문테이블.isEmpty()).willReturn(false);

        //when
        orderService.create(등록할_주문);

        //then
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @DisplayName(value = "주문을 등록할때 반드시 주문형태를 선택해야 한다")
    @Test
    void create_fail_should_contain_type() throws Exception {
        //given
        Order 등록할_주문 = mock(Order.class);

        //when,then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문은 반드시 하나 이상의 주문구성메뉴를 포함하고 있어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_주문구성메뉴_리스트")
    void create_fail_should_contain_orderLineItem(final List<OrderLineItem> 주문구성메뉴_리스트) throws Exception {
        //given
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(OrderType.DELIVERY);
        given(등록할_주문.getOrderLineItems()).willReturn(주문구성메뉴_리스트);

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문의 갯수와 주문구성메뉴의 갯수가 다를 수 없다")
    @Test
    void create_fail_menuList_size_should_same_orderLineItemList_size() throws Exception {
        //given
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(OrderType.DELIVERY);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴1 = mock(Menu.class);
        Menu 메뉴2 = mock(Menu.class);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴1, 메뉴2)));
        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "매장식사가 아닌경우, 주문구성메뉴의 갯수(quantity)는 0 이상이어야 한다")
    @Test
    void create_fail_orderLineItem_quantity_should_gt_0() throws Exception {
        //given
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(OrderType.DELIVERY);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(-1L);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "존재하는 메뉴만 주문할 수 있다")
    @Test
    void create_fail_menu_no_exist() throws Exception {
        //given
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(OrderType.DELIVERY);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(1L);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(메뉴.isDisplayed()).willReturn(true);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "판매중인 메뉴만 주문할 수 있다")
    @Test
    void create_fail_menu_should_display() throws Exception {
        //given
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(OrderType.DELIVERY);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(1L);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(메뉴.isDisplayed()).willReturn(false);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

       //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "메뉴의 가격과 주문구성메뉴의 가격이 다를 수 없다")
    @Test
    void create_fail_menu_price_should_same_orderLineItem_price() throws Exception {
        //given
        BigDecimal 메뉴가격 = BigDecimal.valueOf(17000L);
        BigDecimal 주문구성메뉴가격 = BigDecimal.valueOf(17500L);
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(OrderType.DELIVERY);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(1L);
        given(주문구성메뉴.getPrice()).willReturn(주문구성메뉴가격);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(메뉴.isDisplayed()).willReturn(true);
        given(메뉴.getPrice()).willReturn(메뉴가격);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문형태가 배달인 경우 반드시 주문주소를 포함하고 있어야 한다")
    @ParameterizedTest
    @MethodSource("잘못된_주문주소")
    void create_fail_when_type_delivery_should_contain_delivery_address(final String 주문주소) throws Exception {
        //given
        BigDecimal 메뉴가격 = BigDecimal.valueOf(17000L);
        BigDecimal 주문구성메뉴가격 = BigDecimal.valueOf(17000L);
        OrderType 배달 = OrderType.DELIVERY;
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(배달);
        given(등록할_주문.getDeliveryAddress()).willReturn(주문주소);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(1L);
        given(주문구성메뉴.getPrice()).willReturn(주문구성메뉴가격);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(메뉴.isDisplayed()).willReturn(true);
        given(메뉴.getPrice()).willReturn(메뉴가격);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName(value = "주문형태가 매장식사인 경우 반드시 주문 테이블을 포함하고 있어야 한다")
    @Test
    void create_fail_when_type_eat_in_should_contain_order_table() throws Exception {
        //given
        BigDecimal 메뉴가격 = BigDecimal.valueOf(17000L);
        BigDecimal 주문구성메뉴가격 = BigDecimal.valueOf(17000L);
        OrderType 매장식사 = OrderType.EAT_IN;
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(매장식사);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(1L);
        given(주문구성메뉴.getPrice()).willReturn(주문구성메뉴가격);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(메뉴.isDisplayed()).willReturn(true);
        given(메뉴.getPrice()).willReturn(메뉴가격);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName(value = "공석이 아닌 주문테이블에 주문을 등록할 수 없다")
    @Test
    void create_fail_table_should_empty() throws Exception {
        //given
        BigDecimal 메뉴가격 = BigDecimal.valueOf(17000L);
        BigDecimal 주문구성메뉴가격 = BigDecimal.valueOf(17000L);
        OrderType 매장식사 = OrderType.EAT_IN;
        Order 등록할_주문 = mock(Order.class);
        given(등록할_주문.getType()).willReturn(매장식사);
        OrderLineItem 주문구성메뉴 = mock(OrderLineItem.class);
        given(주문구성메뉴.getQuantity()).willReturn(1L);
        given(주문구성메뉴.getPrice()).willReturn(주문구성메뉴가격);
        given(등록할_주문.getOrderLineItems()).willReturn(new ArrayList<>(Arrays.asList(주문구성메뉴)));
        Menu 메뉴 = mock(Menu.class);
        given(메뉴.isDisplayed()).willReturn(true);
        given(메뉴.getPrice()).willReturn(메뉴가격);
        given(menuRepository.findAllByIdIn(any())).willReturn(new ArrayList<>(Arrays.asList(메뉴)));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));
        OrderTable 주문테이블 = mock(OrderTable.class);
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(주문테이블));
        given(주문테이블.isEmpty()).willReturn(true);

        //when, then
        assertThatThrownBy(() -> orderService.create(등록할_주문))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 주문수락으로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING"})
    void accept_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);

        //when
        orderService.accept(UUID.randomUUID());

        //then
        verify(배달요청, times(0)).requestDelivery(any(),any(),any());
        verify(주문,times(1)).setStatus(OrderStatus.ACCEPTED);
    }

    @DisplayName(value = "주문수락으로 변경 후 주문형태가 배달인 경우 배달 라이더를 요청한다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING"})
    void accept_success_call_rider(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);
        given(주문.getType()).willReturn(OrderType.DELIVERY);

        //when
        orderService.accept(UUID.randomUUID());

        //then
        verify(배달요청, times(1)).requestDelivery(any(),any(),any());
        verify(주문,times(1)).setStatus(OrderStatus.ACCEPTED);
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
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);

        //when, then
        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 서빙완료로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED"})
    void serve_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);

        //when
        orderService.serve(UUID.randomUUID());

        //then
        verify(주문,times(1)).setStatus(OrderStatus.SERVED);
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
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);

        //when, then
        assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 배달중으로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"SERVED"})
    void startDelivery_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);
        given(주문.getType()).willReturn(OrderType.DELIVERY);

        //when
        orderService.startDelivery(UUID.randomUUID());

        //then
        verify(주문,times(1)).setStatus(OrderStatus.DELIVERING);
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
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING" , "DELIVERED", "COMPLETED"})
    void startDelivery_status_should_serve(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);
        given(주문.getType()).willReturn(OrderType.DELIVERY);

        //when, then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문형태가 배달인 경우만 배달중으로 변경한다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void startDelivery_type_should_delivery(final OrderType 주문형태) throws Exception {
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getType()).willReturn(주문형태);

        //when, then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 배달완료로 변경할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"DELIVERING"})
    void completeDelivery_success(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);

        //when
        orderService.completeDelivery(UUID.randomUUID());

        //then
        verify(주문,times(1)).setStatus(OrderStatus.DELIVERED);
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
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED" , "DELIVERED", "COMPLETED"})
    void completeDelivery_status_should_startDelivery(final OrderStatus 주문상태) throws Exception {
        //given
        Order 주문 = mock(Order.class);
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(주문));
        given(주문.getStatus()).willReturn(주문상태);

        //when, then
        assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName(value = "주문상태를 주문종결로 변경할 수 있다")
    @Test
    void complete_success() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문형태가 배달인 경우, 주문상태가 배달중인 경우만 주문종결로 변경한다")
    @Test
    void complete_when_type_delivery_status_should_delivering() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "주문형태가 매장식사 또는 테이크아웃인경우, 주문상태가 서빙완료인경우만 주문종결로 변경한다")
    @Test
    void complete_when_type_eat_in_or_take_out_status_should_serve() throws Exception {
        //given

        //when

        //then
    }

    @DisplayName(value = "전체 주문을 조회할 수 있다")
    @Test
    void findAll_success() throws Exception {
        //given

        //when

        //then
    }

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
}