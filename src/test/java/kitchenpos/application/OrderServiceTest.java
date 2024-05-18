package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {

    private OrderRepository orderRepository = new InMemoryOrderRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();

    private ProductRepository productRepository = new InMemoryProductRepository();
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test
    @DisplayName("주문 타입이 null이라면 주문 생성 시 IllegalArgumentException이 발생한다")
    void create_fail_for_null_order_type() {
        Order request = takeoutRequestBuilder()
            .withType(null)
            .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("주문 라인 아이템이 null이거나 비어있다면 주문 생성 시 IllegalArgumentException이 발생한다")
    void create_fail_for_null_or_empty_order_line_item_requests(List<OrderLineItem> orderLineItems) {
        Order request = takeoutRequestBuilder()
                .withOrderLineItemRequests(orderLineItems)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문항목의 메뉴와 실제 대응되는 메뉴의 개수가 다를 경우 IllegalArgumentException이 발생한다")
    void create_fail_for_order_line_item_menu_size_is_not_same_actual_including_menu_size() {
        OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        Order request = takeoutRequestBuilder()
                .withOrderLineItemRequests(List.of(orderLineItemRequest, orderLineItemRequest))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("포장주문인데 주문항목의 수량이 음수일 경우 주문 생성 시 IllegalArgumentException이 발생한다")
    void create_fail_for_order_line_item_has_negative_quantity_but_takeout_order() {
        OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        orderLineItemRequest.setQuantity(-1);

        Order request = takeoutRequestBuilder()
                .withOrderLineItemRequests(List.of(orderLineItemRequest))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("배달주문인데 주문항목의 수량이 음수일 경우 주문 생성 시 IllegalArgumentException이 발생한다")
    void create_fail_for_order_line_item_has_negative_quantity_but_delivery_order() {
        OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        orderLineItemRequest.setQuantity(-1);

        Order request = deliveryOrderRequestBuilder()
                .withOrderLineItemRequests(List.of(orderLineItemRequest))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("메뉴가 미노출 상태인 경우 주문 생성 시 IllegalStateException이 발생한다")
    void create_fail_for_order_hided_menu() {
        OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        orderLineItemRequest.getMenu().setDisplayed(false);
        Menu menu = menuRepository.findById(orderLineItemRequest.getMenu().getId()).orElseThrow();
        menu.setDisplayed(false);
        menuRepository.save(menu);

        Order request = takeoutRequestBuilder()
                .withOrderLineItemRequests(List.of(orderLineItemRequest))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문항목의 메뉴의 가격과 주문항목의 가격이 다를 경우 IllegalArgumentException이 발생한다")
    void create_fail_order_line_item_menu_price_is_not_same() {
        OrderLineItem orderLineItemRequest = createOrderLineItemRequest();
        orderLineItemRequest.setPrice(orderLineItemRequest.getPrice().add(BigDecimal.ONE));

        Order request = takeoutRequestBuilder()
                .withOrderLineItemRequests(List.of(orderLineItemRequest))
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("배달주문인데 배달 주소가 null일 경우 주문 생성 시 IllegalArgumentException이 발생한다")
    void create_fail_for_null_delivery_address_but_delivery_order(String deliveryAddress) {
        Order request = deliveryOrderRequestBuilder()
                .withDeliveryAddress(deliveryAddress)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("매장주문인데 지정한 주문 테이블이 없을 경우 주문 생성 시 NoSuchElementException이 발생한다")
    void create_fail_for_null_order_table_but_eatin_order() {
        Order request = eatInOrderRequestBuilder()
                .withOrderTable(null)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("매장주문인데 지정한 주문 테이블이 점유돼있지 않을 경우 주문 생성 시 IllegalStateException이 발생한다")
    void create_fail_for_not_occupied_order_table_but_eatin_order() {
        OrderTable savedOrderTable = createAndSaveOrderTable(false);
        Order request = eatInOrderRequestBuilder()
                .withOrderTable(savedOrderTable)
                .build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("포장주문을 생성한다")
    void create_takeout_order_success() {
        Order request = takeoutRequestBuilder().build();
        Order response = orderService.create(request);

        assertThat(response.getId()).isNotNull();
    }

    @Test
    @DisplayName("배달주문을 생성한다")
    void create_delivery_order_success() {
        Order request = deliveryOrderRequestBuilder().build();
        Order response = orderService.create(request);

        assertThat(response.getId()).isNotNull();
    }

    @Test
    @DisplayName("매장주문을 생성한다")
    void create_eatin_order_success() {
        Order request = eatInOrderRequestBuilder().build();
        Order response = orderService.create(request);

        assertThat(response.getId()).isNotNull();
    }

    @Test
    @DisplayName("주문을 접수한다")
    void accept_success() {
        Order order = orderService.create(takeoutRequestBuilder().build());
        order.setId(UUID.randomUUID());
        orderRepository.save(order);

        Order acceptedOrder = orderService.accept(order.getId());

        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("해당하는 주문 ID가 없을 경우 주문 접수 시 NoSuchElementException이 발생한다")
    void accept_fail_for_not_existing_order() {
        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
    @DisplayName("주문 상태가 대기 상태가 아닐 경우 주문 접수 시 IllegalStateException이 발생한다")
    void accept_fail_for_status_is_not_waiting(String statusName) {
        Order order = orderService.create(takeoutRequestBuilder().build());
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.valueOf(statusName));
        orderRepository.save(order);

        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문한 것을 전달한다")
    void serve_success() {
        Order order = orderService.create(takeoutRequestBuilder().build());
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        Order servedOrder = orderService.serve(order.getId());

        assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    void serve_fail_for_not_existing_order() {
        assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"SERVED", "WAITING", "DELIVERING", "DELIVERED", "COMPLETED"})
    @DisplayName("주문 상태가 접수 상태가 아닐 경우 주문 전달 시 IllegalStateException이 발생한다")
    void serve_fail_for_status_is_not_accepted(String statusName) {
        Order order = orderService.create(takeoutRequestBuilder().build());
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.valueOf(statusName));
        orderRepository.save(order);

        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    private OrderRequestBuilder eatInOrderRequestBuilder() {
        OrderTable savedOrderTable = createAndSaveOrderTable(true);
        OrderLineItem orderLineItem = createOrderLineItemRequest();
        return OrderRequestBuilder.eatInOrderRequest(savedOrderTable, List.of(orderLineItem));
    }

    public OrderRequestBuilder takeoutRequestBuilder() {
        OrderLineItem orderLineItem = createOrderLineItemRequest();
        return OrderRequestBuilder.takeoutOrderRequest(List.of(orderLineItem));
    }

    public OrderRequestBuilder deliveryOrderRequestBuilder() {
        OrderLineItem orderLineItem = createOrderLineItemRequest();
        return OrderRequestBuilder.deliveryOrderRequest("배달 주소", List.of(orderLineItem));
    }

    private OrderTable createAndSaveOrderTable(boolean isOccupied) {
        OrderTable orderTable = OrderTableRequestBuilder.builder().withOccupied(isOccupied).build();
        orderTable.setId(UUID.randomUUID());
        return orderTableRepository.save(orderTable);
    }

    public OrderLineItem createOrderLineItemRequest() {
        Menu savedMenu = createAndSaveMenu();
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(savedMenu);
        orderLineItem.setMenuId(savedMenu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(savedMenu.getPrice());
        return orderLineItem;
    }

    private Menu createAndSaveMenu() {
        Menu request = menuRequestBuilder().build();
        request.setId(UUID.randomUUID());
        return menuRepository.save(request);
    }

    private MenuRequestBuilder menuRequestBuilder() {
        MenuGroup savedMenuGroup = createAndSaveMenuGroup();
        Product savedProduct = createAndSaveProduct();
        return new MenuRequestBuilder(savedMenuGroup, List.of(new MenuProduct(savedProduct, 1L)));
    }

    private Product createAndSaveProduct() {
        Product product = new Product("상품 이름1", MenuRequestBuilder.DEFAULT_PRICE);
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    private MenuGroup createAndSaveMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName("메뉴 그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);
        return savedMenuGroup;
    }
}

