package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.mock.TestUtil;
import kitchenpos.mock.client.FakeKitchenridersClient;
import kitchenpos.mock.fixture.MenuFixture;
import kitchenpos.mock.fixture.OrderFixture;
import kitchenpos.mock.persistence.FakeMenuGroupRepository;
import kitchenpos.mock.persistence.FakeMenuRepository;
import kitchenpos.mock.persistence.FakeOrderRepository;
import kitchenpos.mock.persistence.FakeOrderTableRepository;
import kitchenpos.mock.persistence.FakeProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

class OrderServiceTest {

    private OrderService orderService;
    private TestUtil testUtil;
    private FakeOrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new FakeOrderRepository();
        FakeMenuRepository menuRepository = new FakeMenuRepository();
        FakeOrderTableRepository orderTableRepository = new FakeOrderTableRepository();
        FakeKitchenridersClient kitchenridersClient = new FakeKitchenridersClient();
        FakeMenuGroupRepository menuGroupRepository = new FakeMenuGroupRepository();
        FakeProductRepository productRepository = new FakeProductRepository();
        this.orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
            kitchenridersClient);
        this.testUtil = new TestUtil(menuGroupRepository, productRepository,
            menuRepository, orderTableRepository);
    }

    @DisplayName("주문 유형 없이 주문 생성 시, 예외가 발생한다.")
    @Test
    void createTypeException() {
        // given
        Order order = OrderFixture.create(null, testUtil.getOrderLineItems(),
            null,
            null);

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 항목 없이 주문 생성 시, 예외가 발생한다.")
    @Test
    void createOrderLineItemsException() {
        // given
        Order order = OrderFixture.create(OrderType.DELIVERY, null,
            null,
            null);

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 항목과 메뉴 개수가 불일치하게 주문 생성 시, 예외가 발생한다.")
    @Test
    void createSizeException() {
        // given
        List<OrderLineItem> orderLineItems = testUtil.getOrderLineItems();
        orderLineItems.add(new OrderLineItem());
        Order order = OrderFixture.create(OrderType.DELIVERY, orderLineItems,
            null,
            null);

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("숨김 처리된 메뉴를 주문 시, 예외가 발생한다.")
    @Test
    void createMenuHideException() {
        // given
        Menu menu = testUtil.getSavedMenu(
            MenuFixture.create("두마리 치킨", BigDecimal.valueOf(28_000), false,
                testUtil.getSavedMenuGroup(),
                testUtil.getMenuProducts(1)));
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(testUtil.getOrderLineItem(menu));
        Order order = OrderFixture.create(OrderType.DELIVERY, orderLineItems,
            null,
            null);

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 항목 가격과 메뉴 가격이 불일치하면, 예외가 발생한다.")
    @Test
    void createMenuPriceException() {
        // given
        Menu menu = testUtil.getSavedMenu();
        OrderLineItem orderLineItem = testUtil.getOrderLineItem(menu);
        orderLineItem.setPrice(BigDecimal.ZERO);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order order = OrderFixture.create(OrderType.DELIVERY, orderLineItems,
            null,
            null);

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 항목을 마이너스 개수로 주문 생성 시, 예외가 발생한다.")
    @Test
    void createOrderLineItemQuantityException() {
        // given
        int quantity = -1;
        OrderLineItem minusOrderLineItem = testUtil.getOrderLineItem(quantity);
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(minusOrderLineItem);
        Order order = OrderFixture.create(OrderType.DELIVERY, orderLineItems,
            null,
            testUtil.getSavedOrderTable());

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달 주문을 생성할 수 있다.")
    @Test
    void createDelivery() {
        // given
        Order order = createDeliveryOrder();

        // when
        Order savedOrder = orderService.create(order);

        // then
        assertThat(savedOrder.getType()).isEqualTo(order.getType());
        assertThat(savedOrder.getOrderLineItems().size()).isEqualTo(
            order.getOrderLineItems().size());
        assertThat(savedOrder.getDeliveryAddress()).isEqualTo(order.getDeliveryAddress());
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("배달 주소 없이 배달 주문 시, 예외가 발생한다.")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void createDeliveryAddressException(String deliveryAddress) {
        // given
        Order order = OrderFixture.create(OrderType.DELIVERY, testUtil.getOrderLineItems(),
            deliveryAddress,
            null);

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 식사 주문을 생성할 수 있다.")
    @Test
    void createEatIn() {
        // given
        Order order = createEatInOrder();

        // when
        Order savedOrder = orderService.create(order);

        // then
        assertThat(savedOrder.getType()).isEqualTo(order.getType());
        assertThat(savedOrder.getOrderLineItems().size()).isEqualTo(
            order.getOrderLineItems().size());
        assertThat(savedOrder.getOrderTable().getId()).isEqualTo(order.getOrderTable().getId());
    }

    @DisplayName("착석 안된 테이블로 매장 식사 주문 생성 시, 예외가 발생한다.")
    @Test
    void createOrderTableNotOccupiedException() {
        // given
        OrderTable orderTable = testUtil.getSavedOrderTable();
        orderTable.setOccupied(false);
        Order order = OrderFixture.create(OrderType.EAT_IN, testUtil.getOrderLineItems(), null,
            orderTable);

        // when then
        assertThatThrownBy(() -> orderService.create(order))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이크아웃 주문을 생성할 수 있다.")
    @Test
    void createTakeout() {
        // given
        Order order = createTakeoutOrder();

        // when
        Order savedOrder = orderService.create(order);

        // then
        assertThat(savedOrder.getType()).isEqualTo(order.getType());
        assertThat(savedOrder.getOrderLineItems().size()).isEqualTo(
            order.getOrderLineItems().size());
    }

    @DisplayName("주문을 수락할 수 있다.")
    @Test
    void accept() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();

        // when
        Order updatedOrder = orderService.accept(orderId);

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("존재하지 않는 주문을 수락 시, 예외가 발생 한다.")
    @Test
    void acceptNoSuchElementException() {
        // given when then
        assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("대기 중 상태가 아닌 주문을 수락 시, 예외가 발생 한다.")
    @Test
    void acceptStatusException() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        savedOrder.setStatus(OrderStatus.COMPLETED);
        Order updatedOrder = orderRepository.save(savedOrder);
        UUID orderId = updatedOrder.getId();

        // when then
        assertThatThrownBy(() -> orderService.accept(orderId))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 제공됨 처리할 수 있다.")
    @Test
    void serve() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);

        // when
        Order updatedOrder = orderService.serve(orderId);

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("수락됨 상태가 아닌 주문을 제공 시, 예외가 발생 한다.")
    @Test
    void serveStatusException() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();

        // when then
        assertThatThrownBy(() -> orderService.serve(orderId))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 배달시작됨 처리할 수 있다.")
    @Test
    void startDelivery() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);
        orderService.serve(orderId);

        // when
        Order updatedOrder = orderService.startDelivery(orderId);

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 주문이 아닌데 배달시작됨 처리 시, 예외가 발생 한다.")
    @Test
    void startDeliveryTypeException() {
        // given
        Order savedOrder = orderService.create(createEatInOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);
        orderService.serve(orderId);

        // when then
        assertThatThrownBy(() -> orderService.startDelivery(orderId))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제공됨 상태가 아닌데 배달시작됨 처리 시, 예외가 발생 한다.")
    @Test
    void startDeliveryStatusException() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();

        // when then
        assertThatThrownBy(() -> orderService.startDelivery(orderId))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 배달완료됨 처리할 수 있다.")
    @Test
    void completeDelivery() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);
        orderService.serve(orderId);
        orderService.startDelivery(orderId);

        // when
        Order updatedOrder = orderService.completeDelivery(orderId);

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달시작됨 상태가 아닌데 배달완료됨 처리 시, 예외가 발생 한다.")
    @Test
    void completeDeliveryStatusException() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();

        // when then
        assertThatThrownBy(() -> orderService.startDelivery(orderId))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문을 완료됨 처리할 수 있다.")
    @Test
    void completeDeliveryOrder() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);
        orderService.serve(orderId);
        orderService.startDelivery(orderId);
        orderService.completeDelivery(orderId);

        // when
        Order updatedOrder = orderService.complete(orderId);

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("배달 주문의 경우 배달완료됨 상태가 아닌데 완료됨 처리 시, 예외가 발생 한다.")
    @Test
    void completeTypeDeliveryStatusException() {
        // given
        Order savedOrder = orderService.create(createDeliveryOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);
        orderService.serve(orderId);
        orderService.startDelivery(orderId);

        // when then
        assertThatThrownBy(() -> orderService.complete(orderId))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이크아웃 주문의 경우 제공됨 상태가 아닌데 완료됨 처리 시, 예외가 발생 한다.")
    @Test
    void completeTypeTakeoutStatusException() {
        // given
        Order savedOrder = orderService.create(createTakeoutOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);

        // when then
        assertThatThrownBy(() -> orderService.complete(orderId))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 식사 주문을 완료됨 처리할 수 있다.")
    @Test
    void completeEatInOrder() {
        // given
        Order savedOrder = orderService.create(createEatInOrder());
        UUID orderId = savedOrder.getId();
        orderService.accept(orderId);
        orderService.serve(orderId);

        // when
        Order updatedOrder = orderService.complete(orderId);

        // then
        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(updatedOrder.getOrderTable().isOccupied()).isFalse();
        assertThat(updatedOrder.getOrderTable().getNumberOfGuests()).isZero();
    }

    @DisplayName("저장된 주문 리스트를 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Order deliveryOrder = orderService.create(createDeliveryOrder());
        Order eatInOrder = orderService.create(createEatInOrder());
        Order takeoutOrder = orderService.create(createTakeoutOrder());

        // when
        List<Order> savedOrders = orderService.findAll();

        // then
        assertThat(savedOrders)
            .hasSize(3)
            .extracting(Order::getId)
            .containsExactlyInAnyOrder(deliveryOrder.getId(),
                eatInOrder.getId(), takeoutOrder.getId());
    }


    private Order createDeliveryOrder() {
        String deliveryAddress = "서울시 주소";
        return OrderFixture.create(OrderType.DELIVERY, testUtil.getOrderLineItems(),
            deliveryAddress,
            null);
    }

    private Order createEatInOrder() {
        OrderTable orderTable = testUtil.getSavedOrderTable();
        return OrderFixture.create(OrderType.EAT_IN, testUtil.getOrderLineItems(), null,
            orderTable);
    }

    private Order createTakeoutOrder() {
        return OrderFixture.create(OrderType.TAKEOUT, testUtil.getOrderLineItems(),
            null,
            null);
    }
}
