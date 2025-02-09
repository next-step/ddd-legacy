package kitchenpos.application.order;

import kitchenpos.application.Exception.ErrorCode;
import kitchenpos.application.Exception.OrderException;
import kitchenpos.application.OrderService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fake.FakechenridersClientImpl;
import kitchenpos.fake.repository.InMemoryMenuRepository;
import kitchenpos.fake.repository.InMemoryOrderRepository;
import kitchenpos.fake.repository.InMemoryOrderTableRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;



import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderServiceTest {
    private OrderService orderService;
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        KitchenridersClient kitchenridersClient = new FakechenridersClientImpl();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {
        @Test
        @DisplayName("배달 주문 성공")
        void createDeliveryOrderSuccess() {
            // given
            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());
            ReflectionTestUtils.setField(menu, "id", UUID.randomUUID());
            menuRepository.save(menu);

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order request = OrderFixture.deliveryOrder("서울시 강남구", List.of(orderLineItem));

            // when
            Order created = orderService.create(request);

            // then
            assertAll(
                () -> assertThat(created.getId()).isNotNull(),
                () -> assertThat(created.getType()).isEqualTo(OrderType.DELIVERY),
                () -> assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(created.getDeliveryAddress()).isEqualTo("서울시 강남구")
            );
        }

        @Test
        @DisplayName("매장 주문 성공")
        void createEatInOrderSuccess() {
            // given
            OrderTable orderTable = OrderTableFixture.orderTable("1번 테이블", 4, true);
            orderTableRepository.save(orderTable);

            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());
            ReflectionTestUtils.setField(menu, "id", UUID.randomUUID());

            menuRepository.save(menu);

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order request = OrderFixture.eatInOrder(orderTable.getId(), List.of(orderLineItem));

            // when
            Order created = orderService.create(request);

            // then
            assertAll(
                () -> assertThat(created.getId()).isNotNull(),
                () -> assertThat(created.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(created.getOrderTable().getId()).isEqualTo(orderTable.getId())
            );
        }

        @Test
        @DisplayName("주문 유형이 없으면 실패")
        void failWithoutOrderType() {
            Order request = new Order();

            ReflectionTestUtils.setField(request, "orderLineItems", List.of(new OrderLineItem()));

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(OrderException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_TYPE_INVALID)
                    .hasMessageContaining(ErrorCode.ORDER_TYPE_INVALID.getMessage());
        }

        @Test
        @DisplayName("주문 상품이 없으면 실패")
        void failWithoutOrderLineItems() {
            Order request = new Order();
            ReflectionTestUtils.setField(request, "type", OrderType.TAKEOUT);

            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(OrderException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_LINE_ITEM_NOT_FOUND)
                    .hasMessageContaining(ErrorCode.ORDER_LINE_ITEM_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("메뉴가 존재하지 않으면 실패")
        void failWithNonExistentMenu() {
            OrderLineItem orderLineItem = OrderFixture.orderLineItem(
                    UUID.randomUUID(),
                    1,
                    BigDecimal.valueOf(10000)
            );

            Order request = new Order();
            ReflectionTestUtils.setField(request, "type", OrderType.TAKEOUT);
            ReflectionTestUtils.setField(request, "orderLineItems", List.of(orderLineItem));


            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(OrderException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_LINE_ITEM_SIZE_NOT_MATCHED)
                    .hasMessageContaining(ErrorCode.ORDER_LINE_ITEM_SIZE_NOT_MATCHED.getMessage());
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class ChangeOrderStatus {
        @Test
        @DisplayName("주문 접수 성공")
        void acceptOrderSuccess() {
            // given
            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order order = OrderFixture.takeoutOrder(List.of(orderLineItem));
            orderRepository.save(order);

            // when
            Order accepted = orderService.accept(order.getId());

            // then
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("대기 상태가 아닌 주문은 접수할 수 없다")
        void cannotAcceptNonWaitingOrder() {
            // given
            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order order = OrderFixture.acceptedTakeoutOrder(List.of(orderLineItem));
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.accept(order.getId()))
                    .isInstanceOf(OrderException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_STATUS_INVALID)
                    .hasMessageContaining(ErrorCode.ORDER_STATUS_INVALID.getMessage());
        }

        @Test
        @DisplayName("주문 서빙 성공")
        void serveOrderSuccess() {
            // given
            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order order = OrderFixture.acceptedTakeoutOrder(List.of(orderLineItem));
            orderRepository.save(order);

            // when
            Order served = orderService.serve(order.getId());

            // then
            assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        @DisplayName("접수 상태가 아닌 주문은 서빙할 수 없다")
        void cannotServeNonAcceptedOrder() {
            // given
            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order order = OrderFixture.waitingTakeoutOrder(List.of(orderLineItem));
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.serve(order.getId()))
                    .isInstanceOf(OrderException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_STATUS_INVALID)
                    .hasMessageContaining(ErrorCode.ORDER_STATUS_INVALID.getMessage());
        }
    }

    @Nested
    @DisplayName("배달 주문 처리")
    class HandleDeliveryOrder {
        @Test
        @DisplayName("배달 시작 성공")
        void startDeliverySuccess() {
            // given
            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order order = OrderFixture.servedDeliveryOrder(List.of(orderLineItem));
            ReflectionTestUtils.setField(order, "deliveryAddress", "서울시 강남구");
            orderRepository.save(order);

            // when
            Order delivering = orderService.startDelivery(order.getId());

            // then
            assertThat(delivering.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        @DisplayName("배달 완료 성공")
        void completeDeliverySuccess() {
            // given
            Product product = ProductFixture.product("돈까스", 10000);
            MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
            MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
            Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());

            OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);
            Order order = OrderFixture.deliveringDeliveryOrder(List.of(orderLineItem));
            ReflectionTestUtils.setField(order, "deliveryAddress", "서울시 강남구");
            orderRepository.save(order);

            // when
            Order delivered = orderService.completeDelivery(order.getId());

            // then
            assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Test
    @DisplayName("주문 목록 조회")
    void findAllOrders() {
        // given
        Product product = ProductFixture.product("돈까스", 10000);
        MenuGroup menuGroup = MenuFixture.menuGroup("메인 메뉴");
        MenuProduct menuProduct = MenuFixture.menuProduct(product, 1);
        Menu menu = MenuFixture.menuWithDisplayTrue("돈까스", List.of(menuProduct), 10000, menuGroup.getId());

        OrderLineItem orderLineItem = OrderFixture.orderLineItem(menu, 2);

        Order order1 = OrderFixture.takeoutOrder(List.of(orderLineItem));
        Order order2 = OrderFixture.deliveryOrder("서울시 강남구", List.of(orderLineItem));

        orderRepository.save(order1);
        orderRepository.save(order2);

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders).hasSize(2);
    }
}