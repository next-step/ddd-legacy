package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {OrderService.class})
class OrderServiceTest {
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private MenuRepository menuRepository;
    @MockBean
    private OrderTableRepository orderTableRepository;
    @MockBean
    private KitchenridersClient kitchenridersClient;
    @Autowired
    private OrderService orderService;

    private UUID id;
    private List<Menu> menus;
    private List<OrderLineItem> orderLineItems;
    private long menuPrice;
    private Menu menu;
    private UUID orderTableId;
    private Order createRequest;
    private static String deliveryAddress = "주문주소";

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        menuPrice = 1000L;
        menu = getMenu(true, menuPrice);
        menus = getMenus(menu);
        orderLineItems = getOrderLineItems(menus);
        orderTableId = UUID.randomUUID();
        createRequest = createRequest(OrderType.TAKEOUT, null, null, orderLineItems);
    }

    @MethodSource(value = "getOrderValues")
    @ParameterizedTest
    @DisplayName("주문을 생성한다.")
    void create(OrderType orderType, String deliveryAddress, UUID orderTableId) {
        Order request = createRequest(orderType, deliveryAddress, orderTableId, orderLineItems);
        givenToCreate(menus, menu, getOrderTable(false));
        given(orderRepository.save(any())).willAnswer(returnsFirstArg());


        Order order = orderService.create(request);

        assertAll(
                () -> assertNotNull(order.getId()),
                () -> assertEquals(order.getType(), orderType),
                () -> assertEquals(order.getStatus(), OrderStatus.WAITING)
        );
    }

    @NullSource
    @ParameterizedTest
    @DisplayName("주문 생성시 타입은 필수다.")
    void create_valid_type(OrderType type) {
        Order request = createRequest(type, null, null, null);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @NullAndEmptySource
    @ParameterizedTest
    @DisplayName("주문 생성시 메뉴 정보는 필수이다.")
    void create_valid_orderLineItem(List<OrderLineItem> orderLineItems) {
        Order request = createRequest(OrderType.TAKEOUT, null, null, orderLineItems);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 생성시 요청한 메뉴와 조회한 메뉴의 수가 같아야 한다.")
    void create_valid_menuSize() {
        givenToCreate(Collections.emptyList(), menu, getOrderTable(false));

        assertThatThrownBy(() -> orderService.create(createRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 생성시 요청한 메뉴는 기등록된 메뉴여야한다.")
    void create_exit_menu() {
        givenToCreate(menus, null, getOrderTable(false));

        assertThatThrownBy(() -> orderService.create(createRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문 생성시 매장 내 식사시 주문 수량은 0 이상이어야 한다.")
    void create_valid_eatIn_quantity() {
        List<OrderLineItem> orderLineItems = getOrderLineItems(menus, -1);
        Order request = createRequest(OrderType.TAKEOUT, null, null, orderLineItems);
        givenToCreate(menus, null, null);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 생성시 노출된 메뉴만 주문 가능하다.")
    void create_valid_menu_isDisplayed() {
        givenToCreate(menus, getMenu(false, menuPrice), null);

        assertThatThrownBy(() -> orderService.create(createRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 생성시 요청한 메뉴 가격과 실제 메뉴 가격이 일치해야한다.")
    void create_valid_menu_price() {
        givenToCreate(menus, getMenu(false, 100L), null);

        assertThatThrownBy(() -> orderService.create(createRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @NullAndEmptySource
    @ParameterizedTest
    @DisplayName("배달 타입 주문시 배송정보는 필수이다.")
    void create_valid_deliveryType(String deliveryAddress) {
        Order request = createRequest(OrderType.DELIVERY, deliveryAddress, null, orderLineItems);
        givenToCreate(menus, menu, null);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("매장 내 식사 타입 주문시 테이블 정보는 필수이다.")
    void create_valid_eatInType() {
        Order request = createRequest(OrderType.EAT_IN, null, orderTableId, orderLineItems);
        givenToCreate(menus, menu, null);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("매장 내 식사 타입 주문시 주문테이블은 착석처리 되어있어야 한다.")
    void create_valid_eatInType_table_isEmpty() {
        Order request = createRequest(OrderType.EAT_IN, null, orderTableId, orderLineItems);
        givenToCreate(menus, menu, getOrderTable(true));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @MethodSource(value = "getOrderValues")
    @ParameterizedTest
    @DisplayName("주문 승인처리 한다.")
    void accept(OrderType orderType, String deliveryAddress, UUID orderTableId) {
        Order waitingOrder = createOrder(orderType, deliveryAddress, orderTableId, OrderStatus.WAITING);
        given(orderRepository.findById(any())).willReturn(Optional.of(waitingOrder));

        Order order = orderService.accept(waitingOrder.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("존재하는 주문만 승인처리 가능하다.")
    void accept_exit_order() {
        Order waitingOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, OrderStatus.WAITING);
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.accept(waitingOrder.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문을 수락할때, 주문 상태는 대기상태다.")
    void accept_valid_Status() {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus == OrderStatus.WAITING) {
                continue;
            }

            Order waitingOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, orderStatus);
            given(orderRepository.findById(any())).willReturn(Optional.of(waitingOrder));

            assertThatThrownBy(() -> orderService.accept(waitingOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("배달 주문일 경우, 배달 서비스에 배달 요청한다.")
    void accept_request_ridersClient() {
        Order waitingOrder = createOrder(OrderType.DELIVERY, deliveryAddress, null, OrderStatus.WAITING);
        given(orderRepository.findById(any())).willReturn(Optional.of(waitingOrder));

        orderService.accept(waitingOrder.getId());

        verify(kitchenridersClient, atLeastOnce()).requestDelivery(any(), any(), any());
    }

    @MethodSource(value = "getOrderValues")
    @ParameterizedTest
    @DisplayName("주문 제공처리 한다.")
    void serve(OrderType orderType, String deliveryAddress, UUID orderTableId) {
        Order acceptedOrder = createOrder(orderType, deliveryAddress, orderTableId, OrderStatus.ACCEPTED);
        given(orderRepository.findById(any())).willReturn(Optional.of(acceptedOrder));

        Order order = orderService.serve(acceptedOrder.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("존재하는 주문만 제공처리 한다.")
    void serve_exit_order() {
        Order acceptedOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, OrderStatus.ACCEPTED);
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.serve(acceptedOrder.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품 제공 처리할때, 주문 상태는 수락상태다.")
    void serve_valid_Status() {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus == OrderStatus.ACCEPTED) {
                continue;
            }

            Order acceptedOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, orderStatus);
            given(orderRepository.findById(any())).willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.serve(acceptedOrder.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Test
    @DisplayName("주문 배달을 시작한다.")
    void startDelivery() {
        Order servedOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.of(servedOrder));

        Order order = orderService.startDelivery(servedOrder.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("존재하는 주문만 배달을 시작한다.")
    void startDelivery_exit_order() {
        Order servedOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.startDelivery(servedOrder.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문 배달 시작 가능한 주문타입은 배달 타입이다.")
    void startDelivery_valid_type() {
        for (OrderType orderType : OrderType.values()) {
            if (orderType == OrderType.DELIVERY) {
                continue;
            }

            Order servedOrder = createOrder(orderType, deliveryAddress, orderTableId, OrderStatus.SERVED);
            given(orderRepository.findById(any())).willReturn(Optional.of(servedOrder));

            assertThatThrownBy(() -> orderService.startDelivery(servedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("주문 배달을 시작할때, 주문 상태는 제공상태이다.")
    void startDelivery_valid_status() {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus == OrderStatus.SERVED) {
                continue;
            }

            Order servedOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, orderStatus);
            given(orderRepository.findById(any())).willReturn(Optional.of(servedOrder));

            assertThatThrownBy(() -> orderService.startDelivery(servedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("주문 배달을 완료한다.")
    void completeDelivery() {
        Order deliveringOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, OrderStatus.DELIVERING);
        given(orderRepository.findById(any())).willReturn(Optional.of(deliveringOrder));

        Order order = orderService.completeDelivery(deliveringOrder.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("존재하는 주문만 배달 완료 가능하다.")
    void completeDelivery_exit_order() {
        Order deliveringOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, OrderStatus.DELIVERING);
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.completeDelivery(deliveringOrder.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("주문 배달 완료 가능한 주문타입은 배달중 타입이다.")
    void completeDelivery_valid_type() {
        for (OrderType orderType : OrderType.values()) {
            if (orderType == OrderType.DELIVERY) {
                continue;
            }

            Order deliveringOrder = createOrder(orderType, deliveryAddress, orderTableId, OrderStatus.SERVED);
            given(orderRepository.findById(any())).willReturn(Optional.of(deliveringOrder));

            assertThatThrownBy(() -> orderService.completeDelivery(deliveringOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("주문 배달 완료 가능한 주문상태는 배달중 상태이다.")
    void completeDelivery_valid_status() {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus == OrderStatus.DELIVERING) {
                continue;
            }

            Order deliveringOrder = createOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, orderStatus);
            given(orderRepository.findById(any())).willReturn(Optional.of(deliveringOrder));

            assertThatThrownBy(() -> orderService.completeDelivery(deliveringOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @MethodSource(value = "getOrderValuesWithStatus")
    @ParameterizedTest
    @DisplayName("주문을 완료 처리한다.")
    void complete(OrderType orderType, String deliveryAddress, UUID orderTableId, OrderStatus orderStatus) {
        Order completeOrder = completeOrder(orderType, deliveryAddress, orderTableId, orderStatus);
        given(orderRepository.findById(any())).willReturn(Optional.of(completeOrder));

        Order order = orderService.complete(completeOrder.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("존재하는 주문만 완료처리 가능하다.")
    void complete_exit_order() {
        Order completeOrder = completeOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, OrderStatus.DELIVERED);
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.complete(completeOrder.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("배달타입 주문일 경우 주문 완료 가능한 주문 상태는 배달 완료 상태이다.")
    void complete_valid_delivery_type_status() {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus == OrderStatus.DELIVERED) {
                continue;
            }

            Order completeOrder = completeOrder(OrderType.DELIVERY, deliveryAddress, orderTableId, orderStatus);
            given(orderRepository.findById(any())).willReturn(Optional.of(completeOrder));

            assertThatThrownBy(() -> orderService.complete(completeOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @ValueSource(strings = {"TAKEOUT", "EAT_IN"})
    @ParameterizedTest
    @DisplayName("테이크아웃, 매장내식사타입 주문일 경우 주문 완료 가능한 주문 상태는 제공 상태이다.")
    void complete_valid_takeoutAndEatIn_type_status(OrderType orderType) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus == OrderStatus.SERVED) {
                continue;
            }

            Order completeOrder = completeOrder(orderType, deliveryAddress, orderTableId, orderStatus);
            given(orderRepository.findById(any())).willReturn(Optional.of(completeOrder));

            assertThatThrownBy(() -> orderService.complete(completeOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("매장내식사타입 주문일 경우 주문이 모두 처리 되었는지 확인 후 테이블을 비운다.")
    void name() {
        Order completeOrder = completeOrder(OrderType.EAT_IN, deliveryAddress, orderTableId, OrderStatus.SERVED);
        given(orderRepository.findById(any())).willReturn(Optional.of(completeOrder));

        orderService.complete(completeOrder.getId());

        OrderTable orderTable = completeOrder.getOrderTable();
        assertAll(
                () -> assertTrue(orderTable.isEmpty()),
                () -> assertEquals(orderTable.getNumberOfGuests(), 0)
        );
    }

    private void givenToCreate(List<Menu> menus, Menu menu, OrderTable orderTable) {
        given(menuRepository.findAllById(any())).willReturn(menus);
        given(menuRepository.findById(any())).willReturn(Optional.ofNullable(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.ofNullable(orderTable));
    }

    public Stream<Arguments> getOrderValues() {
        return Stream.of(
                Arguments.of(OrderType.DELIVERY, deliveryAddress, null),
                Arguments.of(OrderType.EAT_IN, null, UUID.randomUUID()),
                Arguments.of(OrderType.TAKEOUT, null, null)
        );
    }

    public Stream<Arguments> getOrderValuesWithStatus() {
        return Stream.of(
                Arguments.of(OrderType.DELIVERY, deliveryAddress, null, OrderStatus.DELIVERED),
                Arguments.of(OrderType.EAT_IN, null, UUID.randomUUID(), OrderStatus.SERVED),
                Arguments.of(OrderType.TAKEOUT, null, null, OrderStatus.SERVED)
        );
    }

    private Order completeOrder(OrderType orderType, String deliveryAddress, UUID orderTableId, OrderStatus orderStatus) {
        Order order = createOrder(orderType, deliveryAddress, orderTableId, orderStatus);
        order.setOrderTable(getOrderTable(false));
        return order;
    }

    private Order createOrder(OrderType orderType, String deliveryAddress, UUID orderTableId, OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableId);
        order.setStatus(orderStatus);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    private Order createRequest(OrderType orderType, String deliveryAddress, UUID orderTableId, List<OrderLineItem> orderLineItems) {
        Order request = new Order();
        request.setType(orderType);
        request.setOrderLineItems(orderLineItems);
        request.setDeliveryAddress(deliveryAddress);
        request.setOrderTableId(orderTableId);
        return request;
    }

    private List<OrderLineItem> getOrderLineItems(List<Menu> menus) {
        return getOrderLineItems(menus, 1);
    }

    private List<OrderLineItem> getOrderLineItems(List<Menu> menus, long quantity) {
        return menus.stream()
                .map(menu -> {
                    OrderLineItem orderLineItem = new OrderLineItem();
                    orderLineItem.setMenu(menu);
                    orderLineItem.setPrice(menu.getPrice());
                    orderLineItem.setQuantity(quantity);
                    return orderLineItem;
                }).collect(Collectors.toList());
    }

    private List<Menu> getMenus(Menu... menus) {
        return Arrays.asList(menus);
    }

    private Menu getMenu(boolean isDisPlayed, long price) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(isDisPlayed);
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

    private OrderTable getOrderTable(boolean isEmpty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableId);
        orderTable.setNumberOfGuests(3);
        orderTable.setEmpty(isEmpty);
        return orderTable;
    }
}
