package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

    @DisplayName("주문 등록(wating) - 주문은 반드시 타입(매장식사, 배달, 테이크아웃)을 선택해야 한다.")
    @Test
    void create01() {
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(null);
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<List<OrderLineItem>> provideOrderLineItemsForNullAndEmpty() {
        return Stream.of(
                null,
                Collections.emptyList()
        );
    }

    @DisplayName("주문 등록(wating) - 주문은 반드시 하나 이상의 메뉴(menu)를 포함해야 한다.")
    @MethodSource("provideOrderLineItemsForNullAndEmpty")
    @ParameterizedTest
    void create02(List<OrderLineItem> 주문_등록_요청_메뉴들) {
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(OrderType.DELIVERY);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 등록(wating) - 존재하는 메뉴만 선택할 수 있다.")
    @Test
    void create03() {
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(OrderType.DELIVERY);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = mock(List.class);
        int 주문_등록_요청_메뉴_수 = 2;
        when(주문_등록_요청_메뉴들.size()).thenReturn(주문_등록_요청_메뉴_수);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);

        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);

        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }


    private static Stream<OrderType> provideOrderTypeForDeliveryAndTakeout() {
        return Stream.of(
                OrderType.DELIVERY,
                OrderType.TAKEOUT
        );
    }

    //@TODO 매장식사는 음수로 주문이 가능함. 의도한 동작인지 확인후 개선
    @DisplayName("주문 등록(wating) - 메뉴 품목별 수량은 매장 식사가 아닌 경우 0보다 큰 값을 가져야 한다.")
    @MethodSource("provideOrderTypeForDeliveryAndTakeout")
    @ParameterizedTest
    void create04(OrderType 주문_등록_요청_타입) {
        //given
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(주문_등록_요청_타입);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = spy(ArrayList.class);
        OrderLineItem 주문_등록_요청_메뉴 = mock(OrderLineItem.class);
        given(주문_등록_요청_메뉴.getQuantity()).willReturn(-1l);
        주문_등록_요청_메뉴들.add(주문_등록_요청_메뉴);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);
        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);
        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 등록(wating) - 메뉴 품목별 수량은 매장 식사가 아닌 경우 0보다 큰 값을 가져야 한다.")
    @MethodSource("provideOrderTypeForDeliveryAndTakeout")
    @ParameterizedTest
    void create05(OrderType 주문_등록_요청_타입) {
        //given
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(주문_등록_요청_타입);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = spy(ArrayList.class);
        OrderLineItem 주문_등록_요청_메뉴 = mock(OrderLineItem.class);
        given(주문_등록_요청_메뉴.getQuantity()).willReturn(-1L);
        주문_등록_요청_메뉴들.add(주문_등록_요청_메뉴);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);
        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);
        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 등록(wating) - 진열된 메뉴만 선택할 수 있다")
    @Test
    void create06() {
        //given
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(OrderType.EAT_IN);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = spy(ArrayList.class);
        OrderLineItem 주문_등록_요청_메뉴 = mock(OrderLineItem.class);
        UUID 주문_등록_요청_메뉴_아이디 = UUID.randomUUID();
        given(주문_등록_요청_메뉴.getMenuId()).willReturn(주문_등록_요청_메뉴_아이디);
        주문_등록_요청_메뉴들.add(주문_등록_요청_메뉴);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);

        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);
        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        Menu 조회된_메뉴 = mock(Menu.class);
        given(조회된_메뉴.isDisplayed()).willReturn(false);
        given(menuRepository.findById(주문_등록_요청_메뉴_아이디)).willReturn(Optional.of(조회된_메뉴));
        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 등록(wating) - 주문 가격(진열된 메뉴의 가격과)과 메뉴의 가격은 다를 수 없다.")
    @Test
    void create07() {
        //given
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(OrderType.EAT_IN);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = spy(ArrayList.class);
        OrderLineItem 주문_등록_요청_메뉴 = mock(OrderLineItem.class);
        UUID 주문_등록_요청_메뉴_아이디 = UUID.randomUUID();
        given(주문_등록_요청_메뉴.getMenuId()).willReturn(주문_등록_요청_메뉴_아이디);
        BigDecimal 주문_요청_메뉴_가격 = BigDecimal.valueOf(1500);
        given(주문_등록_요청_메뉴.getPrice()).willReturn(주문_요청_메뉴_가격);
        주문_등록_요청_메뉴들.add(주문_등록_요청_메뉴);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);

        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);
        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        Menu 조회된_메뉴 = mock(Menu.class);
        given(조회된_메뉴.isDisplayed()).willReturn(true);
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(2000L);
        given(조회된_메뉴.getPrice()).willReturn(조회된_메뉴_가격);
        given(menuRepository.findById(주문_등록_요청_메뉴_아이디)).willReturn(Optional.of(조회된_메뉴));
        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<String> provideDeliveryAddressForNullAndEmptyString() {
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName("주문 등록(wating) - 배달 주문의 경우 반드시 배달 주소를 가져야 한다.")
    @MethodSource("provideDeliveryAddressForNullAndEmptyString")
    @ParameterizedTest
    void create08(String 배송_받을_주소) {
        //given
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(OrderType.DELIVERY);
        given(주문_등록_요청.getDeliveryAddress()).willReturn(배송_받을_주소);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = spy(ArrayList.class);
        OrderLineItem 주문_등록_요청_메뉴 = mock(OrderLineItem.class);
        UUID 주문_등록_요청_메뉴_아이디 = UUID.randomUUID();
        given(주문_등록_요청_메뉴.getMenuId()).willReturn(주문_등록_요청_메뉴_아이디);
        BigDecimal 주문_요청_메뉴_가격 = BigDecimal.valueOf(1500);
        given(주문_등록_요청_메뉴.getPrice()).willReturn(주문_요청_메뉴_가격);
        주문_등록_요청_메뉴들.add(주문_등록_요청_메뉴);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);

        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);
        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        Menu 조회된_메뉴 = mock(Menu.class);
        given(조회된_메뉴.isDisplayed()).willReturn(true);
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(1500L);
        given(조회된_메뉴.getPrice()).willReturn(조회된_메뉴_가격);
        given(menuRepository.findById(주문_등록_요청_메뉴_아이디)).willReturn(Optional.of(조회된_메뉴));

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<Arguments> provideOrderTableForNullAndNonEmpty() {
        OrderTable 조회된_테이블_비어있지_않음 = mock(OrderTable.class);
        given(조회된_테이블_비어있지_않음.isEmpty()).willReturn(true);
        return Stream.of(
                Arguments.of(Optional.empty(), NoSuchElementException.class),
                Arguments.of(Optional.of(조회된_테이블_비어있지_않음), IllegalStateException.class)
        );
    }

    @DisplayName("주문 등록(wating) - 매장 식사의 경우 반드시 착석한 테이블을 선택해야 한다.")
    @MethodSource("provideOrderTableForNullAndNonEmpty")
    @ParameterizedTest
    void create09(Optional<OrderTable> 조회된_테이블, Class<RuntimeException> expected) {
        //given
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(OrderType.EAT_IN);
        UUID 주문_등록_요청_테이블_아이디 = UUID.randomUUID();
        given(주문_등록_요청.getOrderTableId()).willReturn(주문_등록_요청_테이블_아이디);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = spy(ArrayList.class);
        OrderLineItem 주문_등록_요청_메뉴 = mock(OrderLineItem.class);
        UUID 주문_등록_요청_메뉴_아이디 = UUID.randomUUID();
        given(주문_등록_요청_메뉴.getMenuId()).willReturn(주문_등록_요청_메뉴_아이디);
        BigDecimal 주문_요청_메뉴_가격 = BigDecimal.valueOf(1500);
        given(주문_등록_요청_메뉴.getPrice()).willReturn(주문_요청_메뉴_가격);
        주문_등록_요청_메뉴들.add(주문_등록_요청_메뉴);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);

        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);
        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        Menu 조회된_메뉴 = mock(Menu.class);
        given(조회된_메뉴.isDisplayed()).willReturn(true);
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(1500L);
        given(조회된_메뉴.getPrice()).willReturn(조회된_메뉴_가격);
        given(menuRepository.findById(주문_등록_요청_메뉴_아이디)).willReturn(Optional.of(조회된_메뉴));

        given(orderTableRepository.findById(주문_등록_요청_테이블_아이디)).willReturn(조회된_테이블);

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(expected);
    }

    @DisplayName("주문 등록(wating) - 주문을 등록할 수 있다.")
    @Test
    void create10() {
        //given
        Order 주문_등록_요청 = mock(Order.class);
        given(주문_등록_요청.getType()).willReturn(OrderType.TAKEOUT);
        List<OrderLineItem> 주문_등록_요청_메뉴들 = spy(ArrayList.class);
        OrderLineItem 주문_등록_요청_메뉴 = mock(OrderLineItem.class);
        UUID 주문_등록_요청_메뉴_아이디 = UUID.randomUUID();
        given(주문_등록_요청_메뉴.getMenuId()).willReturn(주문_등록_요청_메뉴_아이디);
        BigDecimal 주문_요청_메뉴_가격 = BigDecimal.valueOf(1500);
        given(주문_등록_요청_메뉴.getPrice()).willReturn(주문_요청_메뉴_가격);
        주문_등록_요청_메뉴들.add(주문_등록_요청_메뉴);
        given(주문_등록_요청.getOrderLineItems()).willReturn(주문_등록_요청_메뉴들);

        List<OrderLineItem> 조회된_메뉴들 = mock(List.class);
        int 조회된_메뉴_수 = 1;
        when(조회된_메뉴들.size()).thenReturn(조회된_메뉴_수);
        given(menuRepository.findAllByIdIn(any(List.class))).willReturn(조회된_메뉴들);

        Menu 조회된_메뉴 = mock(Menu.class);
        given(조회된_메뉴.isDisplayed()).willReturn(true);
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(1500L);
        given(조회된_메뉴.getPrice()).willReturn(조회된_메뉴_가격);
        given(menuRepository.findById(주문_등록_요청_메뉴_아이디)).willReturn(Optional.of(조회된_메뉴));
        //when
        orderService.create(주문_등록_요청);
        //then
        verify(orderRepository).save(any(Order.class));
    }


    private static Stream<OrderStatus> provideOrderStatusExceptForWaiting() {
        return Stream.of(
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.SERVED
        );
    }

    @DisplayName("주문 승인(accept) - 대기중(waiting)인 주문만 승인할 수 있다.")
    @MethodSource("provideOrderStatusExceptForWaiting")
    @ParameterizedTest
    void accept01(OrderStatus 조회된_주문_상태) {
        //given
        UUID 승인할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getStatus()).willReturn(조회된_주문_상태);
        given(orderRepository.findById(승인할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        assertThatThrownBy(() -> orderService.accept(승인할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 승인(accept) -  배달주문의 경우 라이더에게 배달을 요청해야 한다.")
    @Test
    void accept02() {
        //given
        UUID 승인할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.WAITING);
        given(조회된_주문.getType()).willReturn(OrderType.DELIVERY);
        given(조회된_주문.getDeliveryAddress()).willReturn("우리집으로 빨리 와줘");
        given(orderRepository.findById(승인할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        orderService.accept(승인할_주문_아이디);

        //then
        verify(kitchenridersClient).requestDelivery(승인할_주문_아이디, BigDecimal.ZERO, "우리집으로 빨리 와줘");
    }

    @DisplayName("주문 승인(accept) - 주문을 승인할 수 있다.")
    @Test
    void accept03() {
        //given
        UUID 승인할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.WAITING);
        given(조회된_주문.getType()).willReturn(OrderType.TAKEOUT);
        given(orderRepository.findById(승인할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        orderService.accept(승인할_주문_아이디);

        //then
        verify(조회된_주문).setStatus(OrderStatus.ACCEPTED);
    }

    private static Stream<OrderStatus> provideOrderStatusExceptForAccepted() {
        return Stream.of(
                OrderStatus.WAITING,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.SERVED
        );
    }

    @DisplayName("주문 서빙(serve) - 승인된(accept) 주문만 서빙할 수 있다.")
    @MethodSource("provideOrderStatusExceptForAccepted")
    @ParameterizedTest
    void serve01(OrderStatus 조회된_주문_상태) {
        //given
        UUID 서빙할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getStatus()).willReturn(조회된_주문_상태);
        given(orderRepository.findById(서빙할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        assertThatThrownBy(() -> orderService.serve(서빙할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 서빙(serve) - 주문을 서빙할 수 있다.")
    @Test
    void serve02() {
        //given
        UUID 서빙할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.ACCEPTED);
        given(orderRepository.findById(서빙할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        orderService.serve(서빙할_주문_아이디);

        //then
        verify(조회된_주문).setStatus(OrderStatus.SERVED);
    }

    private static Stream<OrderType> provideOrderTypeExceptForDelivery() {
        return Stream.of(
                OrderType.EAT_IN,
                OrderType.TAKEOUT
        );
    }

    @DisplayName("주문 배달(delivering) 시작 - 배달주문인 경우에만 배달을 시작할 수 있다.")
    @MethodSource("provideOrderTypeExceptForDelivery")
    @ParameterizedTest
    void startDelivery01(OrderType 조회된_주문_타입) {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getType()).willReturn(조회된_주문_타입);
        given(orderRepository.findById(배달할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(배달할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }


    private static Stream<OrderStatus> provideOrderStatusExceptForServed() {
        return Stream.of(
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.WAITING
        );
    }

    @DisplayName("주문 배달(delivering) 시작 - 서빙(accept)된 주문만 배달을 시작할 수 있다.")
    @MethodSource("provideOrderStatusExceptForServed")
    @ParameterizedTest
    void startDelivery02(OrderStatus 조회된_주문_상태) {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getType()).willReturn(OrderType.DELIVERY);
        given(조회된_주문.getStatus()).willReturn(조회된_주문_상태);
        given(orderRepository.findById(배달할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(배달할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달(delivering) 시작 - 배달을 시작할 수 있다.")
    @Test
    void startDelivery03() {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getType()).willReturn(OrderType.DELIVERY);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.SERVED);
        given(orderRepository.findById(배달할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when
        orderService.startDelivery(배달할_주문_아이디);

        //then
        verify(조회된_주문).setStatus(OrderStatus.DELIVERING);
    }

    private static Stream<OrderStatus> provideOrderStatusExceptForDelivering() {
        return Stream.of(
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.SERVED,
                OrderStatus.WAITING
        );
    }


    @DisplayName("배달 완료(delivered) - 배달중(delivering)인 주문만 배달을 완료할 수 있다.")
    @MethodSource("provideOrderStatusExceptForDelivering")
    @ParameterizedTest
    void completeDelivery01(OrderStatus 조회된_주문_상태) {
        //given
        UUID 배달_완료할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getStatus()).willReturn(조회된_주문_상태);
        given(orderRepository.findById(배달_완료할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        assertThatThrownBy(() -> orderService.completeDelivery(배달_완료할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 완료(delivered) - 배달을  완료할 수 있다.")
    @Test
    void completeDelivery02() {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.DELIVERING);
        given(orderRepository.findById(배달할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when
        orderService.completeDelivery(배달할_주문_아이디);

        //then
        verify(조회된_주문).setStatus(OrderStatus.DELIVERED);
    }


    @DisplayName("주문 완료(complete) - 매장식사, 테이크아웃의 경우 서빙된 주문만 완료 할 수 있다.")
    @MethodSource("provideOrderTypeExceptForDelivery")
    @ParameterizedTest
    void complete01(OrderType 조회된_주문_타입) {
        //given
        UUID 완료할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getType()).willReturn(조회된_주문_타입);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.ACCEPTED);
        given(orderRepository.findById(완료할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        assertThatThrownBy(() -> orderService.complete(완료할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료(complete) - 배달주문의 경우 배달완료된 주문만 완료 할 수 있다.")
    @Test
    void complete02() {
        //given
        UUID 완료할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getType()).willReturn(OrderType.DELIVERY);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.ACCEPTED);
        given(orderRepository.findById(완료할_주문_아이디)).willReturn(Optional.of(조회된_주문));

        //when & then
        assertThatThrownBy(() -> orderService.complete(완료할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료(complete) - 매장식사의 경우 주문이 완료 되면 테이블을 정리해야 한다.")
    @Test
    void complete03() {
        //given
        UUID 완료할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        OrderTable 조회된_주문_테이블 = mock(OrderTable.class);
        given(조회된_주문.getType()).willReturn(OrderType.EAT_IN);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.SERVED);
        given(조회된_주문.getOrderTable()).willReturn(조회된_주문_테이블);
        given(orderRepository.findById(완료할_주문_아이디)).willReturn(Optional.of(조회된_주문));
        given(orderRepository.existsByOrderTableAndStatusNot(조회된_주문_테이블, OrderStatus.COMPLETED))
                .willReturn(false);
        //when
        orderService.complete(완료할_주문_아이디);
        //then
        verify(조회된_주문_테이블).setEmpty(true);
        verify(조회된_주문_테이블).setNumberOfGuests(0);
    }

    @DisplayName("주문 완료(complete) - 주문을 완료할 수 있다.")
    @Test
    void complete04() {
        //given
        UUID 완료할_주문_아이디 = UUID.randomUUID();
        Order 조회된_주문 = mock(Order.class);
        given(조회된_주문.getType()).willReturn(OrderType.TAKEOUT);
        given(조회된_주문.getStatus()).willReturn(OrderStatus.SERVED);
        given(orderRepository.findById(완료할_주문_아이디)).willReturn(Optional.of(조회된_주문));
        //when
        orderService.complete(완료할_주문_아이디);
        //then
        verify(조회된_주문).setStatus(OrderStatus.COMPLETED);
    }


    @DisplayName("주문 조회 - 등록된 모든 주문을 조회할 수 있다.")
    @Test
    void findAll() {
        // given & when
        orderService.findAll();
        //then
        verify(orderRepository).findAll();
    }

}
