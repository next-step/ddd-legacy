package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.OrderFixtures;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;
import static kitchenpos.fixture.OrderFixtures.*;
import static kitchenpos.fixture.OrderTableFixtures.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService sut;

    private final static UUID uuid = UUID.randomUUID();

    private Menu menu;
    private OrderLineItem orderLineItem;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        MenuProduct menuProduct = createMenuProduct();

        menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));
        orderTable = createOrderTable();
        orderLineItem = createOrderLineItem(1L, menu.getPrice(), menu);
    }

    @DisplayName("주문 타입이 null이면 주문을 생성할 수 없다")
    @Test
    void notCreateOrderWithoutOrderType() {
        // given
        Order request = new Order();
        request.setType(null);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "주문은 최소한 1개 이상의 상품으로 이루어져야 주문을 생성할 수 있다: orderLineItems = {0}")
    @NullAndEmptySource
    void notCreateOrderWithZeroOrFewerProduct(List<OrderLineItem> orderLineItems) {
        // given
        Order request = eatInOrder(orderLineItems);

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "포장 및 배달 주문의 경우 주문할 메뉴의 양이 0이상이 아니라면 주문을 생성할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void notCreateTakeoutOrDeliveryOrderWithQuantityOfMenuLessThanZero(OrderType orderType) {
        // given
        OrderLineItem orderLineItem = createOrderLineItem(-1L, menu.getPrice(), menu);
        Order request = createOrder(orderType, List.of(orderLineItem), "주소지");

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "화면에 표시되지 않고 있는 메뉴를 주문한 경우엔 주문을 생성할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class)
    void notCreateOrderIfMenuIsHidden(OrderType orderType) {
        // given
        menu.setDisplayed(false);

        Order request = createOrder(orderType);

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "메뉴의 현재 가격과 주문시점의 메뉴 가격이 다르면 주문을 생성할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class)
    void notCreateOrderIfMenuPriceIsDifferentFromOrderPoint(OrderType orderType) {
        // given
        OrderLineItem orderLineItem = createOrderLineItem(1L, new BigDecimal("3000"), menu);
        Order request = createOrder(orderType, List.of(orderLineItem));

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "배달 주문의 경우 주소지가 없다면 주문을 생성할 수 없다: deliveryAddress = {0}")
    @NullAndEmptySource
    void notCreateDeliveryOrderIfNoAddress(String deliveryAddress) {
        // given
        Order request = createOrder(OrderType.DELIVERY, List.of(orderLineItem), deliveryAddress);

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장에서 식사하는 경우 테이블에 착석해 있지 않다면 주문을 생성할 수 없다")
    @Test
    void notCreateEatInOrderIfNotSitting() {
        // given
        Order request = eatInOrder();

        orderTable.setOccupied(false);

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Nested
    @DisplayName("주문을 생성할 수 있다")
    class CreateOrder {

        @DisplayName("매장에서 식사")
        @Test
        void eatIn() {
            // given
            Order request = eatInOrder();

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
            given(orderRepository.save(any())).willReturn(new Order());

            // when
            Order result = sut.create(request);

            // then
            assertThat(result).isExactlyInstanceOf(Order.class);
        }

        @DisplayName("포장 주문")
        @Test
        void takeout() {
            // given
            Order request = takeoutOrder();

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderRepository.save(any())).willReturn(new Order());

            // when
            Order result = sut.create(request);

            // then
            assertThat(result).isExactlyInstanceOf(Order.class);
        }

        @Test
        @DisplayName("배달 주문")
        void delivery() {
            // given
            Order request = deliveryOrder();

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderRepository.save(any())).willReturn(new Order());

            // when
            Order result = sut.create(request);

            // then
            assertThat(result).isExactlyInstanceOf(Order.class);
        }

    }

    @ParameterizedTest(name = "주문이 대기중이 아닌 경우에는 수락 처리할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class)
    void notAcceptOrderIfOrderStatusIsNotWaiting(OrderType orderType) {
        // given
        Order order = createOrder(orderType, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.accept(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "주문을 수락 처리할 수 있다: orderType = {0}")
    @EnumSource(value = OrderType.class)
    void acceptOrder(OrderType orderType) {
        // given
        Order order = createOrder(orderType, OrderStatus.WAITING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.accept(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("배달 주문의 수락처리는 kitchenridersClient 배달 요청 API를 요청한 후에 이뤄진다")
    @Test
    void acceptDeliveryOrderAfterRequestingKitchenidersClientAPI() {
        // given
        Order order = deliveryOrder(OrderStatus.WAITING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.accept(uuid);

        // then
        verify(kitchenridersClient).requestDelivery(any(), any(), any());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @ParameterizedTest(name = "수락 상태가 아닌 주문을 제공 처리할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class)
    void notServeOrderIfOrderStatusIsNotAccepted(OrderType orderType) {
        // given
        Order order = createOrder(orderType, OrderStatus.WAITING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.serve(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "주문을 제공 처리할 수 있다: orderType = {0}")
    @EnumSource(value = OrderType.class)
    void serveOrder() {
        // given
        Order order = takeoutOrder(OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // then
        sut.serve(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @ParameterizedTest(name = "배달 주문이 아닌 경우 배달을 시작 처리할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void notStartDeliveryIfOrderTypeIsNotDelivery(OrderType orderType) {
        // given
        Order order = createOrder(orderType, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문이 제공인 상태가 아닌 경우 배달을 시작 처리할 수 없다")
    @Test
    void notStartDeliveryIfOrderStatusIsNotServed() {
        // given
        Order order = deliveryOrder(OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달을 시작 처리할 수 있다")
    @Test
    void startDelivery() {
        // given
        Order order = deliveryOrder(OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // then
        sut.startDelivery(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달을 시작한 상태가 아닌 경우 배달을 완료 처리할 수 없다")
    @Test
    void notCompleteDeliveryIfOrderStatusIsNotDelivering() {
        // given
        Order order = deliveryOrder(OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.completeDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달을 완료 처리할 수 있다")
    @Test
    void completeDelivery() {
        // given
        Order order = deliveryOrder(OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.completeDelivery(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달 주문이면서 배달 완료 상태가 아닌 경우 주문을 완료 처리할 수 없다")
    @Test
    void notCompleteDeliveryOrderIfOrderStatusIsNotDelivered() {
        // given
        Order order = deliveryOrder(OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.complete(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "포장 주문 또는 매장에서 식사하는 경우 제공 상태가 아닌 경우 주문을 완료 처리할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void notCompleteTakeoutOrEatInOrderIfOrderStatusIsNotServiced(OrderType orderType) {
        // given
        Order order = OrderFixtures.createOrder(orderType, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.complete(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @Nested
    @DisplayName("주문을 완료할 수 있다")
    class CompleteOrder {
        @DisplayName("매장에서 식사")
        @Test
        void completeEatInOrder() {
            // given
            Order order = eatInOrder(OrderStatus.SERVED);
            order.setOrderTable(orderTable);

            given(orderRepository.findById(any())).willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

            // when
            sut.complete(uuid);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(orderTable.getNumberOfGuests()).isZero();
            assertThat(orderTable.isOccupied()).isFalse();
        }

        @DisplayName("포장 주문")
        @Test
        void completeTakeOutOrder() {
            // given
            Order order = takeoutOrder(OrderStatus.SERVED);

            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when
            sut.complete(uuid);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("배달 주문")
        @Test
        void completeDeliveryOrder() {
            // given
            Order order = deliveryOrder(OrderStatus.DELIVERED);

            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            // when
            sut.complete(uuid);

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }
}
