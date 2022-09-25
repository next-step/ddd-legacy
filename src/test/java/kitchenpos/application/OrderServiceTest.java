package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.fake.FakeKitchenridersClient;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class OrderServiceTest {

    private static MenuRepository menuRepository;
    private static OrderRepository orderRepository;

    private static OrderTableRepository orderTableRepository;
    private static KitchenridersClient kitchenridersClien;

    private static OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        kitchenridersClien = new FakeKitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
                kitchenridersClien);
    }

    @Test
    @DisplayName("주문 생성시 주문 타입은 필수 이다.")
    void orderTypeIsEssential() {
        Order request = new Order();
        request.setType(null);

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @ParameterizedTest
    @DisplayName("주문 생성시 주문 항목은 1개 이상 있어야 한다.")
    @MethodSource("nullAndEmptyOrderLienItemList")
    void orderLineIsEssential(List<OrderLineItem> orderLineItems) {
        Order request = createDeliveryOrder();
        request.setOrderLineItems(orderLineItems);

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문 항목에 메뉴는 등록되어 있어야한다.")
    void orderItem_has_registMenu() {
        final Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.setMenuId(null);

        request.setOrderLineItems(List.of(orderLineItem));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));
    }


    @ParameterizedTest
    @MethodSource("notEatInOrder")
    @DisplayName("주문 유형이 매장 식사가 아닌 경우 주문 항목의 수량은 0개 이상 이어야 한다.")
    void not_EatInOrder_Is_Quantity_Not_LessThen_Zero(Order request) {
        List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(-1));
        request.setOrderLineItems(orderLineItems);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }


    @Test
    @DisplayName("등록된 메뉴만 주문 등록이 가능하다.")
    void createdMenuRegisteredOrder() {
        Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = new OrderLineItem();
        request.setOrderLineItems(List.of(orderLineItem));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("주문항목의 메뉴가 숨겨져 있으면 주문 등록을 할수가 없다")
    void orderItem_has_a_no_displayed_menu() {
        Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.getMenu().setDisplayed(false);
        request.setOrderLineItems(Collections.singletonList(orderLineItem));

        assertThatIllegalStateException().isThrownBy(() ->
                orderService.create(request)
        );
    }


    @ParameterizedTest
    @DisplayName("주문항목의 메뉴의 가격은 주문항목의 가격과 같아야 등록이 가능하다.")
    @ValueSource(ints = {-1, 2})
    void orderItemPrice_IsEqual_MenuPrice(int price) {
        Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.getMenu().setPrice(BigDecimal.valueOf(1));
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        request.setOrderLineItems(Collections.singletonList(orderLineItem));

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }


    @DisplayName("주문 유형이 배달주문일떄는 배달주소가 필수여야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void deliveryOrder_is_deliveryAddress_is_essential(String deliveryAddress) {
        Order request = createDeliveryOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        request.setDeliveryAddress(deliveryAddress);

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @DisplayName("등록할때 주문 유형이 매장식사인경우 주문 테이블이 반드시 필요하다.")
    @Test
    void eat_in_order_has_orderTable_essential() {
        Order request = createEatInOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문유형이 매장식사인 경우 사용중인 오더 테이블이 아닌경우 등록이 불가능하다.")
    @Test
    void eat_in_order_has_orderTable_is_no_occupied() {
        Order request = createEatInOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        OrderTable orderTable = createOrderTable();
        orderTable.setOccupied(false);
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());

        assertThatIllegalStateException().isThrownBy(
                () -> orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문을 등록 한다.")
    void create() {
        Order request = createEatInOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        OrderTable orderTable = createOrderTable();
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());

        Order createOrder = orderService.create(request);

        assertAll(
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getOrderTable().getId()).isEqualTo(orderTable.getId()),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @Test
    @DisplayName("존재하지 않는 주문을 수락 할수 없다.")
    void accept_is_existOrder() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.accept(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 상태가 대기 상태가 아닌 경우 수락을 할 수 없다.")
    void accept_before_status_is_waiting() {
        Order createOrder = createEatInOrder();
        createOrder.setStatus(OrderStatus.ACCEPTED);
        Order createdOrder = orderRepository.save(createOrder);

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.accept(createdOrder.getId()));
    }

    @Test
    @DisplayName("주문을 수락 한다.")
    void accept() {
        final Order createdOrder = 매장주문이_등록되어_있음();

        final Order acceptOrder = orderService.accept(createdOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("존재 하지 않은 주문은 주문 제공 할 수 없다.")
    void serve_is_exist_order() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.serve(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 상태가 접수 상태가 아닌 경우 주문 제공을 할 수 없다.")
    void serve_before_status_is_accepted() {
        final Order createdOrder = 매장주문이_등록되어_있음();

        assertThatIllegalStateException().isThrownBy(() ->
                orderService.serve(createdOrder.getId())
        );
    }

    @Test
    @DisplayName("주문을 제공 한다.")
    void serve() {
        Order createdOrder = 주문접수된_배달주문();

        final Order serve = orderService.serve(createdOrder.getId());

        assertThat(serve.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문이 등록 되어 있는 주문만 배달이 가능 하다.")
    void startDelivery_is_not_noSearchOrder() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.startDelivery(UUID.randomUUID()));
    }


    @Test
    @DisplayName("주문 유형이 배달이 아니면 배달중 상태로 변경이 불가능 하다.")
    void startDelivery_is_orderType_is_not_delivered() {
        Order createdOrder = 매장주문이_등록되어_있음();

        assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(createdOrder.getId())
        );
    }

    @Test
    @DisplayName("주문 제공 상태가 아니면 배달중 상태로 변경이 불 가능하다.")
    void startDelivery_is_beforeState_is_served() {
        Order createdOrder = 배달주문이_등록되어_있음();

        assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(createdOrder.getId())
        );
    }

    @Test
    @DisplayName("주문 제공 상태가 아니면 배달중 상태로 변경이 불가능하다.")
    void startDelivery() {
        Order createdOrder = 주문제공된_배달주문();

        final Order order = orderService.startDelivery(createdOrder.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("주문이 등록 되어 있는 주문만 배달완료가 가능 하다.")
    void completeDelivery_is_not_noSearchOrder() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()));
    }


    @Test
    @DisplayName("주문 유형이 배달이 아니면 배달완료 상태로 변경이 불가능 하다.")
    void completeDelivery_is_orderType_is_not_delivered() {
        Order createdOrder = 매장주문이_등록되어_있음();

        assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(createdOrder.getId())
        );
    }

    @Test
    @DisplayName("배달중 상태가 아니면 배달 완료 상태로 변경이 불가능하다.")
    void completeDelivery_is_beforeState_is_served() {
        Order createdOrder = 배달주문이_등록되어_있음();

        assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(createdOrder.getId())
        );
    }

    @Test
    @DisplayName("배달 완료 상태로 변경 된다.")
    void completeDelivery() {
        Order createdOrder = 배달시작된_배달주문();

        final Order order = orderService.completeDelivery(createdOrder.getId());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("주문이 등록 되어 있는 주문만 주문 완료 가능 하다.")
    void complete_is_not_noSearchOrder() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.complete(UUID.randomUUID()));
    }

    @Test
    @DisplayName("배달 주문은 배달 완료 상태에서만 주문 완료가 가능하다.")
    void deliveryOrder_complete_is_beforeState_is_delivered() {
        Order createdOrder = 배달주문이_등록되어_있음();

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(createdOrder.getId()));
    }

    @Test
    @DisplayName("포장 주문은 주문은 주문 제공 상태에서만 가능하다.")
    void takeOutOrder_is_beforeState_is_delivered() {
        Order createdOrder = 포장주문이_등록되어_있음();

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(createdOrder.getId()));
    }

    @Test
    @DisplayName("매장 주문은 주문은 주문 제공 상태에서만 가능하다.")
    void eatIn_is_beforeState_is_delivered() {
        Order createdOrder = 매장주문이_등록되어_있음();

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.complete(createdOrder.getId()));
    }


    @Test
    @DisplayName("배달 주문을 완료한다.")
    void deliveryOrderComplete() {
        final Order 배달완료된_배달주문 = 배달완료된_배달주문();

        final Order completeOrder = orderService.complete(배달완료된_배달주문.getId());

        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("포장주문을 완료한다.")
    void takeInOrderComplete() {
        Order order = 포장주문이_등록되어_있음();
        주문을_주문제공_상태까지_진행(order);

        final Order completeOrder = orderService.complete(order.getId());

        assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("매장주문을 완료한다.")
    void eatInOrderComplete() {
        Order order = 매장주문이_등록되어_있음();
        주문을_주문제공_상태까지_진행(order);

        final Order completeOrder = orderService.complete(order.getId());

        assertAll(
                () -> assertThat(completeOrder.getOrderTable().getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(completeOrder.getOrderTable().isOccupied()).isFalse(),
                () -> assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );
    }

    @Test
    @DisplayName("주문이 조회됨")
    void findAll() {
        Order deliveryOrder = 배달주문이_등록되어_있음();
        Order takeOutOrder = 포장주문이_등록되어_있음();
        Order eatInOrder = 매장주문이_등록되어_있음();

        final List<Order> orders = orderService.findAll();

        assertAll(
                () -> assertThat(orders).hasSize(3),
                () -> assertThat(orders)
                        .extracting("type")
                        .contains(OrderType.EAT_IN, OrderType.DELIVERY, OrderType.TAKEOUT)
        );
    }

    private static Order 주문접수된_배달주문() {
        Order createdOrder = 배달주문이_등록되어_있음();
        return orderService.accept(createdOrder.getId());
    }

    private static Order 주문제공된_배달주문() {
        Order createdOrder = 주문접수된_배달주문();
        return orderService.serve(createdOrder.getId());
    }

    private static Order 주문을_주문제공_상태까지_진행(Order order) {
        orderService.accept(order.getId());
        return orderService.serve(order.getId());
    }

    private static Order 배달시작된_배달주문() {
        Order createdOrder = 주문제공된_배달주문();
        return orderService.startDelivery(createdOrder.getId());
    }

    private static Order 배달완료된_배달주문() {
        Order createdOrder = 배달시작된_배달주문();
        return orderService.completeDelivery(createdOrder.getId());
    }

    private static Order 매장주문이_등록되어_있음() {
        Order request = createEatInOrder();
        return createdOrder(request);
    }

    private static Order 배달주문이_등록되어_있음() {
        Order request = createDeliveryOrder();
        request.setDeliveryAddress("배달 주소");
        return createdOrder(request);
    }

    private static Order 포장주문이_등록되어_있음() {
        Order request = createTakeOutOrder();
        return createdOrder(request);
    }

    private static Order createdOrder(Order request) {
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        OrderTable orderTable = createOrderTable();
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());
        return orderService.create(request);
    }


    private static Stream<List<OrderLineItem>> nullAndEmptyOrderLienItemList() {
        return Stream.of(null, new ArrayList<>());
    }

    private static OrderTable createOrderTable() {
        OrderTable request = new OrderTable();
        request.setOccupied(true);
        return orderTableRepository.save(request);
    }

    private static OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = menuRepository.save(createMenu());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.ONE);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(BigDecimal.ONE);
        orderLineItem.setQuantity(10);
        return orderLineItem;
    }


    private static OrderLineItem createOrderLineItem(long quantity) {
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    private static Order createDeliveryOrder() {
        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        return order;
    }

    private static Order createTakeOutOrder() {
        final Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        return order;
    }

    private static Order createEatInOrder() {
        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        return order;
    }

    private static Stream<Order> notEatInOrder() {
        return Stream.of(createDeliveryOrder(), createTakeOutOrder());
    }

}
