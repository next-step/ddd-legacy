package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kitchenpos.application.MenuServiceTest.createMenu;
import static kitchenpos.application.MenuServiceTest.createMenuProduct;
import static kitchenpos.application.OrderTableServiceTest.createOrderTable;
import static kitchenpos.application.ProductServiceTest.createProduct;
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
        Product product = createProduct("햄버거", new BigDecimal("1000"));
        MenuProduct menuProduct = createMenuProduct(product, 1L);

        menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));
        orderTable = createOrderTable("테이블1", 3);
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
        Order request = createOrder(OrderType.EAT_IN, orderLineItems, "주소지");

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포장 및 배달 주문의 경우 주문할 메뉴의 양이 0 이상이어야 한다")
    @Test
    void notCreateTakeoutOrDeliveryOrderWithQuantityOfMenuLessThanZero() {
        // given
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소지");

        given(menuRepository.findAllByIdIn(any())).willReturn(emptyList());

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

    @DisplayName("화면에 표시되지 않고 있는 메뉴를 주문한 경우엔 주문을 생성할 수 없다")
    @Test
    void noteCreateOrderIfMenuIsHidden() {
        // given
        menu.setDisplayed(false);

        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소지");

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴의 현재 가격과 주문시점의 메뉴 가격이 다르면 주문을 생성할 수 없다")
    @Test
    void notCreateOrderIfMenuPriceIsDifferentFromOrderPoint() {
        // given
        OrderLineItem orderLineItem = createOrderLineItem(1L, new BigDecimal("3000"), menu);
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소지");

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
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소");

        orderTable.setOccupied(false);

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when & then
        assertThatThrownBy(() -> sut.create(request)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 생성할 수 있다")
    @Test
    void createOrder() {
        // given
        Order request = createOrder(OrderType.EAT_IN, List.of(orderLineItem), "주소");

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.save(any())).willReturn(new Order());

        // when
        Order result = sut.create(request);

        // then
        assertThat(result).isExactlyInstanceOf(Order.class);
    }

    @DisplayName("주문이 대기중이 아닌 경우에는 수락 처리할 수 없다")
    @Test
    void notAcceptOrderIfOrderStatusIsNotWaiting() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.accept(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 수락 처리할 수 있다")
    @Test
    void acceptOrder() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.WAITING);

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
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.WAITING);
        order.setOrderLineItems(List.of(orderLineItem));

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.accept(uuid);

        // then
        verify(kitchenridersClient).requestDelivery(any(), any(), any());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("수락 상태가 아닌 주문을 제공 처리할 수 없다")
    @Test
    void notServeOrderIfOrderStatusIsNotAccepted() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.WAITING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.serve(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 제공 처리할 수 있다")
    @Test
    void serveOrder() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // then
        sut.serve(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달 주문이 아닌 경우 배달을 시작 처리할 수 없다")
    @Test
    void notStartDeliveryIfOrderTypeIsNotDelivery() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문이 제공인 상태가 아닌 경우 배달을 시작 처리할 수 없다")
    @Test
    void notStartDeliveryIfOrderStatusIsNotServed() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.startDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달을 시작 처리할 수 있다")
    @Test
    void startDelivery() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.SERVED);

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
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.completeDelivery(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달을 완료 처리할 수 있다")
    @Test
    void completeDelivery() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

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
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERING);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.complete(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "포장 주문 또는 매장에서 식사하는 경우 제공 상태가 아닌 경우 주문을 완료 처리할 수 없다: orderType = {0}")
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void notCompleteTakeoutOrEatInOrderIfOrderStatusIsNotServiced(OrderType orderType) {
        // given
        Order order = createOngoingOrder(orderType, OrderStatus.ACCEPTED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> sut.complete(uuid)).isExactlyInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문을 완료 처리할 수 있다")
    @Test
    void completeDeliveryOrder() {
        // given
        Order order = createOngoingOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.complete(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("포장 주문을 완료 처리할 수 있다")
    @Test
    void completeTakeOutOrder() {
        // given
        Order order = createOngoingOrder(OrderType.TAKEOUT, OrderStatus.SERVED);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        sut.complete(uuid);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장에서 식사하는 주문을 완료 처리할 수 있다")
    @Test
    void completeEatInOrder() {
        // given
        Order order = createOngoingOrder(OrderType.EAT_IN, OrderStatus.SERVED);
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

    public static Order createOngoingOrder(OrderType orderType, OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(uuid);
        order.setType(orderType);
        order.setStatus(orderStatus);
        return order;
    }

    public static Order createOrder(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order order = new Order();
        order.setId(uuid);
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(uuid);
        return order;
    }

    public static OrderLineItem createOrderLineItem(long quantity, BigDecimal price, Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
