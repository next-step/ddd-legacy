package kitchenpos.application;

import static kitchenpos.application.MenuFixture.뿌링클_세트;
import static kitchenpos.application.OrderFixture.매장_주문;
import static kitchenpos.application.OrderFixture.배달_주문;
import static kitchenpos.application.OrderFixture.포장_주문;
import static kitchenpos.application.OrderTableFixture.일번_테이블;
import static kitchenpos.application.OrderTableFixture.착석_테이블;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("주문")
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

    private Order 신규_주문;

    @BeforeEach
    void setUp() {
        신규_주문 = new Order();
        신규_주문.setId(UUID.randomUUID());
        신규_주문.setType(OrderType.DELIVERY);
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(뿌링클_세트);
        orderLineItem.setMenuId(뿌링클_세트.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(뿌링클_세트.getPrice());
        신규_주문.setOrderLineItems(Collections.singletonList(orderLineItem));
        신규_주문.setDeliveryAddress("배달주소");
        신규_주문.setOrderTableId(일번_테이블.getId());
        신규_주문.setOrderTable(일번_테이블);
        신규_주문.setStatus(OrderStatus.WAITING);
        신규_주문.setOrderDateTime(LocalDateTime.now());
    }

    @DisplayName("주문 예외 - 주문 유형 없음")
    @Test
    void createOrderTypeException() {
        //given
        신규_주문.setType(null);

        //when
        ThrowingCallable actual = () -> orderService.create(new Order());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }



    @DisplayName("주문 예외 - 주문 메뉴 없음")
    @ParameterizedTest(name = "주문 메뉴: [{arguments}]")
    @NullAndEmptySource
    void createOrderHasNoMenuException(List<OrderLineItem> orderLineItems) {
        //given
        신규_주문.setOrderLineItems(orderLineItems);

        //when
        ThrowingCallable actual = () -> orderService.create(신규_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 예외 - 등록되지 않은 메뉴 주문")
    @Test
    void createOrderInvalidMenuException() {
        //given
        given(menuRepository.findAllByIdIn(anyList())).willReturn(new ArrayList<>());

        //when
        ThrowingCallable actual = () -> orderService.create(신규_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 예외 - 포장 또는 배달 메뉴 수량 미달")
    @ParameterizedTest(name = "메뉴 유형: [{arguments}]")
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void createOrderInvalidQuantityException(OrderType orderType) {
        //given
        신규_주문.setType(orderType);
        OrderLineItem 모자란_수량 = new OrderLineItem();
        모자란_수량.setQuantity(-1L);
        신규_주문.setOrderLineItems(Collections.singletonList(모자란_수량));

        given(menuRepository.findAllByIdIn(anyList())).willReturn(new ArrayList<>());

        //when
        ThrowingCallable actual = () -> orderService.create(신규_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 예외 - 진열되지 않은 메뉴 주문")
    @Test
    void createOrderNotDisplayedMenuException() {
        //given
        Menu 진열되지_않은_메뉴 = new Menu();
        진열되지_않은_메뉴.setDisplayed(false);

        given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.singletonList(뿌링클_세트));
        given(menuRepository.findById(any())).willReturn(Optional.of(진열되지_않은_메뉴));

        //when
        ThrowingCallable actual = () -> orderService.create(신규_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 예외 - 가격 불일치")
    @Test
    void createOrderMismatchPriceException() {
        //given
        OrderLineItem 모자란_금액 = new OrderLineItem();
        모자란_금액.setPrice(BigDecimal.valueOf(10_000L));

        신규_주문.setOrderLineItems(Collections.singletonList(모자란_금액));

        given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.singletonList(뿌링클_세트));
        given(menuRepository.findById(any())).willReturn(Optional.of(뿌링클_세트));

        //when
        ThrowingCallable actual = () -> orderService.create(신규_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 예외 - 배달 주문 시 배달 주소 없음")
    @ParameterizedTest(name = "배달 주소: [{arguments}]")
    @NullAndEmptySource
    void createDeliveryOrderHasNoAddressException(String deliveryAddress) {
        //given
        신규_주문.setDeliveryAddress(deliveryAddress);

        given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.singletonList(뿌링클_세트));
        given(menuRepository.findById(any())).willReturn(Optional.of(뿌링클_세트));

        //when
        ThrowingCallable actual = () -> orderService.create(신규_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 예외 - 식당 내 주문 시 착석하지 않음")
    @Test
    void createEatInException() {
        //given
        신규_주문.setType(OrderType.EAT_IN);

        given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.singletonList(뿌링클_세트));
        given(menuRepository.findById(any())).willReturn(Optional.of(뿌링클_세트));
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(일번_테이블));

        //when
        ThrowingCallable actual = () -> orderService.create(신규_주문);

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 주문 성공")
    @Test
    void createOrderEatIn() {
        //given
        신규_주문.setType(OrderType.EAT_IN);
        신규_주문.setOrderTableId(착석_테이블.getId());

        given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.singletonList(뿌링클_세트));
        given(menuRepository.findById(any())).willReturn(Optional.of(뿌링클_세트));
        given(orderTableRepository.findById(any(UUID.class))).willReturn(Optional.of(착석_테이블));
        given(orderRepository.save(any(Order.class))).willReturn(신규_주문);

        //when
        Order order = orderService.create(신규_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.EAT_IN),
            () -> assertThat(order.getOrderDateTime()).isEqualTo(신규_주문.getOrderDateTime()),
            () -> assertThat(order.getOrderTableId()).isEqualTo(착석_테이블.getId())
        );
    }

    @DisplayName("포장 주문 성공")
    @Test
    void createOrderTakeout() {
        //given
        신규_주문.setType(OrderType.TAKEOUT);

        given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.singletonList(뿌링클_세트));
        given(menuRepository.findById(any())).willReturn(Optional.of(뿌링클_세트));
        given(orderRepository.save(any(Order.class))).willReturn(신규_주문);

        //when
        Order order = orderService.create(신규_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT),
            () -> assertThat(order.getOrderDateTime()).isEqualTo(신규_주문.getOrderDateTime())
        );
    }

    @DisplayName("배달 주문 성공")
    @Test
    void createOrderDelivery() {
        //given

        given(menuRepository.findAllByIdIn(anyList())).willReturn(Collections.singletonList(뿌링클_세트));
        given(menuRepository.findById(any())).willReturn(Optional.of(뿌링클_세트));
        given(orderRepository.save(any(Order.class))).willReturn(신규_주문);

        //when
        Order order = orderService.create(신규_주문);

        //then
        assertAll(
            () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
            () -> assertThat(order.getType()).isEqualTo(OrderType.DELIVERY),
            () -> assertThat(order.getOrderDateTime()).isEqualTo(신규_주문.getOrderDateTime())
        );
    }

    @DisplayName("주문 수락 예외 - 대기중인 주문만 수락 가능")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void accept(OrderStatus orderStatus) {
        //given
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.accept(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문 수락")
    @Test
    void acceptOrderDelivery() {
        //given
        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        Order actual = orderService.accept(신규_주문.getId());

        //then
        assertAll(
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED),
            () -> verify(kitchenridersClient, atLeastOnce()).requestDelivery(any(), any(), any())
        );
    }

    @DisplayName("주문 수락")
    @ParameterizedTest(name = "주문 유형: [{arguments}]")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void accept(OrderType orderType) {
        //given
        신규_주문.setType(orderType);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        Order actual = orderService.accept(신규_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("조리 완료 예외 - 수락된 주문만 가능")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void serveException(OrderStatus orderStatus) {
        //given
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.serve(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("조리 완료")
    @Test
    void serve() {
        //given
        신규_주문.setStatus(OrderStatus.ACCEPTED);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        Order actual = orderService.serve(신규_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달 중 예외 - 배달 주문만 가능")
    @ParameterizedTest(name = "주문 유형: [{arguments}]")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void startDeliveryOrderTypeException(OrderType orderType) {
        //given
        신규_주문.setType(orderType);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.startDelivery(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 중 예외 - 조리완료된 주문만 가능")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void startDeliveryOrderStatusException(OrderStatus orderStatus) {
        //given
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.startDelivery(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 중")
    @Test
    void startDelivery() {
        //given
        신규_주문.setType(OrderType.DELIVERY);
        신규_주문.setStatus(OrderStatus.SERVED);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        Order actual = orderService.startDelivery(신규_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 완료 예외 - 배달중인 주문만 가능")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    void completeDeliveryOrderStatusException(OrderStatus orderStatus) {
        //given
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.completeDelivery(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 완료")
    @Test
    void completeDelivery() {
        //given
        신규_주문.setType(OrderType.DELIVERY);
        신규_주문.setStatus(OrderStatus.DELIVERING);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        Order actual = orderService.completeDelivery(신규_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달 주문 완료 예외 - 배달 완료 주문만 가능")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
    void completeDeliveryStatusException(OrderStatus orderStatus) {
        //given
        신규_주문.setType(OrderType.DELIVERY);
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.complete(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문 완료")
    @Test
    void completeDeliveryOrder() {
        //given
        신규_주문.setType(OrderType.DELIVERY);
        신규_주문.setStatus(OrderStatus.DELIVERED);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        Order actual = orderService.complete(신규_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장 주문 완료 예외 - 조리완료된 주문만 가능")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERED", "DELIVERING", "COMPLETED"})
    void completeTakeoutOrderStatusException(OrderStatus orderStatus) {
        //given
        신규_주문.setType(OrderType.TAKEOUT);
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.complete(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 주문 완료 예외 - 조리완료된 주문만 가능")
    @ParameterizedTest(name = "주문 상태: [{arguments}]")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERED", "DELIVERING", "COMPLETED"})
    void completeEatInStatusException(OrderStatus orderStatus) {
        //given
        신규_주문.setType(OrderType.EAT_IN);
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        ThrowingCallable actual = () -> orderService.complete(신규_주문.getId());

        //then
        assertThatThrownBy(actual).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료 - 포장, 배달")
    @ParameterizedTest(name = "주문 유형: [{0}], 주문 상태: [{1}]")
    @MethodSource("completeTakeoutOrDelivery")
    void completeTakeoutOrDeliveryOrder(OrderType orderType, OrderStatus orderStatus) {
        //given
        신규_주문.setType(orderType);
        신규_주문.setStatus(orderStatus);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));

        //when
        Order actual = orderService.complete(신규_주문.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    private static Stream<Arguments> completeTakeoutOrDelivery() {
        return Stream.of(
            Arguments.of(OrderType.DELIVERY, OrderStatus.DELIVERED),
            Arguments.of(OrderType.TAKEOUT, OrderStatus.SERVED)
        );
    }

    @DisplayName("매장 주문 완료")
    @Test
    void completeEatInOrder() {
        //given
        신규_주문.setType(OrderType.EAT_IN);
        신규_주문.setStatus(OrderStatus.SERVED);

        given(orderRepository.findById(any(UUID.class))).willReturn(Optional.of(신규_주문));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        //when
        Order actual = orderService.complete(신규_주문.getId());

        //then
        assertAll(
            () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
            () -> assertThat(actual.getOrderTable().isEmpty()).isTrue(),
            () -> assertThat(actual.getOrderTable().getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("모든 주문 조회")
    @Test
    void findAll() {
        //given
        given(orderRepository.findAll()).willReturn(Arrays.asList(배달_주문, 포장_주문, 매장_주문));

        //when
        List<Order> actual = orderService.findAll();

        //then
        assertThat(actual).hasSize(3);
    }

}
