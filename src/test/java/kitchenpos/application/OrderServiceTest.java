package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.MenuFixture.menu;
import static kitchenpos.MenuProductFixture.*;
import static kitchenpos.OrderTableFixture.orderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderTableRepository orderTableRepository;
    private MenuRepository menuRepository;
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        menuRepository = new InMemoryMenuRepository();
        kitchenridersClient = new KitchenridersClient();

        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("매장주문을 생성한다.")
    @Test
    void createOrder() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        OrderTable orderTable = orderTableRepository.save(orderTable(false, 4));
        Order order = createEatInOrder(orderTable, Arrays.asList(createOrderLineItem(menu, 1)));

        Order createOrder = orderService.create(order);

        assertThat(createOrder).isNotNull();
        assertAll(
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }

    @DisplayName("포장주문을 생성한다.")
    @Test
    void createTakeOutOrder() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        OrderTable orderTable = orderTableRepository.save(orderTable(false, 4));
        Order order = createTakeOutOrder(orderTable, Arrays.asList(createOrderLineItem(menu, 1)));

        Order createOrder = orderService.create(order);

        assertThat(createOrder).isNotNull();
        assertAll(
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.TAKEOUT)
        );
    }

    @DisplayName("배달주문을 생성한다.")
    @Test
    void createOrderDelivery() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        Order order = createDeliveryOrder("경기도 남양주시", Arrays.asList(createOrderLineItem(menu, 1)));

        Order createOrder = orderService.create(order);

        assertThat(createOrder).isNotNull();
        assertAll(
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.DELIVERY)
        );
    }

    @DisplayName("주문시 주문의 타입은 필수로 지정해야한다.")
    @Test
    void necessaryOrderType() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        Order order = createOrder(null, null, "경기도 남양주시", Arrays.asList(createOrderLineItem(menu, 1)));

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문시 상품은 하나이상 존재해야한다.")
    @Test
    void necessaryOrderItemDeliveryCase() {
        Order order = createDeliveryOrder("경기도 남양주시", new ArrayList<>());

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문시 주문상품은 0개 이상 주문해야한다.")
    @Test
    void necessaryProductQuantity() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));

        Order order = createDeliveryOrder("경기도 남양주시", Arrays.asList(createOrderLineItem(menu, -1)));

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문시 배달지주소는 필수로 입력해야한다.")
    @Test
    void necessaryDeliveryAddress() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        Order order = createDeliveryOrder(null, Arrays.asList(createOrderLineItem(menu, 1)));

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("빈테이블에는 주문이 불가능하다.")
    @Test
    void unableEmptyTable() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        OrderTable orderTable = orderTableRepository.save(orderTable());
        Order order = createEatInOrder(orderTable, Arrays.asList(createOrderLineItem(menu, 1)));

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("비노출 메뉴는 주문할수 없다.")
    @Test
    void unableHideMenu() {
        Menu menu = menuRepository.save(menu(19_000L, false, menuProduct()));
        OrderTable orderTable = orderTableRepository.save(orderTable());
        Order order = createEatInOrder(orderTable, Arrays.asList(createOrderLineItem(menu, 1)));

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 접수한다.")
    @Test
    void accept() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.WAITING, OrderType.EAT_IN));

        Order acceptOrder = orderService.accept(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주믄을 접수는 대기상태일때만 가능하다.")
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "COMPLETED", "DELIVERED", "DELIVERING"})
    @ParameterizedTest
    void couldAcceptForWaiting(OrderStatus orderStatus) {
        assertThatThrownBy(
                () -> orderService.accept(saveOrder(createOrderSetStatusWithOrderType(orderStatus, OrderType.EAT_IN))
                        .getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주믄을 서빙한다.")
    @Test
    void serve() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.ACCEPTED, OrderType.EAT_IN));

        Order acceptOrder = orderService.serve(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문을 서빙하는것은 접수상태일때만 가능하다.")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "COMPLETED", "DELIVERED", "DELIVERING"})
    @ParameterizedTest
    void couldServeForAccept(OrderStatus orderStatus) {
        assertThatThrownBy(
                () -> orderService.serve(saveOrder(createOrderSetStatusWithOrderType(orderStatus, OrderType.EAT_IN))
                        .getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 배달시작한다.")
    @Test
    void startDelivery() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.SERVED, OrderType.DELIVERY));

        Order acceptOrder = orderService.startDelivery(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문을 배달시작하는것은 서빙상태일때만 가능하다.")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "COMPLETED", "DELIVERED", "DELIVERING"})
    @ParameterizedTest
    void couldStartDeliveryForServe(OrderStatus orderStatus) {
        assertThatThrownBy(
                () -> orderService.startDelivery(saveOrder(createOrderSetStatusWithOrderType(orderStatus, OrderType.DELIVERY))
                        .getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 배달시작하는것은 주문타입이 배달일 경우만 가능하다.")
    @Test
    void couldStartDeliveryOnlyDeliveryOrderType() {
        assertThatThrownBy(
                () -> orderService.startDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.SERVED, OrderType.EAT_IN))
                        .getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달중인 주문의 상태로 배달완료로 변경한다.")
    @Test
    void deliveryComplete() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERING, OrderType.DELIVERY));

        Order acceptOrder = orderService.completeDelivery(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달중인 주문을 배달완료로 변경하는것은 배달중인 상태만 가능하다.")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "ACCEPTED", "COMPLETED", "DELIVERED"})
    @ParameterizedTest
    void couldDeliveryCompleteForDelivering(OrderStatus orderStatus) {
        assertThatThrownBy(
                () -> orderService.completeDelivery(saveOrder(createOrderSetStatusWithOrderType(orderStatus, OrderType.DELIVERY))
                        .getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문타입이 배달인경우 배달이 완료된 주문만 주문완료로 변경한다.")
    @Test
    void completeForDeliveryType() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERED, OrderType.DELIVERY));

        Order acceptOrder = orderService.complete(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문타입이 매장식사인 경우 서빙이 완료된 주문만 주문완료로 변경한다.")
    @Test
    void completeForEatIn() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        OrderTable savedOrderTable = orderTableRepository.save(orderTable());
        Order eatInOrder = createEatInOrder(OrderStatus.SERVED, savedOrderTable, Arrays.asList(createOrderLineItem(menu, 1)));
        Order order = saveOrder(eatInOrder);
        Order acceptOrder = orderService.complete(order.getId());

        OrderTable orderTable = orderTableRepository.findById(savedOrderTable.getId()).get();
        assertAll(
                () -> assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(orderTable.isEmpty()).isTrue(),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("매장식사시 해당 테이블의 주문내역중 완료되지않은것이 존재하면 빈테이블로 변경되지않는다.")
    @Test
    void completeForEatInNotEmptyTable() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        OrderTable savedOrderTable = orderTableRepository.save(orderTable());

        Order eatInOrder1 = createEatInOrder(OrderStatus.SERVED, savedOrderTable, Arrays.asList(createOrderLineItem(menu, 1)));
        eatInOrder1.setOrderTable(savedOrderTable);
        Order eatInOrder2 = createEatInOrder(OrderStatus.ACCEPTED, savedOrderTable, Arrays.asList(createOrderLineItem(menu, 1)));
        eatInOrder2.setOrderTable(savedOrderTable);
        Order order = saveOrder(eatInOrder1);

        saveOrder(eatInOrder2);

        assertThatThrownBy(
                () -> orderService.complete(order.getId())
        ).isInstanceOf(IllegalStateException.class);

    }

    @DisplayName("주문완료상태로 변경하는것은 배달완료,서빙상태 일때만 가능하다")
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "COMPLETED", "DELIVERING"})
    @ParameterizedTest
    void couldCompleteForDeliveryCompleteAndServed(OrderStatus orderStatus) {
        assertThatThrownBy(
                () -> orderService.complete(saveOrder(createOrderSetStatusWithOrderType(orderStatus, OrderType.EAT_IN))
                        .getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void findAll() {
        Menu menu = menuRepository.save(menu(19_000L, true, menuProduct()));
        OrderTable savedOrderTable = orderTableRepository.save(orderTable());
        Order eatInOrder = createEatInOrder(OrderStatus.SERVED, savedOrderTable, Arrays.asList(createOrderLineItem(menu, 1)));
        saveOrder(eatInOrder);

        List<Order> orders = orderService.findAll();
        assertThat(orders).hasSize(1);
    }

    private Order createOrderSetStatusWithOrderType(OrderStatus orderStatus, OrderType orderType) {
        return createOrder(orderStatus, orderType, null, null);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    private Order createDeliveryOrder(String address, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderStatus.WAITING, OrderType.DELIVERY, address, orderLineItems);
    }

    public static Order createEatInOrder(OrderTable savedOrderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderStatus.WAITING, OrderType.EAT_IN, savedOrderTable, null, orderLineItems);
    }

    public static Order createEatInOrder(OrderStatus status, OrderTable savedOrderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(status, OrderType.EAT_IN, savedOrderTable, null, orderLineItems);
    }

    private Order createTakeOutOrder(OrderTable savedOrderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, savedOrderTable, null, orderLineItems);
    }

    private static Order createOrder(OrderStatus status, OrderType orderType, String address, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setStatus(status);
        order.setType(orderType);
        order.setDeliveryAddress(address);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    private static Order createOrder(OrderStatus status, OrderType orderType, OrderTable savedOrderTable, String address, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setStatus(status);
        order.setType(orderType);
        order.setOrderTable(savedOrderTable);
        order.setOrderTableId(savedOrderTable.getId());
        order.setDeliveryAddress(address);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    private OrderLineItem createOrderLineItem(Menu singleMenu, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(singleMenu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(singleMenu.getPrice());
        return orderLineItem;
    }
}
