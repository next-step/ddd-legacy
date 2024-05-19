package kitchenpos.application;

import fixtures.MenuProductSteps;
import fixtures.OrderBuilder;
import fixtures.OrderLineItemBuilder;
import fixtures.OrderSteps;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private ProductRepository productRepository;

    private OrderSteps orderSteps;
    private MenuProductSteps menuProductSteps;


    @BeforeEach
    void setUp() {
        orderSteps = new OrderSteps(orderRepository, orderTableRepository);
        menuProductSteps = new MenuProductSteps(menuGroupRepository, menuRepository, productRepository);
    }

    @Nested
    @DisplayName("주문이 생성될 때")
    class OrderCreateTest {

        @DisplayName("주문 상태는 대기(WAITING)다")
        @Test
        void createOrderStatusIsWaitingTest() {

            Order order = createOrder(OrderType.EAT_IN);

            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @DisplayName("주문의 타입은 배달, 포장, 매장 식사여야 한다")
        @ParameterizedTest
        @EnumSource(value = OrderType.class)
        void createOrderTest(OrderType orderType) {

            assertDoesNotThrow(() -> createOrder(orderType));
        }


        @DisplayName("주문의 타입이 없으면 주문 생성이 불가능하다")
        @ParameterizedTest
        @NullSource()
        void createOrderFailedWhenOrderTypeIsNullTest(OrderType orderType) {

            Order order = new OrderBuilder()
                    .withOrderType(orderType)
                    .withDeliveryAddress(null)
                    .build();

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("배달 주문은 주소 정보가 없으면 주문 생성이 불가능하다")
        @Test
        void createDeliveryOrderFailWhenAddressIsNullTest() {

            Order order = new OrderBuilder()
                    .withOrderType(OrderType.DELIVERY)
                    .withDeliveryAddress(null)
                    .build();

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @DisplayName("매장식사 주문은 주문 테이블 정보가 없으면 주문 생성이 불가능하다")
        @Test
        void createEatInOrderFailWhenOrderTableIsEmptyTest() {

            Order order = new OrderBuilder()
                    .withOrderType(OrderType.EAT_IN)
                    .withOrderTable(new OrderTable())
                    .build();

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @DisplayName("매장 식사 주문은 주문 수량이 0보다 작으면 주문 생성이 불가능하다")
        @Test
        void createEatInOrderFailWhenQuantityIsLessThanZeroTest() {

            Order order = new OrderBuilder()
                    .withOrderType(OrderType.EAT_IN)
                    .withOrderLineItems(List.of(new OrderLineItemBuilder().withQuantity(-1).build()))
                    .build();

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @DisplayName("주문에 메뉴가 없으면 주문 생성이 불가능하다")
        @Test
        void createdOrderHasAtLeastOneMenuTest() {

            Order order = new OrderBuilder()
                    .withOrderType(OrderType.EAT_IN)
                    .withOrderLineItems(List.of())
                    .build();

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @DisplayName("주문한 메뉴가 등록된 메뉴가 아니면 주문 생성이 불가능하다")
        @Test
        void createOrderMenuShouldRegisteredMenuTest() {

            Order order = new OrderBuilder()
                    .withOrderType(OrderType.EAT_IN)
                    .withOrderLineItems(List.of(new OrderLineItemBuilder().build()))
                    .build();

            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }

        @DisplayName("감추기 된 메뉴는 주문 생성이 불가능하다")
        @Test
        void createOrderMenuShouldDisplayedMenuTest() {

            // given
            Menu menu = menuProductSteps.감추기된_메뉴를_생성한다();

            // when
            // then
            Order order = orderSteps.주문을_생성한다(menu, OrderType.EAT_IN);
            assertThrows(IllegalStateException.class, () -> orderService.create(order));
        }

        @DisplayName("주문한 메뉴의 가격이 0보다 작으면 주문 생성이 불가능 하다")
        @Test
        void createOrderShouldHasPositivePriceTest() {

            // given
            MenuGroup menuGroup = menuProductSteps.메뉴그룹_생성한다();
            Menu menu = menuProductSteps.메뉴를_생성한다("치킨", -1, menuGroup, true, null);

            // when
            // then
            Order order = orderSteps.주문을_생성한다(menu, OrderType.EAT_IN);
            assertThrows(IllegalArgumentException.class, () -> orderService.create(order));
        }
    }

    @Nested
    @DisplayName("주문이 승인(ACCEPTED)될 때")
    class OrderAcceptTest {

        @DisplayName("주문의 상태는 대기(WAITING)에서 승인(ACCEPTED)로 변경된다")
        @Test
        void acceptOrderTest() {

            Menu menu = menuProductSteps.메뉴를_생성한다();
            Order order = orderSteps.주문한다(menu, OrderType.EAT_IN);

            Order accept = orderService.accept(order.getId());

            assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("주문의 상태가 대기(WAITING)이 아니면 주문 승인이 불가능하다")
        @Test
        void acceptOrderFailWhenOrderStatusIsNotWaitingTest() {

            Order order = createOrder(OrderType.EAT_IN);

            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            assertThrows(IllegalStateException.class, () -> orderService.accept(order.getId()));
        }
    }


    @DisplayName("주문이 제공(SERVED)될 때")
    @Nested
    class OrderServedTest {

        @DisplayName("승인(ACCEPTED)에서 제공(SERVED)로 변경된다")
        @Test
        void acceptedOrderCanBeServedTest() {

            // given
            Order order = createOrder(OrderType.DELIVERY);
            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);

            // when
            order = orderService.serve(order.getId());

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @DisplayName("주문이 승인(ACCEPTED)이 아니면 제공(SERVED)로 변경할 수 없다")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "DELIVERING", "DELIVERED", "COMPLETED"})
        void waitingOrderShouldNotBeServedTest(OrderStatus orderStatus) {

            // given
            Order order = createOrder(OrderType.DELIVERY);

            // when
            order.setStatus(orderStatus);
            orderRepository.save(order);

            // then
            assertThatThrownBy(() -> orderService.serve(order.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문이 배달 중(DELIVERING)될 때")
    @Nested
    class OrderDeliveringTest {

        @DisplayName("제공(SERVED)에서 배달 중(DELIVERING)로 변경된다")
        @Test
        void servedOrderCanBeDeliveringTest() {

            // given
            Order order = createOrder(OrderType.DELIVERY);
            order.setStatus(OrderStatus.SERVED);
            orderRepository.save(order);

            // when
            order = orderService.startDelivery(order.getId());

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("주문의 유형은 배달이다")
        @Test
        void servedOrderShouldNotBeDeliveringTest() {
            // given
            Order order = createOrder(OrderType.DELIVERY);
            order.setStatus(OrderStatus.SERVED);
            orderRepository.save(order);

            // when
            Order delivery = orderService.startDelivery(order.getId());

            // then
            assertThat(delivery.getType()).isEqualTo(OrderType.DELIVERY);
        }
    }

    @DisplayName("주문이 배달 완료(DELIVERED)될 때")
    @Nested
    class OrderDeliveredTest {

        @DisplayName("배달 중(DELIVERING)에서 배달 완료(DELIVERED)로 변경된다")
        @Test
        void deliveringOrderCanBeDeliveredTest() {

            // given
            Order order = createOrder(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERING);
            orderRepository.save(order);

            // when
            order = orderService.completeDelivery(order.getId());

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("주문의 상태가 배달 중(DELIVERING)이 아니면 배달 완료(DELIVERED)가 불가능하다")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "COMPLETED"})
        void deliveringOrderShouldNotBeDeliveredTest(OrderStatus orderStatus) {
            // given
            Order ordered = createOrder(OrderType.DELIVERY);

            // when
            ordered.setStatus(orderStatus);
            orderRepository.save(ordered);

            // then
            assertThatThrownBy(() -> orderService.completeDelivery(ordered.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }


    @DisplayName("주문이 완료(COMPLETED)될 때")
    @Nested
    class OrderCompletedTest {

        @Test
        @DisplayName("배달 주문은 배달 완료(DELIVERED) 상태에서 완료(COMPLETED)로 변경된다")
        void completedOrderCanBeCompletedTest() {
            // given
            Order ordered = createOrder(OrderType.DELIVERY);
            ordered.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(ordered);

            // when
            ordered = orderService.complete(ordered.getId());

            // then
            assertThat(ordered.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("테이크아웃 주문은 제공(SERVED) 상태에서 완료(COMPLETED)로 변경된다")
        void servedTakeOutOrderCanBeCompletedTest() {
            // given
            Order ordered = createOrder(OrderType.TAKEOUT);
            ordered.setStatus(OrderStatus.SERVED);
            orderRepository.save(ordered);

            // when
            ordered = orderService.complete(ordered.getId());

            // then
            assertThat(ordered.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("매장 내 식사 주문은 제공(SERVED) 상태에서 완료(COMPLETED)로 변경된다")
        void servedEatInOrderCanBeCompletedTest() {
            // given
            Order ordered = createOrder(OrderType.EAT_IN);
            ordered.setStatus(OrderStatus.SERVED);
            orderRepository.save(ordered);

            // when
            ordered = orderService.complete(ordered.getId());

            // then
            assertThat(ordered.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("매장 내 식사 주문이 완료되면 테이블이 비어있어야 한다")
        void orderTableShouldUnOccupiedWhenOrderCompletedTest() {

            // given
            Order ordered = createOrder(OrderType.EAT_IN);
            ordered.setStatus(OrderStatus.SERVED);
            orderRepository.save(ordered);

            // when
            ordered = orderService.complete(ordered.getId());

            // then
            assertThat(ordered.getOrderTable().isOccupied()).isFalse();
            assertThat(ordered.getOrderTable().getNumberOfGuests()).isZero();
        }
    }

    private Order createOrder(OrderType orderType) {
        Menu menu = menuProductSteps.메뉴를_생성한다();
        return orderSteps.주문한다(menu, orderType);
    }

}
