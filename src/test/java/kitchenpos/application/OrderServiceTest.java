package kitchenpos.application;

import kitchenpos.domain.Order;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderServiceTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    @Autowired
    private ProductRepository productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

        MenuGroup request = createMenuGroup(UUID.randomUUID(), "test group");
        menuGroupRepository.save(request);
    }

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create_order() {
        final Menu givenMenu = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(givenMenu, 1, BigDecimal.valueOf(1000)));
        final OrderTable orderTable = createOrderTable("table1", 3, false);
        final Order request = createOrderRequest(OrderType.EAT_IN, orderLineItems, orderTable.getId(), null);

        final Order actual = orderService.create(request);

        assertThat(actual).isNotNull();
    }

    @DisplayName("주문 형태가 존재해야한다.")
    @Test
    void create_with_no_orderType() {
        final Menu givenMenu = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(givenMenu, 1, BigDecimal.valueOf(1000)));
        final OrderType orderType = null;
        final Order request = createOrderRequest(orderType, orderLineItems, null, null);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 라인 아이템이 비어있으면 안된다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_with_no_orderLineItems(List<OrderLineItem> orderLineItems) {
        final Order request = createOrderRequest(OrderType.EAT_IN, orderLineItems, null, null);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 라인 아이템 갯수와 존재하는 메뉴의 갯수가 동일해야한다.")
    @Test
    void create_with_different_menu_size_and_order_line_size() {
        final Menu givenMenu1 = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final Menu givenMenu2 = createMenu(BigDecimal.valueOf(1000), "menu2", false, Collections.emptyList());
        final List<OrderLineItem> orderLineItems = Arrays.asList(
                createOrderLineItem(givenMenu1, 1, BigDecimal.valueOf(1000)),
                createOrderLineItem(givenMenu2, 1, BigDecimal.valueOf(1000)));
        final Order request = createOrderRequest(OrderType.EAT_IN, orderLineItems, null, null);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("식당내 식사가 아닐때는 주문 라인 아이템의 수량은 0보다 커야한다.")
    @ParameterizedTest
    @CsvSource({"DELIVERY, -1", "DELIVERY, -10", "TAKEOUT,-10", "TAKEOUT,-5"})
    void create_with_negative_quantity_negative_when_not_eat_in(OrderType orderType, int quantity) {
        final Menu givenMenu1 = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Arrays.asList(
                createOrderLineItem(givenMenu1, quantity, BigDecimal.valueOf(1000)));
        final Order request = createOrderRequest(orderType, orderLineItems, null, null);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 전시 상태가 활성화 되어야한다.")
    @Test
    void create_with_not_display_menu() {
        final Menu givenMenu1 = createSavedMenu("test1", 1000, 1000, "menu1", false);
        final List<OrderLineItem> orderLineItems = Arrays.asList(
                createOrderLineItem(givenMenu1, 1, BigDecimal.valueOf(1000)));
        final Order request = createOrderRequest(OrderType.DELIVERY, orderLineItems, null, null);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴가격은 주문 라인 아이템과 가격이 같아야한다.")
    @Test
    void create_with_not_same_menu_price_and_order_line_items_sum() {
        final Menu givenMenu1 = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Arrays.asList(
                createOrderLineItem(givenMenu1, 1, BigDecimal.valueOf(5000)));
        final Order request = createOrderRequest(OrderType.DELIVERY, orderLineItems, null, null);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 형태가 배달이면 배달 주소가 존재해야한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create_delivery_with_no_delivery_address(String deliveryAddress) {
        final Menu givenMenu = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(givenMenu, 1, BigDecimal.valueOf(1000)));
        final Order request = createOrderRequest(OrderType.DELIVERY, orderLineItems, null, deliveryAddress);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 형태가 식당내 식사일 경우 테이블 정보가 존재해야한다.")
    @Test
    void create_eat_in_with_no_order_table() {
        final Menu givenMenu = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(givenMenu, 1, BigDecimal.valueOf(1000)));
        final UUID notExistUUID = UUID.fromString("06fe3514-a8a6-48ed-85e6-e7296d0e1000");
        final Order request = createOrderRequest(OrderType.EAT_IN, orderLineItems, notExistUUID, null);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 형태가 식당내 식사일 경우 테이블이 비어있으면 주문할 수 없다.")
    @Test
    void create_eat_in_with_order_table_empty() {
        final Menu givenMenu = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(givenMenu, 1, BigDecimal.valueOf(1000)));
        final OrderTable orderTable = createOrderTable("table1", 3, true);
        final String deliveryAddress = "test address";
        final Order request = createOrderRequest(OrderType.EAT_IN, orderLineItems, orderTable.getId(), deliveryAddress);

        assertThatCode(
                () -> orderService.create(request)
        ).isInstanceOf(IllegalStateException.class);
    }

    @TestFactory
    Collection<DynamicTest> 승인_상태_변경() {
        final Menu givenMenu = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(givenMenu, 1, BigDecimal.valueOf(1000)));
        final OrderTable orderTable = createOrderTable("table1", 3, false);
        final Order request = createOrderRequest(OrderType.EAT_IN, orderLineItems, orderTable.getId(), null);

        final Order actual = orderService.create(request);
        return Arrays.asList(dynamicTest("주문 생성 후 최초상태는 주문대기이다", () -> {
                    assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
                }),
                dynamicTest("승인 상태로 변경할 수 있다.", () -> {
                    final Order changed = orderService.accept(actual.getId());
                    assertThat(changed.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
                }));
    }

    @DisplayName("승인 상태로 변경시 대기에서 변경해야한다.")
    @Test
    void accept_from_not_order_wait_status() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);
        final Order givenOrder = orderRepository.save(order);

        assertThatCode(() -> orderService.accept(givenOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("승인 상태로 변경시 주문이 존재해야한다.")
    @Test
    void accept_from_not_found_order() {
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";

        assertThatCode(() -> orderService.accept(UUID.fromString(notFoundUUID)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("승인 상태로 변경시 주문 타입이 배달이면 키친 라이더 클라이언트에 배달 요청을 한다.")
    @Test
    void request_delivery_after_accept() {
        final Menu givenMenu = createSavedMenu("test1", 1000, 1000, "menu1", true);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(createOrderLineItem(givenMenu, 1, BigDecimal.valueOf(1000)));
        final String givenAddress = "address1";
        final Order request = createOrderRequest(OrderType.DELIVERY, orderLineItems, null, givenAddress);
        final Order givenOrder = orderService.create(request);

        final Order actual = orderService.accept(givenOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        verify(kitchenridersClient).requestDelivery(eq(givenOrder.getId()), eq(BigDecimal.valueOf(1000)), eq(givenAddress));
    }

    @DisplayName("조리 상태로 변경할 수 있다.")
    @Test
    void serve() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);
        final Order givenOrder = orderRepository.save(order);

        final Order actual = orderService.serve(givenOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("조리 상태로 변경시 승인상태가 아니면 예외를 던진다.")
    @ParameterizedTest
    @CsvSource({"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void serve_when_order_status_not_accept(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(orderStatus);
        final Order givenOrder = orderRepository.save(order);

        assertThatCode(() -> orderService.serve(givenOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("조리 상태로 변경시 주문이 존재해야한다.")
    @Test
    void serve_from_not_found_order() {
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";

        assertThatCode(() -> orderService.serve(UUID.fromString(notFoundUUID)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달중으로 변경할 수 있다.")
    @Test
    void start_delivery() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        final Order givenOrder = orderRepository.save(order);

        final Order actual = orderService.startDelivery(givenOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달중으로 변경시 주문이 존재해야한다.")
    @Test
    void start_delivery_from_not_found_order() {
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";

        assertThatCode(() -> orderService.startDelivery(UUID.fromString(notFoundUUID)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달중으로 변경시 주문 형태가 배달이어야  한다.")
    @ParameterizedTest
    @CsvSource({"TAKEOUT", "EAT_IN"})
    void start_delivery_with_not_order_type_delivery(OrderType orderType) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        final Order givenOrder = orderRepository.save(order);

        assertThatCode(() -> orderService.startDelivery(givenOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달중으로 변경시 조리상태가 아니면 예외를 던진다.")
    @ParameterizedTest
    @CsvSource({"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    void start_delivery_with_not_order_status_served(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        final Order givenOrder = orderRepository.save(order);

        assertThatCode(() -> orderService.startDelivery(givenOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 완료로 변경할 수 있다.")
    @Test
    void complete_delivery() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERING);
        final Order givenOrder = orderRepository.save(order);

        final Order actual = orderService.completeDelivery(givenOrder.getId());

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달완료로 변경시 주문이 존재해야한다.")
    @Test
    void complete_delivery_from_not_found_order() {
        final String notFoundUUID = "06fe3514-a8a6-48ed-85e6-e7296d0e1000";

        assertThatCode(() -> orderService.completeDelivery(UUID.fromString(notFoundUUID)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달완료로 변경시 기존 주문이 배달중이 아니면.예외를 던진다.")
    @ParameterizedTest
    @CsvSource({"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    void complete_delivery_with_not_order_status_delivering(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        final Order givenOrder = orderRepository.save(order);

        assertThatCode(() -> orderService.completeDelivery(givenOrder.getId()))
                .isInstanceOf(IllegalStateException.class);
    }


    private Menu createSavedMenu(String productName, int productPrice, int menuPrice, String menuName, boolean display) {
        final Product product = createProduct(productName, BigDecimal.valueOf(productPrice));
        final MenuProduct menuProduct = createMenuProduct(product, 1);
        final Menu givenMenu = saveMenu(createMenu(BigDecimal.valueOf(menuPrice), menuName, display, Collections.singletonList(menuProduct)));
        return givenMenu;
    }

    private OrderTable createOrderTable(String name, Integer numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setEmpty(empty);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTableRepository.save(orderTable);
    }

    private OrderLineItem createOrderLineItem(Menu menu, Integer quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }

    private Order createOrderRequest(OrderType orderType, List<OrderLineItem> orderLineItems, UUID orderTableId, String deliveryAddress) {
        Order order = new Order();
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setType(orderType);
        return order;
    }

    private Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return productRepository.save(product);
    }

    private MenuProduct createMenuProduct(Product product, Integer quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private Menu createMenu(BigDecimal price, String name, boolean display, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setName(name);
        menu.setDisplayed(display);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(findAnyMenuGroup());
        return menu;
    }

    private Menu saveMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    private MenuGroup createMenuGroup(UUID uuid, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(uuid);
        menuGroup.setName(name);
        return menuGroup;
    }

    private MenuGroup findAnyMenuGroup() {
        return menuGroupRepository.findAll()
                .stream()
                .findAny()
                .orElseThrow(EntityNotFoundException::new);
    }
}
