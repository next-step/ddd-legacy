package kitchenpos.application;

import kitchenpos.FixtureFactory;
import kitchenpos.IntegrationTest;
import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OrderServiceTest extends IntegrationTest {

    private Menu basicMenu;
    private Menu seasonedMenu;

    @BeforeEach
    void setup() {
        MenuGroup menuGroup = menuGroupRepository.save(FixtureFactory.createMenuGroup("추천메뉴"));
        Product seasonedChicken = FixtureFactory.createProduct("양념치킨", BigDecimal.valueOf(16000));
        Product friedChicken = FixtureFactory.createProduct("후라이드치킨", BigDecimal.valueOf(16000));
        Product soySauceChicken = FixtureFactory.createProduct("간장치킨", BigDecimal.valueOf(16000));
        List<Product> productList = productRepository.saveAll(List.of(seasonedChicken, friedChicken, soySauceChicken));

        basicMenu = menuRepository.save(FixtureFactory.createMenu("후라이드 + 후라이드", BigDecimal.valueOf(27000), true, menuGroup, toMenuProductList(friedChicken, friedChicken)));
        seasonedMenu = menuRepository.save(FixtureFactory.createMenu("후라이드 + 양념", BigDecimal.valueOf(28000), true, menuGroup, toMenuProductList(seasonedChicken, friedChicken)));
    }

    @DisplayName("메뉴를 주문할 때")
    @Nested
    class CreateOrderTest {

        @DisplayName("메뉴 주문에는 주문할 메뉴와 수량이 있는 주문 정보가 공백일 수 없다.")
        @NullAndEmptySource
        @ParameterizedTest
        void create_order_line_fail(List<OrderLineItem> items) {
            Order order = FixtureFactory.createTakeOutOrder(items);
            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 항목이 존재하지 않습니다.");
        }

        @DisplayName("메뉴 주문에 있는 메뉴들은 반드시 가게에 있는 메뉴들과 일치해야 한다.")
        @Test
        void create_order_line_fail() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            OrderLineItem orderLineItem2 = FixtureFactory.createOrderLineItem(seasonedMenu, seasonedMenu.getPrice(), 1L);
            Order order = FixtureFactory.createTakeOutOrder(List.of(orderLineItem, orderLineItem2, orderLineItem));
            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 항목과 메뉴가 일치하지 않습니다.");
        }

        @DisplayName("매장 주문이 아닐 때, 주문 수량이 음수일 수 없다.")
        @Test
        void create_order_negative_quantity() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), -1L);
            Order order = FixtureFactory.createTakeOutOrder(List.of(orderLineItem));
            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 수량은 0 이상이어야 합니다.");
        }

        @DisplayName("주문한 메뉴는 고객에게 보이는 메뉴가 아니라면 예외가 발생한다.")
        @Test
        void create_order_menu_visible() {
            Menu menu = FixtureFactory.createMenu("후라이드 + 후라이드", BigDecimal.valueOf(27000), false, basicMenu.getMenuGroup(), toMenuProductList(basicMenu.getMenuProducts().get(0).getProduct(), basicMenu.getMenuProducts().get(1).getProduct()));
            menuRepository.save(menu);
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(menu, menu.getPrice(), 1L);
            Order order = FixtureFactory.createTakeOutOrder(List.of(orderLineItem));
            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("메뉴가 노출되지 않았습니다.");
        }

        @DisplayName("주문 항목 가격과 가게에 있는 메뉴와 가격이 일치해야 한다.")
        @Test
        void create_order_not_equal_menu_price() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice().add(BigDecimal.valueOf(1000)), 1L);
            Order order = FixtureFactory.createTakeOutOrder(List.of(orderLineItem));
            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 항목 가격과 메뉴 가격이 일치하지 않습니다.");
        }

        @DisplayName("배달 주문이라면, 반드시 주소가 존재해야 한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void create_order_delivery_address(String address) {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createDeliveryOrder(address, List.of(orderLineItem));
            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("배달 주소가 존재하지 않습니다.");
        }

        @DisplayName("매장 주문이라면, 테이블은 반드시 공석이 아니여야 한다.")
        @Test
        void create_order_table_not_empty() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            OrderTable orderTable = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", 0, false));

            Order order = FixtureFactory.createEatInOrder(orderTable, List.of(orderLineItem));

            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("테이블이 비어있습니다.");
        }

        @DisplayName("배달 주문을 성공적으로 진행한다.")
        @Test
        void create_order_delivery_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            String address = "광주광역시";
            Order order = FixtureFactory.createDeliveryOrder(address, List.of(orderLineItem));

            Order savedOrder = orderService.create(order);

            assertThat(savedOrder.getId()).isNotNull();
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(savedOrder.getType()).isEqualTo(OrderType.DELIVERY);
            assertThat(savedOrder.getDeliveryAddress()).isEqualTo(address);
        }

        @DisplayName("포장 주문을 성공적으로 진행한다.")
        @Test
        void create_order_take_out_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createTakeOutOrder(List.of(orderLineItem));

            Order savedOrder = orderService.create(order);

            assertThat(savedOrder.getId()).isNotNull();
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(savedOrder.getType()).isEqualTo(OrderType.TAKEOUT);
        }

        @DisplayName("매장 주문을 성공적으로 진행한다.")
        @Test
        void create_order_eat_in_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            OrderTable table1 = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", 4, true));
            Order order = FixtureFactory.createEatInOrder(table1, List.of(orderLineItem));

            Order savedOrder = orderService.create(order);

            assertThat(savedOrder.getId()).isNotNull();
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(savedOrder.getType()).isEqualTo(OrderType.EAT_IN);
        }

        @DisplayName("메뉴 주문은 반드시 매장, 배달, 포장 주문 중 하나이다.")
        @NullSource
        @ParameterizedTest
        void create_order_type_null(OrderType type) {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createOrder(type, OrderStatus.WAITING, null, null, null, List.of(orderLineItem));

            assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문할 수 없는 주문 타입입니다.");
        }
    }

    @Nested
    @DisplayName("주문을 수락할 떄")
    class AcceptOrderTest {

        @DisplayName("주문 대기 상태가 아니면 예외가 발생한다.")
        @Test
        void accept_order_not_waiting() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED, null, null, null, List.of(orderLineItem));
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문 대기 상태가 아닙니다.");
        }

        @DisplayName("주문을 성공적으로 수락한다.")
        @Test
        void accept_order_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, null, null, null, List.of(orderLineItem));
            orderRepository.save(order);

            Order acceptedOrder = orderService.accept(order.getId());

            assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }

    @Nested
    @DisplayName("주문을 제공할 때")
    class ServeOrderTest {

        @DisplayName("주문 대기 상태가 아니면 예외가 발생한다.")
        @Test
        void serve_order_not_waiting() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createOrder(OrderType.TAKEOUT, OrderStatus.SERVED, null, null, null, List.of(orderLineItem));
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문 대기 상태가 아닙니다.");
        }

        @DisplayName("주문을 성공적으로 제공한다.")
        @Test
        void serve_order_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createOrder(OrderType.TAKEOUT, OrderStatus.ACCEPTED, null, null, null, List.of(orderLineItem));
            orderRepository.save(order);

            Order servedOrder = orderService.serve(order.getId());

            assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @Nested
    @DisplayName("배달 주문에서 메뉴 제공을 할 떄")
    class DeliverOrderTest {

        @DisplayName("배달 주문이 아니면 예외가 발생한다.")
        @Test
        void deliver_order_not_delivery() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = orderRepository.save(FixtureFactory.createTakeOutOrder(OrderStatus.SERVED, List.of(orderLineItem)));

            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("배달 주문이 아닙니다.");
        }

        @DisplayName("주문을 성공적으로 배달한다.")
        @Test
        void deliver_order_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createDeliveryOrder("광주광역시", OrderStatus.SERVED, List.of(orderLineItem));
            orderRepository.save(order);

            Order deliveredOrder = orderService.startDelivery(order.getId());
            assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("배달이 성공적으로 완료된다")
        @Test
        void deliver_order_complete_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createDeliveryOrder("광주광역시", OrderStatus.DELIVERING, List.of(orderLineItem));
            orderRepository.save(order);

            Order deliveredOrder = orderService.completeDelivery(order.getId());
            assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Nested
    @DisplayName("주문이 완료된다.")
    class CompleteOrderTest {

        @DisplayName("배달 주문이면 배달 완료 상태에서만 완료가 가능하다.")
        @Test
        void complete_order_delivery_not_delivered() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createDeliveryOrder("광주광역시", OrderStatus.DELIVERING, List.of(orderLineItem));
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("배달이 완료되지 않았습니다.");
        }

        @DisplayName("매장 주문에서는 제공 완료 상태에서만 완료가 가능하다.")
        @Test
        void complete_order_takeout_not_served() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            OrderTable table1 = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", 4, true));
            Order order = FixtureFactory.createEatInOrder(OrderStatus.ACCEPTED, table1, List.of(orderLineItem));
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문이 제공되지 않았습니다.");
        }

        @DisplayName("포장 주문에서는 제공 완료 상태에서만 완료가 가능하다.")
        @Test
        void complete_order_packaging_not_served() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            Order order = FixtureFactory.createTakeOutOrder(OrderStatus.ACCEPTED, List.of(orderLineItem));
            orderRepository.save(order);

            assertThatThrownBy(() -> orderService.complete(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("주문이 제공되지 않았습니다.");
        }

        @DisplayName("매장 주문에서 완료되면 주문 테이블이 비워진다.")
        @Test
        void complete_order_eat_in_success() {
            OrderLineItem orderLineItem = FixtureFactory.createOrderLineItem(basicMenu, basicMenu.getPrice(), 1L);
            OrderTable table1 = orderTableRepository.save(FixtureFactory.createOrderTable("테이블1", 4, true));
            Order order = FixtureFactory.createEatInOrder(OrderStatus.SERVED, table1, List.of(orderLineItem));
            orderRepository.save(order);

            Order completedOrder = orderService.complete(order.getId());

            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(completedOrder.getOrderTable().isOccupied()).isFalse();
        }


    }

}
