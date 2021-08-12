package kitchenpos.application;

import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MenuRepository menuRepository;

    @Mock
    OrderTableRepository orderTableRepository;

    @Mock
    KitchenridersClient kitchenridersClient;

    @InjectMocks
    OrderService orderService;

    private static final String ORDER_TYPE_IS_NULL = "주문 타입이 지정되어야 합니다.";
    private static final String ORDER_LINE_ITEMS_IS_NULL_OR_EMPTY = "주문 상품이 없습니다.";
    private static final String NON_EXISTENCE_MENU = "존재하지 않는 메뉴는 주문할 수 없습니다.";
    private static final String HIDDEN_MENU = "숨겨진 메뉴는 주문할 수 없습니다.";
    private static final String INCORRECT_PRICE = "가격 정보가 올바르지 않습니다.";
    private static final String DELIVERY_ADDRESS_ILLEGAL = "배달지 정보가 없습니다.";
    private static final String EMPTY_ORDER_TABLE = "주문 테이블이 비어있습니다.";
    private static final String INVALID_ORDER_LIST_ITEM_QUANTITY = "주문 상품 수량이 올바르지 않습니다.";
    private static final String ORDER_STATUS_IS_DELIVERED = "배달이 완료된 상태여야 합니다.";
    private static final String ORDER_STATUS_IS_SERVED = "서빙이 완료된 상태여야 합니다.";

    @DisplayName("주문한다.")
    @Test
    void create() {
        //given
        Order order = OrderFixture.generateOrder();

        Menu menu = order.getOrderLineItems().get(0).getMenu();
        doReturn(Optional.of(menu)).when(menuRepository).findById(any());

        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());
        doReturn(menus).when(menuRepository).findAllById(any());
        doReturn(Optional.of(order.getOrderTable())).when(orderTableRepository).findById(order.getOrderTableId());
        when(orderRepository.save(any(Order.class))).then(AdditionalAnswers.returnsFirstArg());

        //when
        Order createdOrder = orderService.create(order);

        //then
        assertAll(
                () -> assertThat(createdOrder).isNotNull(),
                () -> assertThat(createdOrder.getId()).isNotNull(),
                () -> assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(createdOrder.getDeliveryAddress()).isEqualTo(order.getDeliveryAddress()),
                () -> assertThat(createdOrder.getOrderTable()).isEqualTo(order.getOrderTable())
        );
    }

    @DisplayName("주문 실패 - 주문 타입 미지정")
    @Test
    void create_fail_empty_orderType() {
        //given
        Order order = OrderFixture.generateOrder();
        order.setType(null);

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(ORDER_TYPE_IS_NULL);
    }

    @DisplayName("주문 실패 - 주문 상품 미지정")
    @NullAndEmptySource
    @ParameterizedTest
    void create_fail_null_or_empty_orderLineItems(List<OrderLineItem> orderLineItems) {
        //given
        Order order = OrderFixture.generateOrder();
        order.setOrderLineItems(orderLineItems);

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(ORDER_LINE_ITEMS_IS_NULL_OR_EMPTY);
    }

    @DisplayName("주문 실패 - 조회한 메뉴 갯수 차이 : 존재하지 않는 메뉴가 있다.")
    @Test
    void create_fail_non_existence_menu_different_menu_size() {
        //given
        Order order = OrderFixture.generateOrder();
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());
        menus.remove(0);

        doReturn(menus).when(menuRepository).findAllById(any());

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(NON_EXISTENCE_MENU);
    }

    @DisplayName("주문 실패 - 존재하지 않는 메뉴가 있다.")
    @Test
    void create_fail_non_existence_menu() {
        //given
        Order order = OrderFixture.generateOrder();
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());

        doReturn(menus).when(menuRepository).findAllById(any());
        doReturn(Optional.empty()).when(menuRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 실패 - 모든 메뉴가 전시된 상태여야 한다.")
    @Test
    void create_fail_hidden_menu() {
        //given
        Order order = OrderFixture.generateOrder();
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .peek(menu -> menu.setDisplayed(false))
                .collect(Collectors.toList());

        doReturn(menus).when(menuRepository).findAllById(any());
        doReturn(Optional.of(menus.get(0))).when(menuRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(HIDDEN_MENU);
    }

    @DisplayName("주문 실패 - 메뉴 가격과 주문 상품의 가격이 다르다.")
    @Test
    void create_fail_different_price() {
        //given
        Order order = OrderFixture.generateOrder();
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());
        Menu menu = order.getOrderLineItems().get(0).getMenu();
        order.getOrderLineItems().get(0).getMenu().setPrice(menu.getPrice().add(BigDecimal.TEN));

        doReturn(menus).when(menuRepository).findAllById(any());
        doReturn(Optional.of(menu)).when(menuRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(INCORRECT_PRICE);
    }

    @DisplayName("주문 실패 - 주문 타입이 배달인 경우, 배달 주소는 필수이다.")
    @NullAndEmptySource
    @ParameterizedTest
    void creat_fail_empty_deliveryAddress(String address) {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setDeliveryAddress(address);
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());
        Menu menu = order.getOrderLineItems().get(0).getMenu();

        doReturn(menus).when(menuRepository).findAllById(any());
        doReturn(Optional.of(menu)).when(menuRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(DELIVERY_ADDRESS_ILLEGAL);
    }

    @DisplayName("주문 실패 - 주문 타입이 매장 내 식사인 경우, 주문 테이블이 존재해야 한다.")
    @Test
    void create_fail_null_orderTable() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.EAT_IN);
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());
        Menu menu = order.getOrderLineItems().get(0).getMenu();

        doReturn(menus).when(menuRepository).findAllById(any());
        doReturn(Optional.of(menu)).when(menuRepository).findById(any());
        doReturn(Optional.empty()).when(orderTableRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문 실패 - 주문 타입이 매장 내 식사인 경우, 주문 테이블이 비어있으면 안된다.")
    @Test
    void create_fail_empty_orderTable() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.EAT_IN);
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());
        Menu menu = order.getOrderLineItems().get(0).getMenu();
        order.getOrderTable().setEmpty(true);

        doReturn(menus).when(menuRepository).findAllById(any());
        doReturn(Optional.of(menu)).when(menuRepository).findById(any());
        doReturn(Optional.of(order.getOrderTable())).when(orderTableRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(EMPTY_ORDER_TABLE);
    }

    @DisplayName("주문 실패 - 주문 타입이 배달, 포장인 경우 주문 상품 수량은 0이상이어야 한다.")
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT"})
    @ParameterizedTest
    void create_fail_Illegal_quantity(OrderType type) {
        //given
        Order order = OrderFixture.generateOrder(type);
        List<OrderLineItem> orderLineItems = order.getOrderLineItems().stream()
                .peek(orderLineItem -> orderLineItem.setQuantity(-10_000L))
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLineItems);
        List<Menu> menus = order.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());

        doReturn(menus).when(menuRepository).findAllById(any());

        //when & then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderService.create(order))
                .withMessage(INVALID_ORDER_LIST_ITEM_QUANTITY);
    }

    @DisplayName("주문을 승인")
    @Test
    void accept() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when
        Order acceptedOrder = orderService.accept(order.getId());

        //then
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문을 승인 - 주문 타입이 배달인 경우, 주문 정보(주문 가격, 배달지 주소)를 배달기사에게 전달한다.")
    @Test
    void accept_ORDER_TYPE_DELIVERY() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderLineItem orderLineItem : order.getOrderLineItems()) {
            totalPrice = totalPrice.add(
                    orderLineItem.getMenu()
                            .getPrice()
                            .multiply(BigDecimal.valueOf(orderLineItem.getQuantity()))
            );
        }

        System.out.println(totalPrice);
        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when
        Order acceptedOrder = orderService.accept(order.getId());

        //then
        verify(kitchenridersClient).requestDelivery(order.getId(), totalPrice, order.getDeliveryAddress());
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문 승인 실패 - 존재하지 않는 주문")
    @Test
    void accept_fail_non_existence_order() {
        //given
        Order order = OrderFixture.generateOrder();

        doReturn(Optional.empty()).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("주문 승인 실패 - 대기 상태인 주문이어야 한다.")
    @Test
    void accept_fail_orderType_is_not_waiting() {
        //given
        Order order = OrderFixture.generateOrder();
        order.setStatus(OrderStatus.COMPLETED);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("주문을 서빙")
    @Test
    void serve() {
        //given
        Order order = OrderFixture.generateOrder();
        order.setStatus(OrderStatus.ACCEPTED);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when
        Order servedOrder = orderService.serve(order.getId());

        //then
        assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문 서빙 실패 - 존재하지 않는 주문")
    @Test
    void serve_fail_non_existence_order() {
        //given
        Order order = OrderFixture.generateOrder();
        order.setStatus(OrderStatus.ACCEPTED);

        doReturn(Optional.empty()).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("주문 서빙 실패 - 주문은 승인된 상태여야 한다.")
    @Test
    void serve_fail_order_state_is_not_waiting() {
        //given
        Order order = OrderFixture.generateOrder();
        order.setStatus(OrderStatus.WAITING);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.serve(order.getId()));
    }

    @DisplayName("배달을 시작")
    @Test
    void startDelivery() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when
        Order actual = orderService.startDelivery(order.getId());

        //then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 시작 실패 - 존재하지 않는 주문")
    @Test
    void start_Delivery_fail_non_existence_order() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);

        doReturn(Optional.empty()).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("배달 시작 실패 - 주문 타입이 배달이어야 한다.")
    @Test
    void start_Delivery_fail_order_type_is_not_delivery() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("배달 시작 실패 -  주문이 준비 완료된 상태여야 한다.")
    @Test
    void start_Delivery_fail_order_status_is_not_served() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.startDelivery(order.getId()));
    }

    @DisplayName("배달을 완료")
    @Test
    void completeDelivery() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when
        Order deliveredOrder = orderService.completeDelivery(order.getId());

        //then
        assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달 완료 실패 - 존재하지 않는 주문")
    @Test
    void complete_delivery_fail_non_existence_order() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);

        doReturn(Optional.empty()).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("배달 완료 실패 - 주문 타입이 배달이어야 한다.")
    @Test
    void complete_delivery_fail_order_type_is_not_delivery() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.EAT_IN);
        order.setStatus(OrderStatus.DELIVERING);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("배달 완료 실패 - 주문이 배달 중 상태여야 한다.")
    @Test
    void complete_delivery_fail_order_status_is_not_delivering() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.completeDelivery(order.getId()));
    }

    @DisplayName("주문 처리 완료 - 매장 내 식사")
    @Test
    void complete_order_type_is_EAT_IN() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.EAT_IN);
        order.setStatus(OrderStatus.SERVED);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());
        doReturn(false).when(orderRepository).existsByOrderTableAndStatusNot(any(), any());

        //when
        Order completeOrder = orderService.complete(order.getId());

        //then
        OrderTable orderTable = completeOrder.getOrderTable();
        assertAll(
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                () -> assertThat(orderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문 처리 완료 - 포장, 배달")
    @EnumSource(value = OrderType.class, names = {"TAKEOUT", "DELIVERY"})
    @ParameterizedTest
    void complete_order_type_is_TAKEOUT_or_DELIVERY(OrderType type) {
        Order order = OrderFixture.generateOrder(type);
        if (type.equals(OrderType.DELIVERY)) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            order.setStatus(OrderStatus.SERVED);
        }

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when
        Order completeOrder = orderService.complete(order.getId());

        //then
        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 처리 완료 실패 - 존재하지 않는 주문")
    @Test
    void complete_fail_non_existence_order() {
        //given
        Order order = OrderFixture.generateOrder();

        doReturn(Optional.empty()).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderService.complete(order.getId()));
    }

    @DisplayName("주문 처리 완료 실패 - 주문 타입이 배달인 경우, 배달 완료된 상태여야 한다.")
    @Test
    void complete_fail_order_status_is_not_delivered() {
        //given
        Order order = OrderFixture.generateOrder(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.complete(order.getId()))
                .withMessage(ORDER_STATUS_IS_DELIVERED);
    }

    @DisplayName("주문 처리 완료 실패 - 주문 타입이 포장, 매장 내 식사인 경우, 주문이 서빙 완료 상태여야 한다.")
    @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
    @ParameterizedTest
    void complete_fail_order_status_is_not_served(OrderType type) {
        //given
        Order order = OrderFixture.generateOrder(type);
        order.setStatus(OrderStatus.WAITING);

        doReturn(Optional.of(order)).when(orderRepository).findById(any());

        //when & then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderService.complete(order.getId()))
                .withMessage(ORDER_STATUS_IS_SERVED);
    }

    @DisplayName("모든 주문 목록 조회")
    @Test
    void findAll() {
        //given
        Order order01 = OrderFixture.generateOrder();
        Order order02 = OrderFixture.generateOrder(OrderType.DELIVERY);
        Order order03 = OrderFixture.generateOrder(OrderType.TAKEOUT);
        List<Order> orders = Arrays.asList(order01, order02, order03);

        doReturn(orders).when(orderRepository).findAll();

        //when
        List<Order> actual = orderService.findAll();

        //then
        assertThat(actual).hasSize(3);
    }
}
