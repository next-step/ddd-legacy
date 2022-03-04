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
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(2000l);
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
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(1500l);
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
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(1500l);
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
        BigDecimal 조회된_메뉴_가격 = BigDecimal.valueOf(1500l);
        given(조회된_메뉴.getPrice()).willReturn(조회된_메뉴_가격);
        given(menuRepository.findById(주문_등록_요청_메뉴_아이디)).willReturn(Optional.of(조회된_메뉴));
        //when
        orderService.create(주문_등록_요청);
        //then
        verify(orderRepository).save(any(Order.class));
    }

}