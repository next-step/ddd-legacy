package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    @Test
    @DisplayName("새로운_배달_주문을_등록한다")
    void newOrderTest() {
        // given
        Order order = TEST_ORDER_DELIVERY();
        Menu menu = TEST_MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        Order actual = orderService.create(order);

        // then
        verify(menuRepository, times(1)).findAllByIdIn(any());
        verify(menuRepository, times(1)).findById(any());
        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(actual.getDeliveryAddress()).isNotEmpty();
    }

    @Test
    @DisplayName("새로운_테이크아웃_주문을_등록한다")
    void newTakeOutOrderTest() {
        // given
        Order order = TEST_ORDER_TAKEOUT();
        Menu menu = TEST_MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        Order actual = orderService.create(order);

        // then
        verify(menuRepository, times(1)).findAllByIdIn(any());
        verify(menuRepository, times(1)).findById(any());
        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @Test
    @DisplayName("새로운_매장_주문을_등록한다")
    void newEatInTest() {
        // given
        Order order = TEST_ORDER_EAT_IN();
        Menu menu = TEST_MENU();
        OrderTable value = TEST_ORDER_TABLE();
        value.setOccupied(true);
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(order.getOrderTableId()))
                .willReturn(Optional.of(value));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        Order actual = orderService.create(order);

        // then
        verify(menuRepository, times(1)).findAllByIdIn(any());
        verify(menuRepository, times(1)).findById(any());
        verify(orderTableRepository, times(1)).findById(order.getOrderTableId());
        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(actual.getOrderTableId()).isNotNull();
    }

    @Test
    @DisplayName("주문_타입은_배달_매장_포장_중_하나이어야_한다")
    void menuTypeTest() {
        // given
        Order order = TEST_ORDER_EAT_IN();

        // when
        order.setType(null);

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문의_주문_내역은_비어있을_수_없다")
    void menuListNotEmpty() {
        // given
        Order order = TEST_ORDER_EAT_IN();

        // when
        order.setOrderLineItems(null);

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문_내역에_포함된_메뉴들은_존재하는_메뉴들이어야_한다")
    void menuShouldExist() {
        // given
        Order order = TEST_ORDER_EAT_IN();

        // when
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of());

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"take_out", "delivery"})
    @DisplayName("주문_타입이_매장_타입이_아니라면_주문_내역의_수량이_0이상이어야_한다")
    void menuQuantityTest(String typeValue) {
        // given
        Order order = TEST_ORDER_DELIVERY();
        Menu menu = TEST_MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

        // when
        OrderType type = typeValue.equals("take_out") ? OrderType.TAKEOUT : OrderType.DELIVERY;
        OrderLineItem orderLineItem = TEST_ORDER_LINE_ITEM();
        orderLineItem.setQuantity(-1);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setType(type);

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문_내역들의_메뉴들은_활성화_된_메뉴들이어야_한다")
    void menuShouldDisplay() {
        // given
        Order order = TEST_ORDER_EAT_IN();
        Menu menu = TEST_MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

        // when
        Menu hideMenu = TEST_MENU();
        hideMenu.setDisplayed(false);
        given(menuRepository.findById(any())).willReturn(Optional.of(hideMenu));

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문_내역의_가격과_메뉴의_가격이_일치하여야_한다")
    void menuAndMenuLinePriceTest() {
        // given
        Order order = TEST_ORDER_EAT_IN();
        Menu menu = TEST_MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

        // when
        Menu priceChangedMenu = TEST_MENU();
        priceChangedMenu.setPrice(new BigDecimal(999_999_999));
        given(menuRepository.findById(any())).willReturn(Optional.of(priceChangedMenu));

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문_타입이_배달이라면_주소가_적혀있어야_한다")
    void addressTest() {
        // given
        Order order = TEST_ORDER_DELIVERY();
        Menu menu = TEST_MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        order.setDeliveryAddress(null);

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문_타입이_매장이라면_사용_가능한_테이블이_지정되어야_한다")
    void orderTableShouldSet() {
        // given
        Order order = TEST_ORDER_EAT_IN();
        Menu menu = TEST_MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        OrderTable orderTable = TEST_ORDER_TABLE();
        orderTable.setOccupied(false);
        given(orderTableRepository.findById(order.getOrderTableId()))
                .willReturn(Optional.of(orderTable));

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"take_out", "eat_in"})
    @DisplayName("테이크아웃과_매장_주문을_수락한다")
    void takeOutAndEatInTest(String typeName) {
        // given
        Order order = getOrderByTypeName(typeName);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order result = orderService.accept(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("수락하려는_주문의_상태가_대기중이어야_한다")
    void acceptStatusTest() {
        // given
        Order order = TEST_ORDER_EAT_IN();

        // when
        order.setStatus(OrderStatus.COMPLETED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // then
        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("배달_주문이라면_배달을_요청한다")
    void deliveryAcceptTest() {
        // given
        Order order = TEST_ORDER_DELIVERY();
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        orderService.accept(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        verify(kitchenridersClient, times(1))
                .requestDelivery(eq(order.getId()),any(BigDecimal.class), anyString());
    }

    @Test
    @DisplayName("주문을_조리_완료하고_제공_상태로_변경한다")
    void changeStatusTest() {
        // given
        Order order = TEST_ORDER_DELIVERY();
        order.setStatus(OrderStatus.ACCEPTED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order result = orderService.serve(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문의_상태가_수락된_상태이어야_한다")
    void orderStatusShouldAccept() {
        // given
        Order order = TEST_ORDER_EAT_IN();

        // when
        order.setStatus(OrderStatus.COMPLETED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // then
        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문의_배달을_시작한다")
    void startDelivery() {
        // given
        Order order = TEST_ORDER_DELIVERY();
        order.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order result = orderService.startDelivery(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @ParameterizedTest
    @CsvSource(value = {"take_out", "eat_in"})
    @DisplayName("주문_타입이_배달이어야_한다")
    void typeShouldDeliveryTest(String typeName) {
        // given
        Order order = getOrderByTypeName(typeName);

        // when
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문_상태가_제공_상태이어야_한다")
    void statusShouldServed() {
        // given
        Order order = TEST_ORDER_DELIVERY();

        // when
        order.setStatus(OrderStatus.COMPLETED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // then
        assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문의_배달을_완료한다")
    void completeDelivery() {
        // given
        Order order = TEST_ORDER_DELIVERY();
        order.setStatus(OrderStatus.DELIVERING);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order result = orderService.completeDelivery(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("주문의_상태가_배달중이어야_한다")
    void statusShouldDelivering() {
        // given
        Order order = TEST_ORDER_DELIVERY();

        // when
        order.setStatus(OrderStatus.COMPLETED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // then
        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("배달_주문을_완료한다")
    void completeDeliveryTest() {
        // given
        Order order = TEST_ORDER_DELIVERY();
        order.setStatus(OrderStatus.DELIVERED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order result = orderService.complete(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문이_배달이라면_주문_상태가_배달_완료이어야한다")
    void checkDeliveryStatus() {
        // given
        Order order = TEST_ORDER_DELIVERY();

        // when
        order.setStatus(OrderStatus.COMPLETED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("매장_주문을_완료하면서_테이블도_정리한다")
    void clearTableTest() {
        // given
        Order order = TEST_ORDER_EAT_IN();
        order.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order result = orderService.complete(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        verify(orderRepository, times(1))
                .existsByOrderTableAndStatusNot(order.getOrderTable(), OrderStatus.COMPLETED);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("포장_주문을_완료한다")
    void completeTakeOutOrder() {
        // given
        Order order = TEST_ORDER_TAKEOUT();
        order.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order result = orderService.complete(order.getId());

        // then
        verify(orderRepository, times(1)).findById(order.getId());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @ParameterizedTest
    @CsvSource(value = {"take_out", "eat_in"})
    @DisplayName("주문이_매장이거나_포장이라면_주문_상태가_제공된_상태이여야한다")
    void checkTakeOutAndEatInStatus(String typeName) {
        // given
        Order order = getOrderByTypeName(typeName);

        // when
        order.setStatus(OrderStatus.COMPLETED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // then
        assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("모든_주문_정보를_가져온다")
    void findAll() {
        // given
        Order delivery = TEST_ORDER_DELIVERY();
        Order takeout = TEST_ORDER_TAKEOUT();
        Order eat_in = TEST_ORDER_EAT_IN();
        given(orderRepository.findAll()).willReturn(List.of(delivery, takeout, eat_in));

        // when
        List<Order> result = orderService.findAll();

        // then
        verify(orderRepository, times(1)).findAll();
        assertThat(result).containsExactly(delivery, takeout, eat_in);
    }

    private Order getOrderByTypeName(String typeName) {
        Order order;
        switch (typeName) {
            case "take_out":
                order = TEST_ORDER_TAKEOUT();
                break;
            case "eat_in":
            default:
                order = TEST_ORDER_EAT_IN();
        }
        return order;
    }
}