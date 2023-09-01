package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.BDDMockito;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.domain.MenuFixture.MenuGroupFixture.한마리메뉴;
import static kitchenpos.domain.MenuFixture.MenuProductFixture.메뉴상품_후라이드;
import static kitchenpos.domain.ProductFixture.후라이드;
import static kitchenpos.exception.OrderExceptionMessage.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;

class OrderServiceTest {

    private final OrderRepository orderRepository = new FakeOrderRepository();
    private final MenuRepository menuRepository = new FakeMenuRepository();
    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();
    private final KitchenridersClient kitchenridersClient = spy(KitchenridersClient.class);
    private final OrderService service = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);


    @DisplayName("주문생성시 주문유형이 null 이면 예외를 발생시킨다.")
    @Test
    void create_orderType_null() {
        Order order = createOrderBuilder().build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ORDER_TYPE_NULL);
    }

    private OrderBuilder createOrderBuilder() {
        return new OrderBuilder();
    }

    @DisplayName("주문생성시 주문아이템이 존재하지 않으면 예외를 발생시킨다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_orderLineItem_empty(List<OrderLineItem> itemList) {
        Order order = createOrderBuilder()
                .type(OrderType.EAT_IN)
                .orderLineItems(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ORDER_LINE_ITEM_EMPTY);
    }

    @DisplayName("주문아이템의 메뉴가 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void create_not_found_menu() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu menu = menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true);

        Order order = createOrderBuilder()
                .type(OrderType.EAT_IN)
                .orderLineItems(List.of(createOrderLineItem(menu, BigDecimal.valueOf(15000), 1)))
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_FOUND_MENU);
    }

    @DisplayName("매장식사 주문이 아닐때, 주문아이템의 수량이 음수면 예외를 반환한다.")
    @Test
    void create_quantity_less_zero() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), -1)))
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ORDER_LINE_ITEM_QUANTITY_NEGATIVE);
    }

    @DisplayName("주문아이템의 메뉴가 비노출상태면 예외를 발생시킨다.")
    @Test
    void create_not_display() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), false)
        );
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), 1)))
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_LINE_ITEM_MENU_NOT_DISPLAY);
    }

    @DisplayName("메뉴가격이 주문아이템의 가격과 다르면 예외를 발생시킨다.")
    @Test
    void create_price() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(14000), 1)))
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NOT_EQUALS_PRICE);
    }

    @DisplayName("주문유형이 배달일때, 배송지 주소가 없으면 예외를 발생시킨다.")
    @Test
    void create_delivery_address() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), 1)))
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(DELIVERY_ADDRESS_EMPTY);
    }

    @DisplayName("주문유형이 매장식사일때, 주문테이블이 없으면 예외를 발생시킨다.")
    @Test
    void create_eatIn_not_found_orderTable() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        Order order = createOrderBuilder()
                .type(OrderType.EAT_IN)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), 1)))
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_ORDER_TABLE);
    }

    // occupied(가득차있는) 가 아니면 예외가 발생한다??
    @DisplayName("주문유형이 매장식사일때, 주문테이블이 비어있지 않으면 예외를 발생시킨다.")
    @Test
    void create_eatIn_orderTable_not_occupied() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        OrderTable savedOrderTable = orderTableRepository.save(createOrderTable("1번테이블", false, 10));
        Order order = createOrderBuilder()
                .type(OrderType.EAT_IN)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), 1)))
                .orderTable(savedOrderTable)
                .orderTableId(savedOrderTable.getId())
                .build();

        assertThatThrownBy(() -> service.create(order))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(NOT_OCCUPIED_ORDER_TABLE);
    }

    @DisplayName("주문수락시 주문이 존재하지 않으면 예외를 발생시킨다.")
    @Test
    void accept_not_found_order() {
        assertThatThrownBy(() -> service.accept(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_ORDER);
    }

    @DisplayName("주문수락시 주문 상태가 대기중이 아니면 예외 반환")
    @Test
    void accept_not_waiting() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        OrderTable savedOrderTable = orderTableRepository.save(createOrderTable("1번테이블", true, 10));
        Order order = createOrderBuilder()
                .type(OrderType.EAT_IN)
                .status(OrderStatus.ACCEPTED)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), 1)))
                .orderTable(savedOrderTable)
                .orderTableId(savedOrderTable.getId())
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_STATUS_NOT_WAITING);
    }

    @DisplayName("주문수락시 주문유형이 배달이면 배달을 요청한다.")
    @Test
    void accept_delivery_request_delivery() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        OrderTable savedOrderTable = orderTableRepository.save(createOrderTable("1번테이블", true, 10));
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .status(OrderStatus.WAITING)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), 1)))
                .orderTable(savedOrderTable)
                .orderTableId(savedOrderTable.getId())
                .build();
        orderRepository.save(order);

        service.accept(order.getId());

        BDDMockito.verify(kitchenridersClient, atLeastOnce()).requestDelivery(any(), any(), any());
    }

    @DisplayName("주문수락성공하면 주문상태가 수락됨으로 변경된다.")
    @Test
    void accept_success() {
        MenuServiceTest menuServiceTest = new MenuServiceTest();
        Menu savedMenu = menuRepository.save(
                menuServiceTest.createMenu(15000, "치킨메뉴", 한마리메뉴(), List.of(메뉴상품_후라이드(후라이드())), true)
        );
        OrderTable savedOrderTable = orderTableRepository.save(createOrderTable("1번테이블", true, 10));
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .status(OrderStatus.WAITING)
                .orderLineItems(List.of(createOrderLineItem(savedMenu, BigDecimal.valueOf(15000), 1)))
                .orderTable(savedOrderTable)
                .orderTableId(savedOrderTable.getId())
                .build();
        orderRepository.save(order);

        Order result = service.accept(order.getId());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문제공시 주문이 없으면 예외를 발생시킨다.")
    @Test
    void serve_not_found_order() {
        assertThatThrownBy(() -> service.serve(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_ORDER);
    }

    @DisplayName("주문제공시 주문상태가 수락됨이 아니면 예외를 발생시킨다.")
    @Test
    void serve_not_accepted() {
        Order order = createOrderBuilder()
                .status(OrderStatus.WAITING)
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_STATUS_NOT_ACCEPTED);
    }

    @DisplayName("주문제공 성공하면 주문상태를 제공됨으로 변경한다.")
    @Test
    void serve_success() {
        Order order = createOrderBuilder()
                .status(OrderStatus.ACCEPTED)
                .build();
        orderRepository.save(order);

        Order result = service.serve(order.getId());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문배달 시작할때 주문이 없으면 예외를 발생시킨다.")
    @Test
    void start_delivery_not_found_order() {
        assertThatThrownBy(() -> service.startDelivery(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage(NOT_FOUND_ORDER);
    }

    @DisplayName("주문배달 시작할때 주문유형이 배달이 아니면 예외를 발생시킨다.")
    @Test
    void start_delivery_no_delivery() {
        Order order = createOrderBuilder()
                .type(OrderType.TAKEOUT)
                .status(OrderStatus.ACCEPTED)
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_TYPE_NOT_DELIVERY);
    }

    @DisplayName("주문배달 시작할때 주문유형이 배달이 아니면 예외를 발생시킨다.")
    @Test
    void start_delivery_no_served() {
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .status(OrderStatus.WAITING)
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_STATUS_NOT_SERVED);
    }

    @DisplayName("주문배달 시작을 성공하면 배달중 상태로 변경한다.")
    @Test
    void start_delivery_success() {
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .status(OrderStatus.SERVED)
                .build();
        orderRepository.save(order);

        Order result = service.startDelivery(order.getId());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문배달완료시 주문상태가 배달중이 아니면 예외를 발생시킨다.")
    @Test
    void complete_delivery_not_delivering() {
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .status(OrderStatus.SERVED)
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_STATUS_NOT_DELIVERING);
    }

    @DisplayName("주문배달완료 성공하면 배달됨 상태로 변경한다.")
    @Test
    void complete_delivery_success() {
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .status(OrderStatus.DELIVERING)
                .build();
        orderRepository.save(order);

        Order result = service.completeDelivery(order.getId());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문완료 할때, 배달 주문이 배달됨 상태가 아니면 예외를 발생시킨다.")
    @Test
    void complete_not_delivered() {
        Order order = createOrderBuilder()
                .type(OrderType.DELIVERY)
                .status(OrderStatus.DELIVERING)
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_STATUS_NOT_DELIVERED);
    }

    @DisplayName("주문완료 할때, 매장식사나 포장주문이 제공됨 상태가 아니면 예외를 발생시킨다.")
    @Test
    void complete_not_served() {
        Order order = createOrderBuilder()
                .type(OrderType.EAT_IN)
                .status(OrderStatus.WAITING)
                .build();
        orderRepository.save(order);

        assertThatThrownBy(() -> service.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ORDER_STATUS_NOT_SERVED);
    }

    @DisplayName("주문완료하면 주문상태가 완료됨으로 변경된다.")
    @Test
    void complete_success_status_completed() {
        Order order = createOrderBuilder()
                .type(OrderType.TAKEOUT)
                .status(OrderStatus.SERVED)
                .build();
        orderRepository.save(order);

        Order result = service.complete(order.getId());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장식사 주문이 완료되면 주문테이블을 비운다.")
    @Test
    void complete_success_clear_order_table() {
        OrderTable savedOrderTable = orderTableRepository.save(createOrderTable("1번테이블", true, 10));
        Order order = createOrderBuilder()
                .type(OrderType.EAT_IN)
                .status(OrderStatus.SERVED)
                .orderTable(savedOrderTable)
                .orderTableId(savedOrderTable.getId())
                .build();
        orderRepository.save(order);

        Order resultOrder = service.complete(order.getId());
        OrderTable result = resultOrder.getOrderTable();

        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isEqualTo(false);
    }


    public static OrderLineItem createOrderLineItem(Menu menu, BigDecimal price, Integer quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static OrderTable createOrderTable(String name, boolean occupied, Integer numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

}
