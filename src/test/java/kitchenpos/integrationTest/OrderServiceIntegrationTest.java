package kitchenpos.integrationTest;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.fixtures.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.repository.InMemoryMenuGroupRepository;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
public class OrderServiceIntegrationTest {
    private OrderService orderService;
    private MenuGroupRepository menuGroupRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;


    @BeforeEach
    void setUp() {
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    private Order createDefaultEatInOrder(OrderStatus orderStatus) {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨");
        menuGroupRepository.save(menuGroup);

        Product product = ProductFixture.후라이드치킨_16000원_상품();
        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());

        Menu menu = MenuFixture.create("후라이드 치킨", BigDecimal.valueOf(16000), true, menuGroup, List.of(menuProduct));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = OrderLineItemFixture.create(menu, menu.getPrice(), 1);

        OrderTable orderTable = OrderTableFixture.create("테이블1", 4, true);
        UUID orderTableId = orderTableRepository.save(orderTable).getId();
        orderTable.setId(orderTableId);

        return OrderFixture.create(OrderType.EAT_IN, orderStatus, LocalDateTime.now(), List.of(orderLineItem), null, orderTable);
    }

    private Order createDefaultTakeOutOrder(OrderStatus orderStatus) {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨");
        menuGroupRepository.save(menuGroup);

        Product product = ProductFixture.후라이드치킨_16000원_상품();
        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());

        Menu menu = MenuFixture.create("후라이드 치킨", BigDecimal.valueOf(16000), true, menuGroup, List.of(menuProduct));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = OrderLineItemFixture.create(menu, menu.getPrice(), 1);

