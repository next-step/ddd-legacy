package kitchenpos.application;

import static kitchenpos.MenuTestFixture.getSavedMenu;
import static kitchenpos.OrderTestFixture.createDeliveryOrderRequest;
import static kitchenpos.OrderTestFixture.createEatInOrderRequest;
import static kitchenpos.OrderTestFixture.createOrderLineItemRequest;
import static kitchenpos.OrderTestFixture.createOrderRequest;
import static kitchenpos.OrderTestFixture.createTakeoutOrderRequest;
import static kitchenpos.OrderTestFixture.getSavedDeliveryOrder;
import static kitchenpos.OrderTestFixture.getSavedEatInOrder;
import static kitchenpos.OrderTestFixture.getSavedOrderTable;
import static kitchenpos.OrderTestFixture.getSavedTakeOutOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fake.menu.TestMenuRepository;
import kitchenpos.fake.menuGroup.TestMenuGroupRepository;
import kitchenpos.fake.order.TestOrderRepository;
import kitchenpos.fake.ordertable.TestOrderTableRepository;
import kitchenpos.fake.product.TestProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderServiceTest {
    private OrderService orderService;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private MenuService menuService;
    private ProductService productService;
    private MenuGroupService menuGroupService;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderRepository = new TestOrderRepository();
        menuRepository = new TestMenuRepository();
        orderTableRepository = new TestOrderTableRepository();
        menuGroupRepository = new TestMenuGroupRepository();
        productRepository = new TestProductRepository();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, (orderId, amount, deliveryAddress) -> {});
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository, (text) -> false);
        productService = new ProductService(productRepository, menuRepository, (text) -> false);
        menuGroupService = new MenuGroupService(menuGroupRepository);
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("배달주문을 생성한다")
    @Test
    void createDeliveryOrder() {
        // given
        String deliveryAddress = "KitchenPos";
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );
        Order request = createDeliveryOrderRequest(deliveryAddress, orderLineItems);

        // when
        Order order = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(order.getId()).isNotNull(),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(order.getType()).isEqualTo(OrderType.DELIVERY)
        );
    }

    @DisplayName("배달주문에 주소가 없다면 실패한다")
    @Test
    void createDeliveryOrderFail() {
        // given
        String deliveryAddress = null;
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );
        Order request = createDeliveryOrderRequest(deliveryAddress, orderLineItems);

        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @DisplayName("포장주문을 생성한다")
    @Test
    void createTakeOutOrder() {
        // given
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );
        Order request = createTakeoutOrderRequest(orderLineItems);

        // when
        Order order = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(order.getId()).isNotNull(),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT)
        );
    }


    @DisplayName("매장주문을 생성한다")
    @Test
    void createEatInOrder() {
        // given
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );
        OrderTable orderTable = getSavedOrderTable(orderTableService, "testTable");
        orderTableService.sit(orderTable.getId());

        Order request = createEatInOrderRequest(orderTable.getId(), orderLineItems);

        // when
        Order order = orderService.create(request);

        // then
        assertAll(
                () -> assertThat(order.getId()).isNotNull(),
                () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(order.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(order.getOrderTable()).isNotNull()
        );
    }

    @DisplayName("매장주문시 전달받은 orderTable 이 없다면 실패한다")
    @Test
    void createEatInOrderFailNotExistTable() {
        // given
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );

        Order request = createEatInOrderRequest(UUID.randomUUID(), orderLineItems);

        // when, then
        assertThrows(NoSuchElementException.class, () -> orderService.create(request));
    }

    @DisplayName("매장주문시 전달받은 orderTable 이 사용중이 아니라면 실패한다")
    @Test
    void createEatInOrderFailOccupiedTable() {
        // given
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );
        OrderTable orderTable = getSavedOrderTable(orderTableService, "testTable");

        Order request = createEatInOrderRequest(orderTable.getId(), orderLineItems);

        // when, then
        assertThrows(IllegalStateException.class, () -> orderService.create(request));
    }

    @DisplayName("주문타입이 없으면 주문 실패한다")
    @Test
    void createOrderFailNullOrderType() {
        // given
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );
        OrderTable orderTable = getSavedOrderTable(orderTableService, "testTable");
        orderTableService.sit(orderTable.getId());

        Order request = createOrderRequest(null);

        // when then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));

    }

    @DisplayName("orderLineItem 이 없으면 주문 실패한다")
    @Test
    void createOrderFailOrderLineItem() {
        // given
        List<OrderLineItem> orderLineItems = List.of();
        OrderTable orderTable = getSavedOrderTable(orderTableService, "testTable");
        orderTableService.sit(orderTable.getId());

        Order request = createTakeoutOrderRequest(orderLineItems);

        // when then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @DisplayName("메뉴의 개수와 전달받은 orderLineItem 개수가 다르다면 실패한다")
    @Test
    void createOrderFailDifferentOrderLineItem() {
        // given
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(UUID.randomUUID(), savedMenuPrice, 1)
        );
        OrderTable orderTable = getSavedOrderTable(orderTableService, "testTable");
        orderTableService.sit(orderTable.getId());

        Order request = createEatInOrderRequest(orderTable.getId(), orderLineItems);

        // when then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @DisplayName("메뉴가 숨겨져있다면 실패한다")
    @Test
    void createOrderFailHiddenMenu() {
        // given
        String deliveryAddress = "KitchenPos";
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);
        menuService.hide(menu.getId());
        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice, 1)
        );
        Order request = createDeliveryOrderRequest(deliveryAddress, orderLineItems);

        // when then
        assertThrows(IllegalStateException.class, () -> orderService.create(request));
    }

    @DisplayName("메뉴의 가격이 전달받은 것과 다르다면 실패한다")
    @Test
    void createOrderFailMenuPrice() {
        // given
        String deliveryAddress = "KitchenPos";
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);

        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice.add(BigDecimal.ONE), 1)
        );
        Order request = createDeliveryOrderRequest(deliveryAddress, orderLineItems);

        // when then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @DisplayName("수량이 음수라면 실패한다")
    @Test
    void createOrderFailNegativeQuantity() {
        // given
        String deliveryAddress = "KitchenPos";
        BigDecimal savedMenuPrice = BigDecimal.valueOf(2L);
        Menu menu = getSavedMenu(productService, menuService, menuGroupService, savedMenuPrice, true, BigDecimal.TEN);

        List<OrderLineItem> orderLineItems = List.of(
                createOrderLineItemRequest(menu.getId(), savedMenuPrice.add(BigDecimal.ONE), -1)
        );
        Order request = createDeliveryOrderRequest(deliveryAddress, orderLineItems);

        // when then
        assertThrows(IllegalArgumentException.class, () -> orderService.create(request));
    }

    @Test
    @DisplayName("매장식사 주문 수락 성공")
    void eatInOrderAccept() {
        Order order = getSavedEatInOrder(productService, menuService, menuGroupService, orderService, orderTableService);
        Order accept = orderService.accept(order.getId());
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("매장식사 주문 수락 성공")
    void takeOutOrderAccept() {
        Order order = getSavedTakeOutOrder(productService, menuService, menuGroupService, orderService);
        Order accept = orderService.accept(order.getId());
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("매장식사 주문 수락 성공")
    void deliveryOrderAccept() {
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        Order accept = orderService.accept(order.getId());
        assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("매장식사 주문 존재하지 않을 시수락 실패")
    void OrderAcceptNotExist() {
        assertThrows(NoSuchElementException.class, () -> orderService.accept(UUID.randomUUID()));
    }

    @Test
    @DisplayName("매장식사 주문 대기상태아닐시 수락 실패")
    void OrderAcceptNotWaiting() {
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        Order accept = orderService.accept(order.getId());
        assertThrows(IllegalStateException.class, () -> orderService.accept(order.getId()));

    }

    @Test
    @DisplayName("주문 서빙 성공")
    void OrderServeTest() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());

        // when
        Order served = orderService.serve(order.getId());

        // then
        assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문 수락상태 아닐 시 서빙 실패")
    void OrderServeTestFail() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);

        // when, then
        assertThrows(IllegalStateException.class, () -> orderService.serve(order.getId()));
    }

    @Test
    @DisplayName("배달 주문 배달시작 ")
    void deliveryOrderStartDelivery() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        Order deliveryOrder = orderService.startDelivery(order.getId());
        // when, then
        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("배달 주문이 아닌경우 배달중으로 전환할 수 없다")
    void nonDeliveryOrderStartDeliveryFail() {
        // given
        Order order = getSavedTakeOutOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        // when, then
        assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
    }

    @Test
    @DisplayName("배달 주문 서빙상태 아닐 시 배달시작 실패")
    void deliveryOrderStartDeliveryFail() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        // when, then
        assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
    }


    @Test
    @DisplayName("존재하지 않는 주문 배달시작 실패")
    void deliveryOrderStartDeliveryFailNotExist() {
        assertThrows(NoSuchElementException.class, () -> orderService.startDelivery(UUID.randomUUID()));
    }



    @Test
    @DisplayName("배달 주문 배달완료 ")
    void deliveryOrderDeliveryComplete() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.startDelivery(order.getId());
        // when
        Order deliveryOrder = orderService.completeDelivery(order.getId());
        // then
        assertThat(deliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("배달 주문 배달중 아닐 시 배달완료 실패")
    void deliveryOrderDeliveryCompleteFail() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        // when, then
        assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(order.getId()));
    }

    @Test
    @DisplayName("존재하지 않는 주문 배달완료 실패")
    void deliveryOrderDeliveryCompleteNotExist() {
        assertThrows(NoSuchElementException.class, () -> orderService.completeDelivery(UUID.randomUUID()));
    }


    @Test
    @DisplayName("배달 주문 완료 ")
    void deliveryOrderComplete() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.startDelivery(order.getId());
        orderService.completeDelivery(order.getId());
        Order complete = orderService.complete(order.getId());
        // when, then
        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("배달 주문이 배달완료가 아니라면 주문완료로 전환할 수 없다")
    void deliveryOrderCompleteFail() {
        // given
        Order order = getSavedDeliveryOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.startDelivery(order.getId());

        // when, then
        assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
    }

    @Test
    @DisplayName("포장식사 경우 서빙상태라면 주문완료로 전환할 수 있다")
    void takeOutOrderComplete() {
        // given
        Order order = getSavedTakeOutOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        // when
        Order complete = orderService.complete(order.getId());
        // then
        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("매장식사 서빙상태라면 주문완료로 전환할 수 있다")
    void eatInOrderComplete() {
        // given
        Order order = getSavedEatInOrder(productService, menuService, menuGroupService, orderService, orderTableService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        // when
        Order complete = orderService.complete(order.getId());
        // then
        assertThat(complete.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("매장식사 주문완료로 전환하고 나면 테이블은 미사용중, 손님수는 0이된다.")
    void eatInOrderCompleteTable() {
        // given
        Order order = getSavedEatInOrder(productService, menuService, menuGroupService, orderService, orderTableService);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        Order complete = orderService.complete(order.getId());
        // when
        Optional<OrderTable> orderTable = orderTableRepository.findById(order.getOrderTable().getId());

        // then
        assertAll(
            () -> assertTrue(orderTable.isPresent()),
            () -> assertFalse(orderTable.get().isOccupied()),
            () -> assertThat(orderTable.get().getNumberOfGuests()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("배달 주문이 아닌경우 서빙상태가 아니라면 주문완료로 전환할 수 없다")
    void OrderCompleteFail() {
        // given
        Order order = getSavedTakeOutOrder(productService, menuService, menuGroupService, orderService);
        orderService.accept(order.getId());
        // when, then
        assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
    }


    @Test
    @DisplayName("존재하지 않는 주문 완료 실패")
    void orderNotExistCompleteFail() {
        assertThrows(NoSuchElementException.class, () -> orderService.complete(UUID.randomUUID()));
    }
}
