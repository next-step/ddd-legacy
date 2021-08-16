package kitchenpos.order.application;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.order.fixture.OrderFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Order 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private static final int 가격 = 16000;
    private static final int 주문_수량 = 3;

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
        orderService = new OrderService(
                orderRepository,
                menuRepository,
                orderTableRepository,
                kitchenridersClient);
    }

    @DisplayName("주문은 배달, 포장, 매장식사 셋 중에 하나가 아닐 경우 IllegalArgumentException을 던진다")
    @ParameterizedTest
    @NullSource
    public void createWithValidStatus(OrderType type) {
        // given
        Order 주문 = 주문(type, 서울_주소, WAITING, Arrays.asList(
                주문_상품(주문_수량, 가격, null)), null);

        // when, then
        assertThatThrownBy(() -> orderService.create(주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품이 null이나 빈 경우 IllegalArgumentException을 던진다")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithoutOrderLineItem(List<OrderLineItem> orderLineItems) {
        // given
        Order 매장_주문 = 매장_주문(WAITING, orderLineItems);

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품이 등록된 상품에 없는 경우 IllegalArgumentException을 던진다")
    @Test
    public void createWithNotRegisteredMenu() {
        // given
        Order 매장_주문 = 매장_주문(WAITING, Arrays.asList(
                주문_상품(주문_수량, 가격, null),
                주문_상품(주문_수량, 가격, null)));

        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(가격, false);

        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(후라이드_한마리_메뉴));

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품 수량은 매장 식사가 아닌 경우 하나 이상 주문하지 않으면 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = "EAT_IN")
    public void createShouldOrderAtLeastOneIfOrderTypeIsNotEatIn(OrderType notEatIn) {
        // given
        int quantity = -1;

        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(가격, false);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(후라이드_한마리_메뉴));

        Order 매장_식사_아닌_주문 = 주문(notEatIn, 서울_주소, WAITING, Arrays.asList(
                주문_상품(quantity, 가격, null)), null);

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_식사_아닌_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 숨겨진 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(booleans = {false})
    public void createWithNonDisplayedMenu(boolean isDisplay) {
        // given
        Menu 숨겨진_메뉴 = 후라이드_한마리_메뉴(가격, isDisplay);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(숨겨진_메뉴));
        given(menuRepository.findById(any())).willReturn(Optional.of(숨겨진_메뉴));

        Order 배달_주문 = 배달_주문(WAITING, Arrays.asList(
                주문_상품(주문_수량, 가격, null)));

        // when, then
        assertThatThrownBy(() -> orderService.create(배달_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격과 각 주문 항목의 가격이 다를 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @CsvSource(value = {"17000,15000","1000,100"})
    public void createWithDifferentMenuPriceAndOrderLineItemPrice(int menuPrice, int orderLineItemPrice) {
        // given
        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(menuPrice, true);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(후라이드_한마리_메뉴));
        given(menuRepository.findById(any())).willReturn(Optional.of(후라이드_한마리_메뉴));

        Order 주문 = 배달_주문(WAITING, Arrays.asList(주문_상품(주문_수량, orderLineItemPrice, null)));

        // when, then
        assertThatThrownBy(() -> orderService.create(주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달 주문인 경우, 배달 주소가 null이거나 빈 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithDeliveryAddressIfItIsDelivery(String deliveryAddress) {
        // given
        OrderType delivery = DELIVERY;

        Menu menu = 후라이드_한마리_메뉴(가격, true);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        Order 배달_주문 = 주문(delivery, deliveryAddress, WAITING, Arrays.asList(
                주문_상품(주문_수량, 가격, null)), null);

        // when, then
        assertThatThrownBy(() -> orderService.create(배달_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 식사의 경우, 주문 테이블이 빈 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(booleans = true)
    public void createWithEmptyTableIfEatIn(boolean isEmptyTable) {
        // given
        OrderType eatIn = EAT_IN;

        Menu 메뉴 = 후라이드_한마리_메뉴(가격, true);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(메뉴));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

        OrderTable 빈_주문_테이블 = 주문_1번_테이블(isEmptyTable);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(빈_주문_테이블));

        Order 매장_주문 = 주문(eatIn, null, WAITING, Arrays.asList(
                주문_상품(주문_수량, 가격, null)), null);

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_주문))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태가 대기 중이 아닌 경우 IllegalStateException을 던진다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAITING")
    public void acceptWithoutWaitingStatus(OrderStatus excludeWaiting) {
        // given
        Order 대기_상태가_아닌_주문 = 주문(EAT_IN, null, excludeWaiting, null, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(대기_상태가_아닌_주문));

        // when, then
        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문인 경우, 주문 정보를 라이더에게 전달한다.")
    @Test
    public void acceptRequestOrderInfoToRiderIfDelivery() {
        // given
        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(가격, true);
        OrderLineItem 후라이드_한마리_주문_상품 = 주문_상품(주문_수량, 가격, 후라이드_한마리_메뉴);
        Order 대기_상태인_배달_주문 = 배달_주문(WAITING, Arrays.asList(후라이드_한마리_주문_상품));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(대기_상태인_배달_주문));

        // when
        orderService.accept(UUID.randomUUID());

        // then
        verify(kitchenridersClient,times(1))
                .requestDelivery(any(), any(), anyString());
    }

    @DisplayName("주문 상태를 수락으로 변경한다.")
    @Test
    public void accept() {
        // given
        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(가격, true);
        OrderLineItem 후라이드_한마리_주문_상품 = 주문_상품(주문_수량, 가격, 후라이드_한마리_메뉴);
        Order 대기_상태인_배달_주문 = 배달_주문(WAITING, Arrays.asList(후라이드_한마리_주문_상품));

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(대기_상태인_배달_주문));

        // when
        Order acceptedOrder = orderService.accept(UUID.randomUUID());

        // then
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문이 수락되지 않은 경우 IllegalStateException를 던진다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACCEPTED")
    public void serveIfNotAccepted(OrderStatus nonAcceptedStatus) {
        // given
        Order 수락_상태가_아닌_주문 = 매장_주문(nonAcceptedStatus, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(수락_상태가_아닌_주문));

        // when, then
        assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태를 서빙 완료로 변경한다.")
    @Test
    public void serve() {
        // given
        Order 수락_상태_주문 = 매장_주문(ACCEPTED, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(수락_상태_주문));

        // when, then
        Order 서빙된_주문 = orderService.serve(UUID.randomUUID());

        // then
        assertThat(서빙된_주문.getStatus()).isEqualTo(SERVED);
    }

    @DisplayName("배달 주문이 아닌 경우 IllegalStateException를 던진다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERY")
    public void startDeliveryIfNotDelivery(OrderType 배달을_제외한_타입) {
        // given
        Order 배달이_아닌_주문 = 주문(배달을_제외한_타입, 서울_주소, SERVED, null, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(배달이_아닌_주문));

        // when, then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("서빙 상태가 아닌 경우 IllegalStateException를 던진다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
    public void startDeliveryIfNotDelivery(OrderStatus 서빙이_아닌_상태) {
        // given
        Order 서빙상태가_아닌_배달_주문 = 배달_주문(서빙이_아닌_상태, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(서빙상태가_아닌_배달_주문));

        // when, then
        assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문이 아닌 경우 IllegalStateException를 던진다")
    @Test
    public void startDelivery() {
        // given
        Order 서빙된_배달_주문 = 배달_주문(SERVED, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(서빙된_배달_주문));

        // when
        Order 배달_중_상태의_주문 = orderService.startDelivery(UUID.randomUUID());

        // then
        assertThat(배달_중_상태의_주문.getStatus()).isEqualTo(DELIVERING);
    }

    @DisplayName("배달 주문이 아닌 경우 IllegalStateException를 던진다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERY")
    public void completeDeliveryIfNotDelivery(OrderType 배달을_제외한_타입) {
        // given
        Order 배달이_아닌_주문 = 주문(배달을_제외한_타입, 서울_주소, DELIVERING, null, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(배달이_아닌_주문));

        // when, then
        assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("배달 중이 아닌 경우 IllegalStateException를 던진다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERING")
    public void completeDeliveryIfNotDelivery(OrderStatus 배송중이_아닌_상태) {
        // given
        Order 배송중_상태가_아닌_배달_주문 = 배달_주문(배송중이_아닌_상태, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(배송중_상태가_아닌_배달_주문));

        // when, then
        assertThatThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 상태를 배달 완료로 변경한다")
    @Test
    public void completeDelivery() {
        // given
        Order 배달_중인_배달_주문 = 배달_주문(DELIVERING, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(배달_중인_배달_주문));

        // when
        Order 배달_완료_상태의_주문 = orderService.completeDelivery(UUID.randomUUID());

        // then
        assertThat(배달_완료_상태의_주문.getStatus()).isEqualTo(DELIVERED);
    }

    @DisplayName("배달 주문인 경우, 주문 상태가 배달 완료 상태가 아닌 경우 IllegalStateException를 던진다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERED")
    public void completeIfNotDelivery(OrderStatus 배달_완료가_아닌_상태) {
        // given
        Order 배달_완료가_안된_배달_주문 = 배달_주문(배달_완료가_아닌_상태, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(배달_완료가_안된_배달_주문));

        // when, then
        assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("포장이나 매장 식사의 경우, 주문 상태가 서빙 완료가 아닌 경우 IllegalStateException를 던진다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
    public void completeNotServedEatInOrTakeOut(OrderStatus 서빙_완료가_아닌_상태) {
        // given
        Order 서빙_완료가_안된_주문 = 주문(EAT_IN, null, 서빙_완료가_아닌_상태, null, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(서빙_완료가_안된_주문));

        // when, then
        assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 식사인 경우, 주문 테이블의 고객 수를 0명 그리고 빈 자리로 변경한다.")
    @Test
    public void completeEmptyTableAndZeroCustomerIfEatIn() {
        // given
        OrderTable 주문_테이블 = 주문_1번_테이블(false);
        Order 서빙_완료된_주문 = 주문(EAT_IN, null, SERVED, null, 주문_테이블);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(서빙_완료된_주문));

        given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                .willReturn(false);

        // when, then
        Order 완료된_주문 = orderService.complete(UUID.randomUUID());

        // then
        assertAll(
                () -> assertThat(완료된_주문.getOrderTable().getNumberOfGuests()).isZero(),
                () -> assertThat(완료된_주문.getOrderTable().isEmpty()).isTrue());
    }

    @DisplayName("주문 상태를 완료 상태로 변경한다.")
    @Test
    public void complete() {
        // given
        Order 서빙_완료된_매장_주문 = 주문(DELIVERY, null, DELIVERED, null, null);

        given(orderRepository.findById(any()))
                .willReturn(Optional.of(서빙_완료된_매장_주문));

        // when
        Order 완료된_주문 = orderService.complete(UUID.randomUUID());

        // then
        assertThat(완료된_주문.getStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("모든 주문을 조회한다.")
    @Test
    public void findAll() {
        // given
        List<Order> orders = Arrays.asList(
                매장_주문(SERVED, null),
                배달_주문(COMPLETED, null));

        given(orderRepository.findAll())
                .willReturn(orders);

        // when
        List<Order> findOrders = orderService.findAll();

        // then
        assertThat(findOrders.size()).isEqualTo(orders.size());
    }
}
