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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.application.MenuServiceTest.defaultMenu;
import static kitchenpos.application.OrderTableServiceTest.defaultOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private final static UUID DEFAULT_ORDER_ID = UUID.randomUUID();
    private final static Long DEFAULT_ORDER_LINE_ITEM_ID = 1L;

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

    private List<OrderLineItem> defaultOrderLineItems() {
        OrderLineItem orderLineItem = new OrderLineItem();

        final Menu menu = defaultMenu();
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.TEN);
        orderLineItem.setQuantity(1);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setSeq(DEFAULT_ORDER_LINE_ITEM_ID);

        return Arrays.asList(orderLineItem);
    }

    private Order createOrder(final UUID id, final OrderStatus orderStatus, final OrderType orderType, final OrderTable orderTable, final List<OrderLineItem> orderLineItems, final String deliveryAddress) {
        Order order = new Order();

        order.setId(DEFAULT_ORDER_ID);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(orderLineItems);
        order.setStatus(orderStatus);
        order.setType(orderType);
        order.setDeliveryAddress(deliveryAddress);

        return order;
    }

    @DisplayName("주문을 생성할 수 있다")
    @Test
    void create_order() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");

        given(menuRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(Arrays.asList(defaultMenu()));
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenu()));
        given(orderRepository.save(Mockito.any(Order.class)))
                .willReturn(order);

        final Order result = orderService.create(order);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(order);
    }

    @DisplayName("주문을 생성할 때 주문 타입은 필수이다")
    @Test
    void create_order_with_null_and_empty_order_type() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, null, defaultOrderTable(), defaultOrderLineItems(), "");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문을 생성 할 때 주문할 메뉴의 항목은 필수이다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_order_with_null_and_empty_menu(final List<OrderLineItem> orderLineItems) {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.TAKEOUT, defaultOrderTable(), orderLineItems, "");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("비공개 상태의 메뉴는 주문이 불가능하다")
    @Test
    void create_order_with_none_display_menu() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");

        given(menuRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(Arrays.asList(defaultMenu()));

        Menu menu = defaultMenu();
        menu.setDisplayed(false);
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("메뉴의 가격과 주문을 통해 전달된 주문 가격이 다르면 주문이 불가능하다")
    @Test
    void create_order_with_not_match_total_price() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");

        given(menuRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(Arrays.asList(defaultMenu()));

        Menu menu = defaultMenu();
        menu.setPrice(menu.getPrice().multiply(BigDecimal.valueOf(2)));
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(menu));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문을 수락할 수 있다")
    @Test
    void accept() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");
        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        final Order result = orderService.accept(DEFAULT_ORDER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문 상태가 대기 중인 경우만 수락을 할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "COMPLETED", "DELIVERED", "DELIVERING", "SERVED"})
    void accept_by_not_watting_status(final OrderStatus orderStatus) {
        final Order order = createOrder(DEFAULT_ORDER_ID, orderStatus, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");
        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(DEFAULT_ORDER_ID));
    }

    @DisplayName("메뉴를 제공할 수 있다")
    @Test
    void serve() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.ACCEPTED, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");
        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        final Order result = orderService.serve(DEFAULT_ORDER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("수락한 주문만 제공할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "COMPLETED", "DELIVERED", "DELIVERING", "SERVED"})
    void serve_with_not_accept_status(final OrderStatus orderStatus) {
        final Order order = createOrder(DEFAULT_ORDER_ID, orderStatus, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");
        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(DEFAULT_ORDER_ID));
    }

    @DisplayName("주문 배송 완료 처리를 할 수 있다")
    @Test
    void complete_delivery() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.DELIVERING, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");
        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        final Order result = orderService.completeDelivery(DEFAULT_ORDER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문의 상태가 배송 중인 경우에만 배송 완료 처리가 가능하다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "COMPLETED", "DELIVERED", "SERVED"})
    void complete_delivery_with_not_deliverd_status(final OrderStatus orderStatus) {
        final Order order = createOrder(DEFAULT_ORDER_ID, orderStatus, OrderType.TAKEOUT, defaultOrderTable(), defaultOrderLineItems(), "");
        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.completeDelivery(DEFAULT_ORDER_ID));
    }

    @DisplayName("배달이라면 배달 주소가 필수이다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_delivery_order_without_address1(final String address) {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.DELIVERY, defaultOrderTable(), defaultOrderLineItems(), address);

        given(menuRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(Arrays.asList(defaultMenu()));
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenu()));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("매장 내 식사가 아닌경우 주문 수량이 음수 일 수 없다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    void create_order_lower_than_zero_quantity(final OrderType orderType) {
        List<OrderLineItem> orderLineItems = defaultOrderLineItems();
        orderLineItems.stream()
                .forEach(orderLineItem -> orderLineItem.setQuantity(-1));
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, orderType, defaultOrderTable(), orderLineItems, "address");

        given(menuRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(Arrays.asList(defaultMenu()));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("메뉴 제공 상태일 때 배송 시작이 가능하다")
    @Test
    void start_delivery() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.SERVED, OrderType.DELIVERY, defaultOrderTable(), defaultOrderLineItems(), "address");

        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        final Order result = orderService.startDelivery(DEFAULT_ORDER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배송 시작 상태일 때 배송 완료 처리가 가능하다")
    @Test
    void complete_delivering() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.DELIVERING, OrderType.DELIVERY, defaultOrderTable(), defaultOrderLineItems(), "address");

        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        final Order result = orderService.completeDelivery(DEFAULT_ORDER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달 완료 상태 일 때 완료 처리가 가능하다")
    @Test
    void complete_order() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.DELIVERED, OrderType.DELIVERY, defaultOrderTable(), defaultOrderLineItems(), "address");

        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        final Order result = orderService.complete(DEFAULT_ORDER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장 주문일때 주문 테이블이 존재해야한다")
    @Test
    void create_eat_in_order_with_no_such_order_table() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.EAT_IN, defaultOrderTable(), defaultOrderLineItems(), "address");

        given(menuRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(Arrays.asList(defaultMenu()));
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenu()));
        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.empty());

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("매장 주문일때 주문 테이블이 비어있어야한다")
    @Test
    void create_eat_in_order_with_empty_order_table() {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.WAITING, OrderType.EAT_IN, defaultOrderTable(), defaultOrderLineItems(), "address");

        given(menuRepository.findAllByIdIn(Mockito.any(List.class)))
                .willReturn(Arrays.asList(defaultMenu()));
        given(menuRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(defaultMenu()));
        OrderTable orderTable = defaultOrderTable();
        orderTable.setOccupied(false);
        given(orderTableRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(orderTable));

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("매장 주문이거나, 포장 주문일 경우 상품 제공 상태일때 완료 처리가 가능하다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
    void complete_eat_in_and_take_out_order(final OrderType orderType) {
        final Order order = createOrder(DEFAULT_ORDER_ID, OrderStatus.SERVED, orderType, defaultOrderTable(), defaultOrderLineItems(), "address");

        given(orderRepository.findById(Mockito.any(UUID.class)))
                .willReturn(Optional.of(order));

        final Order result = orderService.complete(DEFAULT_ORDER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        if (orderType == OrderType.EAT_IN) {
            assertThat(result.getOrderTable().isOccupied()).isFalse();
            assertThat(result.getOrderTable().getNumberOfGuests()).isZero();
        }
    }

}