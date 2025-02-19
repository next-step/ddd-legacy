package kitchenpos.application;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.transaction.Transactional;
import kitchenpos.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private OrderTableRepository orderTableRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private OrderService orderService;
    private Menu menu;
    private MenuGroup menuGroup;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        menuRepository.deleteAll();
        orderTableRepository.deleteAll();
        menuGroupRepository.deleteAll();

        menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        menuGroupRepository.save(menuGroup);

        menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        menu.setName("후라이드치킨");
        menu.setPrice(BigDecimal.valueOf(16000));
        menu.setDisplayed(true);
        menuRepository.save(menu);

        orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(4);
        orderTableRepository.save(orderTable);
    }

    private OrderLineItem createOrderLineItem(Menu menu, int quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    private Order createOrder(OrderType type, OrderStatus status, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        return orderRepository.save(order);
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class ChangeOrderStatusTest {

        @Test
        @DisplayName("주문을 수락할 수 있다.")
        void acceptOrder() {
            Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING, List.of(createOrderLineItem(menu, 1, BigDecimal.valueOf(16000))));

            Order result = orderService.accept(order.getId());

            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("존재하지 않는 주문을 수락하면 NoSuchElementException 예외 발생")
        void cannotAcceptNonExistentOrder() {
            UUID nonExistentOrderId = UUID.randomUUID();

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.accept(nonExistentOrderId));
        }
    }

    @Nested
    @DisplayName("주문 완료")
    class CompleteOrderTest {
        @Test
        @DisplayName("존재하지 않는 주문을 완료하면 NoSuchElementException 예외 발생")
        void cannotCompleteNonExistentOrder() {
            UUID nonExistentOrderId = UUID.randomUUID();

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.complete(nonExistentOrderId));
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class FindOrderTest {

        @Test
        @DisplayName("모든 주문을 조회할 수 있다.")
        void findAll() {
            Order order1 = createOrder(OrderType.DELIVERY, OrderStatus.WAITING, List.of(createOrderLineItem(menu, 1, BigDecimal.valueOf(16000))));
            Order order2 = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, List.of(createOrderLineItem(menu, 2, BigDecimal.valueOf(32000))));

            List<Order> result = orderService.findAll();

            Assertions.assertThat(result).hasSize(2);
            Assertions.assertThat(result).extracting(Order::getId).containsExactlyInAnyOrder(order1.getId(), order2.getId());
        }
    }


    @Nested
    @DisplayName("배달 주문")
    class DeliveryOrderTest {

        @Test
        @DisplayName("배달 주문을 정상적으로 생성할 수 있다.")
        void createDeliveryOrder() {
            OrderLineItem orderLineItem = createOrderLineItem(menu, 1, BigDecimal.valueOf(16000));
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.WAITING);
            order.setDeliveryAddress("삼성역");
            order.setOrderLineItems(List.of(orderLineItem));

            Order result = orderService.create(order);

            Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
            Assertions.assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("배달 주문 시 배송 주소가 없으면 IllegalArgumentException 예외 발생한다.")
        void cannotCreateDeliveryOrderWithoutAddress(String invalidAddress) {
            Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setDeliveryAddress(invalidAddress);
            order.setOrderLineItems(List.of(createOrderLineItem(menu, 1, BigDecimal.valueOf(16000))));

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(order));
        }

        @Test
        @DisplayName("배달 주문이 ACCEPTED → SERVED → DELIVERING → DELIVERED → COMPLETED 변경된다.")
        void changeStatusForDeliveryOrder() {
            OrderLineItem orderLineItem = createOrderLineItem(menu, 1, BigDecimal.valueOf(16000));
            Order order = createOrder(OrderType.DELIVERY, OrderStatus.WAITING, List.of(orderLineItem));
            order.setDeliveryAddress("삼성역");

            Order acceptedOrder = orderService.accept(order.getId());
            Assertions.assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

            Order servedOrder = orderService.serve(order.getId());
            Assertions.assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);

            Order deliveringOrder = orderService.startDelivery(order.getId());
            Assertions.assertThat(deliveringOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);

            Order deliveredOrder = orderService.completeDelivery(order.getId());
            Assertions.assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);

            Order completedOrder = orderService.complete(order.getId());
            Assertions.assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("포장 주문")
    class TakeoutOrderTest {

        @Test
        @DisplayName("포장 주문을 정상적으로 생성할 수 있다.")
        void createTakeoutOrder() {
            Order order = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, List.of(createOrderLineItem(menu, 1, BigDecimal.valueOf(16000))));

            Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            Assertions.assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT);
        }

        @Test
        @DisplayName("포장 주문 시 수량이 0보다 작으면 IllegalArgumentException 예외 발생한다.")
        void cannotCreateTakeoutOrderWithNegativeQuantity() {
            OrderLineItem orderLineItem = createOrderLineItem(menu, -1, BigDecimal.valueOf(16000));

            Order order = new Order();
            order.setType(OrderType.TAKEOUT);
            order.setOrderLineItems(List.of(orderLineItem));

            Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(order));
        }
        @Test
        @DisplayName("포장 주문이 ACCEPTED → SERVED → COMPLETED 변경된다.")
        void changeStatusForTakeoutOrder() {
            OrderLineItem orderLineItem = createOrderLineItem(menu, 1, BigDecimal.valueOf(16000));
            Order order = createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, List.of(orderLineItem));


            Order acceptedOrder = orderService.accept(order.getId());
            Assertions.assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            Order servedOrder = orderService.serve(order.getId());
            Assertions.assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
            Order completedOrder = orderService.complete(order.getId());
            Assertions.assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }


    @Nested
    @DisplayName("매장 식사 주문")
    class EatInOrderTest {

        @Test
        @DisplayName("매장 식사 주문을 정상적으로 생성할 수 있다.")
        void createEatInOrder() {
            Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING, List.of(createOrderLineItem(menu, 1, BigDecimal.valueOf(16000))));
            order.setOrderTable(orderTable);

            Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            Assertions.assertThat(order.getType()).isEqualTo(OrderType.EAT_IN);
        }

        @Test
        @DisplayName("매장 식사 주문 시 OrderTable이 존재하지 않으면 NoSuchElementException 예외 발생한다")
        void cannotCreateEatInOrderWithoutTable() {

            OrderLineItem orderLineItem = createOrderLineItem(menu, 1, BigDecimal.valueOf(16000));
            Order order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setOrderTableId(UUID.randomUUID());
            order.setOrderLineItems(List.of(orderLineItem));

            Assertions.assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.create(order));
        }

        @Test
        @DisplayName("매장 식사 주문 시 주문테이블이 빈테이블이면 IllegalStateException 예외 발생한다")
        void cannotCreateEatInOrderIfTableNotOccupied() {

            OrderLineItem orderLineItem = createOrderLineItem(menu, 1, BigDecimal.valueOf(16000));
            orderTable.setOccupied(false);
            orderTable = orderTableRepository.save(orderTable);

            Order order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setOrderTable(orderTable);
            order.setOrderTableId(orderTable.getId());
            order.setOrderLineItems(List.of(orderLineItem));

            Assertions.assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.create(order));
        }

        @Test
        @DisplayName("매장 식사 주문이 ACCEPTED → SERVED → COMPLETED 변경된다.")
        void changeStatusForEatInOrder() {
            OrderLineItem orderLineItem = createOrderLineItem(menu, 1, BigDecimal.valueOf(16000));
            orderTable.setOccupied(true);
            orderTable = orderTableRepository.save(orderTable);
            Order order = createOrder(OrderType.EAT_IN, OrderStatus.WAITING, List.of(orderLineItem));
            order.setOrderTable(orderTable);
            order.setOrderTableId(orderTable.getId());
            order = orderRepository.save(order);


            Order acceptedOrder = orderService.accept(order.getId());
            Assertions.assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            Order servedOrder = orderService.serve(order.getId());
            Assertions.assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
            Order completedOrder = orderService.complete(order.getId());
            Assertions.assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }


}