        return OrderFixture.create(OrderType.TAKEOUT, orderStatus, LocalDateTime.now(), List.of(orderLineItem), null, null);
    }

    private Order createDefaultDeliveryOrder(OrderStatus orderStatus) {
        MenuGroup menuGroup = MenuGroupFixture.create("치킨");
        menuGroupRepository.save(menuGroup);

        Product product = ProductFixture.후라이드치킨_16000원_상품();
        MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());

        Menu menu = MenuFixture.create("후라이드 치킨", BigDecimal.valueOf(16000), true, menuGroup, List.of(menuProduct));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = OrderLineItemFixture.create(menu, menu.getPrice(), 1);

        return OrderFixture.create(OrderType.DELIVERY, orderStatus, LocalDateTime.now(), List.of(orderLineItem), "서울시 강남구", null);
    }

    private Stream<Order> getDefaultOrders(OrderStatus orderStatus) {
        Order o1 = createDefaultEatInOrder(orderStatus);
        orderRepository.save(o1);
        Order o2 = createDefaultTakeOutOrder(orderStatus);
        orderRepository.save(o2);
        Order o3 = createDefaultDeliveryOrder(orderStatus);
        orderRepository.save(o3);

        return Stream.of(o1, o2, o3);
    }

    @Nested
    class CommonExceptionTests {
        @Test
        void 주문을_승인할_수_있다() {
            getDefaultOrders(OrderStatus.WAITING)
                    .forEach(o -> {
                        Order acceptedOrder = orderService.accept(o.getId());
                        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
                    });
        }

        @Test
        void 존재하지_않는_주문을_승인_실패() {
            assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void 주문을_서빙할_수_있다() {
            getDefaultOrders(OrderStatus.ACCEPTED)
                    .forEach(order -> {
                        Order acceptedOrder = orderService.serve(order.getId());
                        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
                    });
        }

        @Test
        void 존재하지_않는_주문을_서빙_실패() {
            assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }


        @ParameterizedTest
        @EnumSource(OrderType.class)
        void 메뉴가_표시되지_않으면_주문_생성_실패(OrderType orderType) {
            MenuGroup menuGroup = MenuGroupFixture.create("치킨");
            menuGroupRepository.save(menuGroup);

            Product product = ProductFixture.후라이드치킨_16000원_상품();
            MenuProduct menuProduct = MenuProductFixture.create(product, 1, product.getId());

            Menu menu = MenuFixture.create("후라이드 치킨", BigDecimal.valueOf(16000), false, menuGroup, List.of(menuProduct));
            menuRepository.save(menu);

            OrderLineItem orderLineItem = OrderLineItemFixture.create(menu, menu.getPrice(), 1);

            Order order = OrderFixture.create(orderType, OrderStatus.WAITING, LocalDateTime.now(), List.of(orderLineItem),
                    null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }


        @Test
        void 가격이_일치하지_않는_메뉴로_주문_생성_실패() {
            getDefaultOrders(OrderStatus.WAITING)
                    .forEach(order -> {
                        order.getOrderLineItems().get(0).setPrice(BigDecimal.valueOf(9999));
                        assertThatThrownBy(() -> orderService.create(order))
                                .isInstanceOf(IllegalArgumentException.class);
                    });
        }

        @Test
        void 잘못된_메뉴로_주문_생성_실패() {
            getDefaultOrders(OrderStatus.WAITING)
                    .forEach(order -> {
                        order.getOrderLineItems().get(0).setMenuId(UUID.randomUUID());
                        assertThatThrownBy(() -> orderService.create(order))
                                .isInstanceOf(IllegalArgumentException.class);
                    });
        }
    }

    @Nested
    class TakeOutOrders {
        @Test
        void 주문을_생성할_수_있다() {
            Order order = createDefaultTakeOutOrder(OrderStatus.WAITING);
            Order createdOrder = orderService.create(order);

            assertThat(createdOrder.getId()).isNotNull();
            assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(createdOrder.getOrderLineItems()).hasSize(1);
            // 포장 주문은 테이블이 없으므로 null
            assertThat(createdOrder.getOrderTable()).isNull();
            // 포장 주문은 주소가 없으므로 null
            assertThat(createdOrder.getDeliveryAddress()).isNull();
        }

        @Test
        void 주문을_완료할_수_있다() {
            Order order = createDefaultTakeOutOrder(OrderStatus.SERVED);
            orderRepository.save(order);

            Order completedOrder = orderService.complete(order.getId());
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        void 서빙_상태가_아닌_주문_완료_실패() {
            Order order = createDefaultTakeOutOrder(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class EatInOrders {

        @Test
        void 주문을_생성할_수_있다() {
            Order order = createDefaultEatInOrder(OrderStatus.WAITING);
            Order createdOrder = orderService.create(order);

            assertThat(createdOrder.getId()).isNotNull();
            assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(createdOrder.getOrderLineItems()).hasSize(1);
            // 매장 주문은 테이블이 있다.
            assertThat(createdOrder.getOrderTable()).isNotNull();
            assertThat(createdOrder.getOrderTableId()).isNotNull();
            // 매장 주문은 주소가 없다.
            assertThat(createdOrder.getDeliveryAddress()).isNull();
        }

        @Test
        void 주문을_완료할_수_있다() {
            Order order = createDefaultEatInOrder(OrderStatus.SERVED);
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.create("테이블1", 4, true));
            order.setOrderTable(orderTable);
            order.setOrderTableId(orderTable.getId());
            orderRepository.save(order);

            Order completedOrder = orderService.complete(order.getId());
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            // 매장 주문은 완료시 테이블이 비어있어야 한다.
            assertThat(completedOrder.getOrderTable().isOccupied()).isFalse();
            assertThat(completedOrder.getOrderTable().getNumberOfGuests()).isZero();

        }

        @Test
        void 서빙_상태가_아닌_주문_완료_실패() {
            Order order = createDefaultEatInOrder(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void 매장_주문은_메뉴의_수가_음수가_될_수_있음() {
            Order order = createDefaultEatInOrder(OrderStatus.WAITING);
            order.getOrderLineItems().get(0).setQuantity(-1);

            Order createOrder = orderService.create(order);
            assertThat(createOrder.getOrderLineItems().get(0).getQuantity()).isEqualTo(-1);
        }
    }

    @Nested
    class DeliveryOrders {
        @Test
        void 주문을_생성할_수_있다() {
            Order order = createDefaultDeliveryOrder(OrderStatus.WAITING);
            Order createdOrder = orderService.create(order);

            assertThat(createdOrder.getId()).isNotNull();
            assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(createdOrder.getOrderLineItems()).hasSize(1);
            // 배달 주문은 테이블이 없다.
            assertThat(createdOrder.getOrderTable()).isNull();
            // 배달 주문은 주소가 있다.
            assertThat(createdOrder.getDeliveryAddress()).isNotNull();
        }


        @Test
        void 잘못된_배달_주소로_주문_생성_실패() {
            Order order = createDefaultDeliveryOrder(OrderStatus.WAITING);
            order.setDeliveryAddress(null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 배달_주문을_시작할_수_있다() {
            Order order = createDefaultDeliveryOrder(OrderStatus.SERVED);
            orderRepository.save(order);

            Order deliveringOrder = orderService.startDelivery(order.getId());
            assertThat(deliveringOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        void 배달_상태가_아닌_주문_실패() {
            Order order = createDefaultDeliveryOrder(OrderStatus.WAITING);
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        void 배달을_완료할_수_있다() {
            Order order = createDefaultDeliveryOrder(OrderStatus.DELIVERING);
            orderRepository.save(order);

            Order deliveredOrder = orderService.completeDelivery(order.getId());
            assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        void 배달_완료_상태가_아닌_주문_완료_실패() {
            Order order = createDefaultDeliveryOrder(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

    }
}
