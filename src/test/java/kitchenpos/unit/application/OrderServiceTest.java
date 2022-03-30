package kitchenpos.unit.application;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.testdouble.MenuStubRepository;
import kitchenpos.testdouble.OrderStubRepository;
import kitchenpos.testdouble.OrderTableStubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderStubRepository();
        menuRepository = new MenuStubRepository();
        orderTableRepository = new OrderTableStubRepository();
        kitchenridersClient = mock(KitchenridersClient.class);
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateTest {

        @Nested
        @DisplayName("주문 생성 실패 테스트")
        class OrderFail {
            @DisplayName("주문 타입 없이는 주문을 생성할 수 없다.")
            @Test
            void withoutOrderType() {
                // Arrange
                Order order = createOrder();

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("주문에 주문 메뉴가 없다면 주문을 생성할 수 없다.")
            @Test
            void withoutOrderLineItem() {
                // Arrange
                Order order = createDeliveryOrder();

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("주문이 가진 주문 메뉴의 개수와 등록된 메뉴의 개수가 다르면 주문을 생성할 수 없다.")
            @Test
            void orderLineItemCountDiffMenuSize() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menuRepository.save(menu);

                Order order = createDeliveryOrder();
                OrderLineItem orderLineItem1 = new OrderLineItem();
                OrderLineItem orderLineItem2 = new OrderLineItem();
                orderLineItem1.setMenuId(menu.getId());
                order.setOrderLineItems(Arrays.asList(orderLineItem1, orderLineItem2));

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("주문 타입이 배달이면서 주문이 가진 주문 메뉴의 수량이 0보다 작을때 주문을 생성할 수 없다.")
            @Test
            void orderTypeDeliveryQuantityUnderZero() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menuRepository.save(menu);

                Order order = createDeliveryOrder();
                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(-1);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("주문 타입이 포장이면서 주문이 가진 주문 메뉴의 수량이 0보다 작을때 주문을 생성할 수 없다.")
            @Test
            void orderTypeTakeOutQuantityUnderZero() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menuRepository.save(menu);

                Order order = createTakeOutOrder();
                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(-1);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("주문 메뉴에 등록된 메뉴가 전시중인 메뉴가 아니라면 주문을 생성할 수 없다.")
            @Test
            void orderLineItemMenuUnDisplayed() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(false);
                menuRepository.save(menu);

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());

                Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOfAny(IllegalStateException.class);
            }

            @DisplayName("주문 메뉴에 등록된 메뉴의 가격과 주문 제품의 가격이 다르면 주문을 생성할 수 없다.")
            @Test
            void menuPriceDiffOrderItemPrice() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.TEN);
                menuRepository.save(menu);

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setPrice(BigDecimal.ONE);

                Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOfAny(IllegalArgumentException.class);
            }

            @DisplayName("주문 타입이 배달일때 배달 주소가 없다면 주문을 생성할 수 없다.")
            @ParameterizedTest
            @NullAndEmptySource
            void orderTypeDeliveryAndAddressNull(String address) {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.TEN);
                menuRepository.save(menu);

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setPrice(BigDecimal.TEN);

                Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));
                order.setDeliveryAddress(address);

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOfAny(IllegalArgumentException.class);
            }

            @DisplayName("주문 타입이 매장 식사 일때 주문의 주문테이블이 등록되어 있지 않다면 주문을 생성할 수 없다.")
            @Test
            void orderTypeEatInAndOrderTableNonRegistered() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.TEN);
                menuRepository.save(menu);

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setPrice(BigDecimal.TEN);

                Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.create(order)).isInstanceOfAny(NoSuchElementException.class);
            }
        }

        @Nested
        @DisplayName("주문 생성 성공 테스트")
        class OrderSuccess {

            @DisplayName("주문 타입이 매장 식사일 때 주문 성공")
            @Test
            void createAtOrderTypeEatIn() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.TEN);
                menuRepository.save(menu);

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setPrice(BigDecimal.TEN);

                OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTableRepository.save(orderTable);

                Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));
                order.setOrderTableId(orderTable.getId());

                // Act
                Order result = orderService.create(order);

                // Assert
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getOrderDateTime()).isNotNull(),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING)
                );
            }

            @DisplayName("주문 타입이 포장일 때 주문 성공")
            @Test
            void orderTypeTakeOut() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.TEN);
                menuRepository.save(menu);

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setPrice(BigDecimal.TEN);

                OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTableRepository.save(orderTable);

                Order order = new Order();
                order.setType(OrderType.TAKEOUT);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));
                order.setOrderTableId(orderTable.getId());

                // Act
                Order result = orderService.create(order);

                // Assert
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getOrderDateTime()).isNotNull(),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING)
                );
            }

            @DisplayName("주문 타입이 배달일 때 주문 성공")
            @Test
            void orderTypeDelivery() {
                // Arrange
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.TEN);
                menuRepository.save(menu);

                OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setPrice(BigDecimal.TEN);

                OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTableRepository.save(orderTable);

                Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setOrderLineItems(Collections.singletonList(orderLineItem));
                order.setOrderTableId(orderTable.getId());
                order.setDeliveryAddress("address");

                // Act
                Order result = orderService.create(order);

                // Assert
                assertAll(
                        () -> assertThat(result.getId()).isNotNull(),
                        () -> assertThat(result.getOrderDateTime()).isNotNull(),
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING)
                );
            }
        }
    }

    @Nested
    @DisplayName("주문 수락 테스트")
    class OrderAcceptTest {

        @Nested
        @DisplayName("주문 수락 실패 테스트")
        class FailOrderAccept {

            @DisplayName("등록되지 않은 주문 요청시 예외가 발생한다.")
            @Test
            void request_with_unregistered_order() {
                // Arrange
                // Act
                // Assert
                assertThatThrownBy(() -> orderService.accept(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("요청한 주문의 상태가 대기중이 아니라면 예외가 발생한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, names = "WAITING", mode = EnumSource.Mode.EXCLUDE)
            void request_order_is_not_waiting(OrderStatus status) {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setStatus(status);
                Order save = orderRepository.save(order);

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.accept(save.getId())).isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("주문 수락 성공 테스트")
        class SuccessOrderAccept {

            @DisplayName("배달 주문을 수락할 경우, 라이더를 호출한다.")
            @Test//call a rider to accept a delivery order

            void call_riders_to_accept_delivery_order() {
                // Arrange
                Order order = createOrder(UUID.randomUUID(), OrderType.DELIVERY, OrderStatus.WAITING, LocalDateTime.now(), Collections.emptyList(), "address", null, null);
                Order save = orderRepository.save(order);

                // Act
                orderService.accept(save.getId());

                // Assert
                verify(kitchenridersClient, times(1)).requestDelivery(save.getId(), BigDecimal.ZERO, "address");
            }
            
            @DisplayName("대기중인 상태의 주문을 수락할 경우 주문 상태가 주문 수락으로 변경된다.")
            @Test
            void waiting_order_accept_order_status_change_accepted() {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setStatus(OrderStatus.WAITING);
                Order save = orderRepository.save(order);

                // Act
                Order result = orderService.accept(save.getId());

                // Assert
                assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }
        }
    }

    @DisplayName("주문 서빙 테스트")
    @Nested
    class ServeTest {

        @DisplayName("주문 서빙 성공 테스트")
        @Nested
        class SuccessServe {
            @DisplayName("수락 상태의 주문을 서빙 하면 서빙됨으로 주문 상태가 변경된다.")
            @Test
            void accept_order_serve_order_status_change_served() {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setStatus(OrderStatus.ACCEPTED);
                Order save = orderRepository.save(order);

                // Act
                Order result = orderService.serve(save.getId());

                // Assert
                assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
            }
        }

        @DisplayName("주문 서빙 실패 테스트")
        @Nested
        class FailServe {

            @DisplayName("등록된 주문이 아니라면 예외가 발생한다.")
            @Test
            void request_with_unregistered_order() {
                // Arrange
                // Act
                // Assert
                assertThatThrownBy(() -> orderService.serve(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("요청한 주문의 상태가 수락이 아닌경우 예외가 발생한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, names = "ACCEPTED", mode = EnumSource.Mode.EXCLUDE)
            void request_order_is_not_accept(OrderStatus status) {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setStatus(status);

                Order save = orderRepository.save(order);

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.serve(save.getId())).isInstanceOf(IllegalStateException.class);
            }
        }
    }

    @DisplayName("배달 시작 테스트")
    @Nested
    class StartDeliveryTest {

        @DisplayName("배달 시작 실패 테스트.")
        @Nested
        class FailStartDelivery {
            @DisplayName("등록되지 않은 주문 요청시 배달 시작이 실패한다.")
            @Test
            void request_with_unregistered_order() {
                // Arrange
                // Act
                // Assert
                assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("주문의 타입이 배달이 아닌 주문을 요청하면 배달 시작이 실패한다.")
            @ParameterizedTest
            @EnumSource(value = OrderType.class, names = "DELIVERY", mode = EnumSource.Mode.EXCLUDE)
            void delivery_start_fail_order_type_is_not_delivery(OrderType orderType) {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setType(orderType);
                order.setStatus(OrderStatus.SERVED);
                Order save = orderRepository.save(order);

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.startDelivery(save.getId())).isInstanceOf(IllegalStateException.class);
            }

            @DisplayName("주문 상태가 서빙됨이 아니라면 배달 시작이 실패한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
            void delivery_start_fail_order_status_is_not_served(OrderStatus status) {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setType(OrderType.DELIVERY);
                order.setStatus(status);
                Order save = orderRepository.save(order);

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.startDelivery(save.getId())).isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("배달 시작 성공 테스트.")
        @Nested
        class SuccessStartDelivery {

            @DisplayName("주문의 배달 시작이되면 주문의 상태가 배달중으로 변경된다.")
            @Test
            void success_start_delivery() {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setType(OrderType.DELIVERY);
                order.setStatus(OrderStatus.SERVED);
                Order save = orderRepository.save(order);

                // Act
                Order result = orderService.startDelivery(save.getId());

                // Assert
                assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
            }
        }
    }

    @DisplayName("배달 완료 테스트.")
    @Nested
    class CompleteDeliveryTest {

        @DisplayName("배달 완료 성공 테스트.")
        @Nested
        class SuccessCompleteDeliveryTest {

            @DisplayName("배달 완료에 성공하면 주문 상태가 배달됨 으로 변경된다.")
            @Test
            void order_status_change_delivered_when_complete_delivery() {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setStatus(OrderStatus.DELIVERING);
                order.setType(OrderType.DELIVERY);
                Order save = orderRepository.save(order);

                // Act
                Order result = orderService.completeDelivery(save.getId());

                // Assert
                assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            }
        }

        @DisplayName("배달 완료 실패 테스트.")
        @Nested
        class FailCompleteDeliveryTest {
            @DisplayName("등록되지 않은 주문 요청시 배달 시작이 실패한다.")
            @Test
            void request_with_unregistered_order() {
                // Arrange
                // Act
                // Assert
                assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("주문 상태가 배달중이 아닌 주문은 배달 완료에 실패한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, names = "DELIVERING", mode = EnumSource.Mode.EXCLUDE)
            void delivery_complete_fail_when_order_type_is_not_delivering(OrderStatus status) {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setStatus(status);
                Order save = orderRepository.save(order);

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.completeDelivery(save.getId())).isInstanceOf(IllegalStateException.class);
            }
        }
    }

    @DisplayName("주문 완료 테스트.")
    @Nested
    class OrderCompleteTest {

        @DisplayName("주문 완료 성공 테스트.")
        @Nested
        class SuccessOrderComplete {
            @DisplayName("배달 주문 완료에 성공하면 주문 상태가 완료로 변경된다.")
            @Test
            void success_delivery_order_complete_then_order_status_is_change_to_completed() {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setType(OrderType.DELIVERY);
                order.setStatus(OrderStatus.DELIVERED);
                Order save = orderRepository.save(order);

                // Act
                Order result = orderService.complete(save.getId());

                // Assert
                assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("포장 주문 완료에 성공하면 주문 상태가 완료로 변경된다.")
            @Test
            void success_takeout_order_complete_then_order_status_is_change_to_completed() {
                // Arrange
                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setType(OrderType.TAKEOUT);
                order.setStatus(OrderStatus.SERVED);
                Order save = orderRepository.save(order);

                // Act
                Order result = orderService.complete(save.getId());

                // Assert
                assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("매장 식사 주문 완료에 성공하면 주문 상태가 완료로 변경되고 테이블이 초기화 된다.")
            @Test
            void success_eat_in_order_complete_then_order_status_is_change_to_completed_and_order_table_reset() {
                // Arrange
                OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setName("my order table");
                orderTable.setNumberOfGuests(3);
                orderTable.setEmpty(Boolean.FALSE);
                OrderTable saveOrderTable = orderTableRepository.save(orderTable);

                Order order = createOrder();
                order.setId(UUID.randomUUID());
                order.setType(OrderType.EAT_IN);
                order.setStatus(OrderStatus.SERVED);
                order.setOrderTable(orderTable);
                order.setOrderTableId(saveOrderTable.getId());
                Order save = orderRepository.save(order);

                // Act
                Order result = orderService.complete(save.getId());

                // Assert
                assertAll(
                        () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                        () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                        () -> assertThat(orderTable.isEmpty()).isTrue()
                );
            }
        }

        @DisplayName("주문 완료 실패 테스트.")
        @Nested
        class FailOrderComplete {
            @DisplayName("등록되지 않은 주문 요청시 배달 시작이 실패한다.")
            @Test
            void request_with_unregistered_order() {
                // Arrange
                // Act
                // Assert
                assertThatThrownBy(() -> orderService.startDelivery(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("주문 타입이 배달이면서, 상태가 배달 완료가 아니라면 주문 완료에 실패한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, names = "DELIVERED", mode = EnumSource.Mode.EXCLUDE)
            void fail_order_complete_when_order_type_is_delivery_and_status_is_not_delivered(OrderStatus status) {
                // Arrange
                Order order = createOrder();
                order.setType(OrderType.DELIVERY);
                order.setStatus(status);
                Order save = orderRepository.save(order);

                // Act
                // Assert
                assertThatThrownBy(() -> orderService.complete(save.getId())).isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("주문 타입이 포장 이면서, 상태가 서빙 완료가 아니라면 주문 완료에 실패한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
        void fail_order_complete_when_order_type_is_takeout_and_status_is_not_served(OrderStatus status) {
            // Arrange
            Order order = createOrder();
            order.setType(OrderType.TAKEOUT);
            order.setStatus(status);
            Order save = orderRepository.save(order);

            // Act
            // Assert
            assertThatThrownBy(() -> orderService.complete(save.getId())).isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문 타입이 매장 식사 이면서, 상태가 서빙 완료가 아니라면 주문 완료에 실패한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = "SERVED", mode = EnumSource.Mode.EXCLUDE)
        void fail_order_complete_when_order_type_is_eat_in_and_status_is_not_served(OrderStatus status) {
            // Arrange
            Order order = createOrder();
            order.setType(OrderType.EAT_IN);
            order.setStatus(status);
            Order save = orderRepository.save(order);

            // Act
            // Assert
            assertThatThrownBy(() -> orderService.complete(save.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    private Order createTakeOutOrder() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        return order;
    }

    private Order createDeliveryOrder() {
        return createOrder(null, OrderType.DELIVERY, null, null, null, null, null, null);
    }

    private Order createOrder() {
        return createOrder(null, null, null, null, null, null, null, null);
    }

    private Order createOrder(UUID id,
                             OrderType type,
                             OrderStatus status,
                             LocalDateTime orderDateTime,
                             List<OrderLineItem> orderLineItems,
                             String deliveryAddress,
                             OrderTable orderTable,
                             UUID orderTableId) {
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        return order;
    }
}