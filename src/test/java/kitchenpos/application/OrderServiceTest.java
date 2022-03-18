package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderServiceTest {

    public static OrderRepository orderRepository = new InMemoryOrderRepository();

    public OrderTableRepository orderTableRepository = OrderTableServiceTest.orderTableRepository;
    public MenuRepository menuRepository = MenuServiceTest.menuRepository;
    public KitchenridersClient kitchenridersClient = new KitchenridersClient();


    private OrderService orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

    @DisplayName("매장주문을 생성한다.")
    @Test
    void createOrder() {
        OrderTable savedOrderTable = OrderTableServiceTest.saveOrderTable("1번 테이블", 4, false);

        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createEatInOrder(null, savedOrderTable, orderLineItems);

        Order createOrder = orderService.create(order);

        assertAll(
                () -> assertThat(createOrder).isNotNull(),
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }

    @DisplayName("포장주문을 생성한다.")
    @Test
    void createTakeOutOrder() {
        OrderTable savedOrderTable = OrderTableServiceTest.saveOrderTable("1번 테이블", 4, false);

        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createTakeOutOrder(null, savedOrderTable, orderLineItems);

        Order createOrder = orderService.create(order);

        assertAll(
                () -> assertThat(createOrder).isNotNull(),
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.TAKEOUT)
        );
    }

    @DisplayName("배달주문을 생성한다.")
    @Test
    void createOrderDelivery() {
        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createDeliveryOrder(null, "경기도 남양주시", orderLineItems);

        Order createOrder = orderService.create(order);

        assertAll(
                () -> assertThat(createOrder).isNotNull(),
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.DELIVERY)
        );
    }

    @DisplayName("주문시 주문의 타입은 필수로 지정해야한다.")
    @Test
    void necessaryOrderType() {
        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createOrder(null, null, null, "경기도 남양주시", orderLineItems);

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문시 상품은 하나이상 존재해야한다.")
    @Test
    void necessaryOrderItemDeliveryCase() {
        Order order = createDeliveryOrder(null, "경기도 남양주시", new ArrayList<>());

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문시 주문상품은 0개 이상 주문해야한다.")
    @Test
    void necessaryProductQuantity() {
        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), -1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createDeliveryOrder(null, "경기도 남양주시", orderLineItems);

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문시 배달지주소는 필수로 입력해야한다.")
    @Test
    void necessaryDeliveryAddress() {
        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), -1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createDeliveryOrder(null, null, orderLineItems);

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문시 주문상품은 0개 이상 주문해야한다.")
    @Test
    void unableEmptyTable() {
        OrderTable savedOrderTable = OrderTableServiceTest.saveOrderTable("1번 테이블");

        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createEatInOrder(null, savedOrderTable, orderLineItems);

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("비노출 메뉴는 주문할수 없다.")
    @Test
    void unablehideMenu() {
        OrderTable savedOrderTable = OrderTableServiceTest.saveOrderTable("1번 테이블", 4, false);

        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(false), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = createEatInOrder(null, savedOrderTable, orderLineItems);

        assertThatThrownBy(
                () -> orderService.create(order)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주믄을 접수한다.")
    @Test
    void accept() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.WAITING, OrderType.EAT_IN));

        Order acceptOrder = orderService.accept(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주믄을 접수는 대기상태일때만 가능하다.")
    @Test
    void couldAcceptForWaiting() {
        assertAll(
                () -> assertThatThrownBy(
                        () -> orderService.accept(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.ACCEPTED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.accept(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.SERVED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.accept(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.COMPLETED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.accept(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.accept(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERING, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class)
        );
    }

    @DisplayName("주믄을 서빙한다.")
    @Test
    void serve() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.ACCEPTED, OrderType.EAT_IN));

        Order acceptOrder = orderService.serve(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문을 서빙하는것은 접수상태일때만 가능하다.")
    @Test
    void couldServeForAccept() {
        assertAll(
                () -> assertThatThrownBy(
                        () -> orderService.serve(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.WAITING, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.serve(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.SERVED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.serve(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.COMPLETED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.serve(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.serve(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERING, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class)
        );
    }

    @DisplayName("주문을 배달시작한다.")
    @Test
    void startDelivery() {
        Order savedOrder = saveOrder(createOrderSetStatusWithOrderType(OrderStatus.SERVED, OrderType.DELIVERY));

        Order acceptOrder = orderService.startDelivery(savedOrder.getId());

        assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문을 배달시작하는것은 서빙상태일때만 가능하다.")
    @Test
    void couldStartDeliveryForServe() {
        assertAll(
                () -> assertThatThrownBy(
                        () -> orderService.startDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.WAITING, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.startDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.ACCEPTED, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.startDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.COMPLETED, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.startDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERED, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.startDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERING, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class)
        );
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
    @Test
    void couldDeliveryCompleteForDelivering() {
        assertAll(
                () -> assertThatThrownBy(
                        () -> orderService.completeDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.WAITING, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.completeDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.SERVED, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.completeDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.COMPLETED, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.completeDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERED, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.completeDelivery(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.ACCEPTED, OrderType.DELIVERY))
                                .getId())
                ).isInstanceOf(IllegalStateException.class)
        );
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
        OrderTable savedOrderTable = OrderTableServiceTest.saveOrderTable("1번 테이블", 4, false);

        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order eatInOrder = createEatInOrder(OrderStatus.SERVED, savedOrderTable, orderLineItems);
        eatInOrder.setOrderTable(savedOrderTable);
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
        OrderTable savedOrderTable = OrderTableServiceTest.saveOrderTable("1번 테이블", 4, false);

        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order eatInOrder1 = createEatInOrder(OrderStatus.SERVED, savedOrderTable, orderLineItems);
        eatInOrder1.setOrderTable(savedOrderTable);
        Order eatInOrder2 = createEatInOrder(OrderStatus.ACCEPTED, savedOrderTable, orderLineItems);
        eatInOrder2.setOrderTable(savedOrderTable);
        Order order = saveOrder(eatInOrder1);
        saveOrder(eatInOrder2);

        Order acceptOrder = orderService.complete(order.getId());


        OrderTable orderTable = orderTableRepository.findById(savedOrderTable.getId()).get();
        assertAll(
                () -> assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                () -> assertThat(orderTable.isEmpty()).isFalse(),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(4)
        );
    }

    @DisplayName("주문완료상태로 변경하는것은 배달완료,서빙상태 일때만 가능하다")
    @Test
    void couldCompleteForDeliveryCompleteAndServed() {
        assertAll(
                () -> assertThatThrownBy(
                        () -> orderService.complete(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.WAITING, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.complete(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.DELIVERING, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.complete(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.COMPLETED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class),
                () -> assertThatThrownBy(
                        () -> orderService.complete(saveOrder(createOrderSetStatusWithOrderType(OrderStatus.ACCEPTED, OrderType.EAT_IN))
                                .getId())
                ).isInstanceOf(IllegalStateException.class)
        );
    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void findAll() {
        OrderTable savedOrderTable = OrderTableServiceTest.saveOrderTable("1번 테이블", 4, false);

        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        saveOrder(createEatInOrder(null, savedOrderTable, orderLineItems));
        saveOrder(createDeliveryOrder(null, "경기도 남양주시", orderLineItems));

        List<Order> orders = orderService.findAll();
        assertThat(orders).hasSize(orderRepository.findAll().size());
    }

    private Order createOrderSetStatusWithOrderType(OrderStatus orderStatus, OrderType orderType) {
        return createOrder(orderStatus, orderType, null, null, null);
    }

    public static Order saveOrder(Order order) {
        return orderRepository.save(order);
    }


    public static void saveOrderTargetTable(OrderTable savedOrderTable) {
        OrderLineItem orderLineItem = createOrderLineItem(createDummyMenu(true), 1);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        saveOrder(createEatInOrder(null, savedOrderTable, orderLineItems));
    }

    private static Menu createDummyMenu(boolean display) {
        MenuGroup toastMenuGroup = MenuGroupServiceTest.saveMenuGroup(MenuGroupTest.create("인기 대표 토스트"));
        Product 토스트 = ProductServiceTest.save(ProductTest.create("계란햄치즈토스트", 5000L));
        List<MenuProduct> singleMenuProducts = new ArrayList();
        singleMenuProducts.add(MenuProductTest.create(토스트, 1));
        Menu singleMenu = MenuServiceTest.save("토스트 단품", 5000L, display, toastMenuGroup, singleMenuProducts);
        return singleMenu;
    }

    private Order createDeliveryOrder(OrderStatus status, String address, List<OrderLineItem> orderLineItems) {
        return createOrder(status, OrderType.DELIVERY, null, address, orderLineItems);
    }

    public static Order createEatInOrder(OrderStatus status, OrderTable savedOrderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(status, OrderType.EAT_IN, savedOrderTable.getId(), null, orderLineItems);
    }

    private Order createTakeOutOrder(OrderStatus status, OrderTable savedOrderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(status, OrderType.TAKEOUT, savedOrderTable.getId(), null, orderLineItems);
    }

    private static Order createOrder(OrderStatus status, OrderType orderType, UUID savedOrderTableId, String address, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setStatus(status);
        order.setType(orderType);
        order.setOrderTableId(savedOrderTableId);
        order.setDeliveryAddress(address);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    private static OrderLineItem createOrderLineItem(Menu singleMenu, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(singleMenu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(singleMenu.getPrice());
        return orderLineItem;
    }
}
