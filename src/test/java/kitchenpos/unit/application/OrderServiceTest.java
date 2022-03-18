package kitchenpos.unit.application;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.stub.MenuStubRepository;
import kitchenpos.stub.OrderStubRepository;
import kitchenpos.stub.OrderTableStubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
        kitchenridersClient = new KitchenridersClient();
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

    public Order createOrder(UUID id,
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