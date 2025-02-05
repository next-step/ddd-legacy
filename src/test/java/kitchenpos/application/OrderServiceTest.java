package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.InMemoryMenuGroupRepository;
import kitchenpos.domain.InMemoryMenuRepository;
import kitchenpos.domain.InMemoryOrderRepository;
import kitchenpos.domain.InMemoryOrderTableRepository;
import kitchenpos.domain.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.testfixture.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;
    private ProductRepository productRepository;
    private MenuGroupRepository menuGroupRepository;

    private OrderService orderService;

    private OrderTable orderTable;
    private Menu menu;
    private Order orderRequest;
    private OrderLineItem orderLineItemRequest;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        kitchenridersClient = new KitchenridersClient();
        productRepository = new InMemoryProductRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();

        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

        MenuGroup menuGroup = TestFixture.createMenuGroup("menuGroup");
        menuGroupRepository.save(menuGroup);

        orderTable = TestFixture.createOrderTable("orderTable", 2, true);
        orderTableRepository.save(orderTable);

        Product product = TestFixture.createProduct("product", BigDecimal.valueOf(1000));
        productRepository.save(product);

        MenuProduct menuProduct = TestFixture.createMenuProduct(1, product);

        menu = TestFixture.createMenu("menu", BigDecimal.valueOf(1000), menuGroup, menuProduct);
        menuRepository.save(menu);

        orderLineItemRequest = new OrderLineItem();
        orderLineItemRequest.setMenuId(menu.getId());
        orderLineItemRequest.setQuantity(1);
        orderLineItemRequest.setPrice(menu.getPrice());

        orderRequest = new Order();
        orderRequest.setOrderTableId(orderTable.getId());
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderLineItems(List.of(orderLineItemRequest));
    }

    @Nested
    class Create {

        @DisplayName("주문의 주문방법(매장, 테이크아웃, 배달)은 필수 값 이다.")
        @Test
        void createWithNullOrderType() {
            // given
            orderRequest.setType(null);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("주문의 주문항목(최소 1개)은 필수 값 이다.")
        @ParameterizedTest
        @MethodSource("provideInvalidOrderLineItems")
        void createWithInvalidOrderLineItems(List<OrderLineItem> invalidOrderLineItem) {
            // given
            orderRequest.setOrderLineItems(invalidOrderLineItem);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 주문이 아닐 경우 주문의 주문항목의 수량은 0보다 큰 값이어야 한다.")
        @ParameterizedTest
        @ValueSource(strings = {"DELIVERY", "TAKEOUT"})
        void createWithNegativeQuantity(String orderType) {
            // given
            orderRequest.setType(OrderType.valueOf(orderType));
            orderLineItemRequest.setQuantity(-1);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }


        @DisplayName("주문 항목의 메뉴가 존재하지 않을 경우 예외를 던진다.")
        @Test
        void createWithNonExistentMenu() {
            // given
            orderLineItemRequest.setMenuId(UUID.randomUUID());
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("주문항목의 메뉴는 노출되어 있지 않을 경우 주문을 등록 할 수 없다")
        @Test
        void createWithNonDisplayedMenu() {
            // given
            menu.setDisplayed(false);
            menuRepository.save(menu);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("주문항목의 요청 금액과 메뉴의 가격이 다를 경우 주문을 등록 할 수 없다")
        @Test
        void createWithDifferentMenuPrice() {
            // given
            orderLineItemRequest.setPrice(BigDecimal.valueOf(100));
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("배달 주문에 대해 주소는 필수요청 값 이다")
        @Test
        void createWithNullDeliveryAddress() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress(null);
            // when & then
            assertThrows(IllegalArgumentException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 주문에 대해 주문 테이블은 필수요청 값 이다.")
        @Test
        void createWithNonExistentOrderTable() {
            // given
            orderRequest.setType(OrderType.EAT_IN);
            orderRequest.setOrderTableId(UUID.randomUUID());
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 주문에 대해 해당 테이블에 착석이 되어 있어야 한다")
        @Test
        void createWithNonOccupiedOrderTable() {
            // given
            orderRequest.setType(OrderType.EAT_IN);
            orderTable.setOccupied(false);
            orderTableRepository.save(orderTable);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.create(orderRequest));
        }

        @DisplayName("매장 주문이 등록된다.")
        @Test
        void create() {
            // when
            Order order = orderService.create(orderRequest);
            // then
            assertNotNull(order.getId());
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(order.getType()).isEqualTo(OrderType.EAT_IN);
        }

        @DisplayName("배달 주문이 등록된다.")
        @Test
        void createWithDelivery() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            // when
            Order order = orderService.create(orderRequest);
            // then
            assertNotNull(order.getId());
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(order.getType()).isEqualTo(OrderType.DELIVERY);
            assertThat(order.getDeliveryAddress()).isEqualTo("deliveryAddress");
        }

        @DisplayName("포장 주문이 등록된다.")
        @Test
        void createWithTakeout() {
            // given
            orderRequest.setType(OrderType.TAKEOUT);
            // when
            Order order = orderService.create(orderRequest);
            // then
            assertNotNull(order.getId());
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT);
        }

        private static Stream<List<OrderLineItem>> provideInvalidOrderLineItems() {
            return Stream.of(null, List.of());
        }
    }

    @Nested
    class Accept {

        private Order order;

        @BeforeEach
        void setUp() {
            order = orderService.create(orderRequest);
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void acceptWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.accept(UUID.randomUUID()));
        }

        @DisplayName("대기 상태가 아닌 주문은 처리 할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"ACCEPTED", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void acceptWithNonWaitingOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.accept(order.getId()));
        }

        @DisplayName("배달 주문에 대해 라이더에게 배달 요청을 한다.")
        @Test
        void acceptWithDelivery() {
            // given
            order.setType(OrderType.DELIVERY);
            order.setOrderTableId(null);
            order.setDeliveryAddress("deliveryAddress");
            orderRepository.save(order);

            // when
            Order accepted = orderService.accept(order.getId());
            // then
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("주문을 접수하면 주문 상태가 접수로 변경된다.")
        @Test
        void accept() {
            // when
            Order accepted = orderService.accept(order.getId());
            // then
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }

    @Nested
    class Serve {

        private Order order;

        @BeforeEach
        void setUp() {
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void serveWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.serve(UUID.randomUUID()));
        }

        @DisplayName("접수 상태가 아닌 주문은 처리 할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "SERVED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void serveWithNonAcceptedOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.serve(order.getId()));
        }

        @DisplayName("주문을 서빙하면 주문 상태가 서빙으로 변경된다.")
        @Test
        void serve() {
            // when
            Order served = orderService.serve(order.getId());
            // then
            assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @Nested
    class StartDelivery {

        private Order order;

        @BeforeEach
        void setUp() {
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void startDeliveryWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.startDelivery(UUID.randomUUID()));
        }

        @DisplayName("주문 방법이 배달이 아닌 경우 처리 할 수 없다")
        @ParameterizedTest
        @ValueSource(strings = {"EAT_IN", "TAKEOUT"})
        void startDeliveryWithNonDeliveryOrder(String orderType) {
            // given
            order.setType(OrderType.valueOf(orderType));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
        }

        @DisplayName("접수 상태가 아닌 주문은 처리 할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
        void startDeliveryWithNonServedOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(order.getId()));
        }

        @DisplayName("주문을 배달하면 주문 상태가 배달중으로 변경된다.")
        @Test
        void startDelivery() {
            // when
            Order delivering = orderService.startDelivery(order.getId());
            // then
            assertThat(delivering.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }
    }

    @Nested
    class CompleteDelivery {

        private Order order;

        @BeforeEach
        void setUp() {
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            order = orderService.startDelivery(order.getId());
        }

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void completeDeliveryWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.completeDelivery(UUID.randomUUID()));
        }

        @DisplayName("배달중 상태가 아닌 주문은 처리 할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
        void completeDeliveryWithNonDeliveringOrder(String orderStatus) {
            // given
            order.setStatus(OrderStatus.valueOf(orderStatus));
            orderRepository.save(order);
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(order.getId()));
        }

        @DisplayName("주문을 배달하면 주문 상태가 배달완료로 변경된다.")
        @Test
        void completeDelivery() {
            // when
            Order delivered = orderService.completeDelivery(order.getId());
            // then
            assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Nested
    class Complete {

        private Order order;

        @DisplayName("주문이 존재하지 않을 경우 예외를 던진다.")
        @Test
        void completeWithNonExistentOrder() {
            // when & then
            assertThrows(NoSuchElementException.class, () -> orderService.complete(UUID.randomUUID()));
        }

        @DisplayName("매장 주문이거나, 포장주문 일 경우 주문 상태가 서빙 상태가 아닌 주문은 처리 할 수 없다.")
        @ParameterizedTest
        @ValueSource(strings = {"EAT_IN", "TAKEOUT"})
        void completeWithNonServedOrder(String orderType) {
            // given
            orderRequest.setType(OrderType.valueOf(orderType));
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
        }

        @DisplayName("배달 주문일 경우 주문 상태가 배달완료 상태가 아닌 주문은 처리 할 수 없다.")
        @Test
        void completeWithNonDeliveredOrder() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            order = orderService.startDelivery(order.getId());
            // when & then
            assertThrows(IllegalStateException.class, () -> orderService.complete(order.getId()));
        }

        @DisplayName("매장 주문일 경우 해당주문의 테이블의 주문이 남아있지 않은 경우 테이블을 비운다.")
        @Test
        void completeWithEatIn() {
            // given
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            // when
            Order completed = orderService.complete(order.getId());
            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(completed.getType()).isEqualTo(OrderType.EAT_IN);
            assertThat(orderTableRepository.findById(order.getOrderTable().getId()).get().isOccupied()).isFalse();
            assertThat(orderTableRepository.findById(order.getOrderTable().getId()).get().getNumberOfGuests()).isEqualTo(0);
        }

        @DisplayName("포장 주문에 대해 완료 처리 한다.")
        @Test
        void completeWithTakeout() {
            // given
            orderRequest.setType(OrderType.TAKEOUT);
            orderRequest.setOrderTableId(null);
            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            // when
            Order completed = orderService.complete(order.getId());
            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(completed.getType()).isEqualTo(OrderType.TAKEOUT);
        }

        @DisplayName("배달 주문에 대해 완료 처리 한다.")
        @Test
        void completeWithDelivery() {
            // given
            orderRequest.setType(OrderType.DELIVERY);
            orderRequest.setDeliveryAddress("deliveryAddress");
            orderRequest.setOrderTableId(null);

            order = orderService.create(orderRequest);
            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());
            order = orderService.startDelivery(order.getId());
            order = orderService.completeDelivery(order.getId());
            // when
            Order completed = orderService.complete(order.getId());
            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(completed.getType()).isEqualTo(OrderType.DELIVERY);
        }
    }

    @Nested
    class FindAll {

        @DisplayName("주문이 존재하지 않을 경우 빈 리스트를 반환한다.")
        @Test
        void findAllWithEmpty() {
            // when
            List<Order> orders = orderService.findAll();
            // then
            assertThat(orders).isEmpty();
        }

        @DisplayName("주문이 존재할 경우 주문 리스트를 반환한다.")
        @Test
        void findAll() {
            // given
            orderService.create(orderRequest);
            // when
            List<Order> orders = orderService.findAll();
            // then
            assertThat(orders).isNotEmpty();
        }
    }
}
