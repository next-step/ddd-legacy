package kitchenpos.application;

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

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.MenuTestFixture.getSavedMenu;
import static kitchenpos.OrderTestFixture.createDeliveryOrderRequest;
import static kitchenpos.OrderTestFixture.createEatInOrderRequest;
import static kitchenpos.OrderTestFixture.createOrderLineItemRequest;
import static kitchenpos.OrderTestFixture.createOrderRequest;
import static kitchenpos.OrderTestFixture.createTakeoutOrderRequest;
import static kitchenpos.OrderTestFixture.getSavedOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
