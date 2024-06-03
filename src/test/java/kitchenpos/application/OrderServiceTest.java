package kitchenpos.application;

import kitchenpos.application.fixture.*;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("OrderService.create 메서드 테스트")
    @Nested
    class create {
        @DisplayName("주문을 등록할 수 있다.")
        @Test
        void create() {
            // given
            Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, 2, BigDecimal.valueOf(20_000), true);
            mockMenuRepositoryForOrderRequest(request);
            Order savedOrder = OrderFixture.createSavedOrder(request);
            given(orderRepository.save(any())).willReturn(savedOrder);

            // when
            Order actual = orderService.create(request);

            // then
            assertThat(actual.getId()).isNotNull();
        }

        @DisplayName("주문의 타입이 NULL 일 수 없다.(IllegalArgumentException)")
        @NullSource
        @ParameterizedTest
        void orderTypeCannotBeNull(OrderType type) {
            // given
            Order request = OrderFixture.createOrderRequest(type, 0, BigDecimal.ZERO, true);

            // when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 메뉴가 없을 시 주문을 할 수 없다.(IllegalArgumentException)")
        @NullAndEmptySource
        @ParameterizedTest
        void cannotOrderWithoutMenuItems(List<OrderLineItem> items) {
            // given
            Order request = new Order();
            request.setType(OrderType.TAKEOUT);
            request.setOrderLineItems(items);

            // when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 메뉴와 등록된 메뉴가 일치하지 않으면 주문할 수 없다.(IllegalArgumentException)")
        @Test
        void cannotOrderWithMismatchedMenus() {
            // given
            Product product = ProductFixture.createProduct(BigDecimal.valueOf(9_000), "Product");
            MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 2);

            Menu menu1 = MenuFixture.createMenu(menuProduct, MenuGroupFixture.createMenuGroup(), 20_000, "Menu1", true);
            Menu menu2 = MenuFixture.createMenu(menuProduct, MenuGroupFixture.createMenuGroup(), 20_000, "Menu2", true);

            OrderLineItem orderLineItem1 = OrderFixture.createOrderLineItem(menu1, 2, BigDecimal.valueOf(20_000));
            OrderLineItem orderLineItem2 = OrderFixture.createOrderLineItem(menu2, 2, BigDecimal.valueOf(20_000));

            Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, List.of(orderLineItem1, orderLineItem2));
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu1));

            // when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 메뉴가 비노출 상태이면 주문할 수 없다.(IllegalStateException)")
        @Test
        void cannotOrderWithHiddenMenu() {
            // given
            Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, 2, BigDecimal.valueOf(20_000), false);
            mockMenuRepositoryForOrderRequest(request);

            // when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문 시 메뉴의 금액과 현재 등록된 메뉴 금액이 일치하여야지만 주문이 가능하다.(IllegalArgumentException)")
        @Test
        void cannotOrderWithPriceMismatch() {
            // given
            Order request = OrderFixture.createOrderRequest(OrderType.TAKEOUT, 2, BigDecimal.valueOf(19_000), true);
            mockMenuRepositoryForOrderRequest(request);

            // when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문타입(OrderType)이 배달(DELIVERY)인 경우 주소정보를 반드시 포함하여야 한다.(IllegalArgumentException)")
        @NullAndEmptySource
        @ParameterizedTest
        void deliveryOrderMustIncludeAddress(String address) {
            // given
            Order request = OrderFixture.createOrderRequest(OrderType.DELIVERY, 2, BigDecimal.valueOf(20_000), true, address);
            mockMenuRepositoryForOrderRequest(request);

            // when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문타입(OrderType)이 매장식사(EAT_IN)인 경우 착석한 테이블에서 주문이 가능하다.(IllegalArgumentException)")
        @Test
        void eatInOrderOccupiedOrderTable() {
            // given
            OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setOccupied(false);

            Order request = OrderFixture.createOrderRequest(OrderType.DELIVERY, 2, BigDecimal.valueOf(20_000), true, orderTable);
            mockMenuRepositoryForOrderRequest(request);

            // when then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("OrderService.accept 메서드 테스트")
    @Nested
    class accept {
        @Test
        @DisplayName("주문 요청을 승인할 수 있다")
        void accept() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            orderService.accept(orderId);

            // then
            verify(kitchenridersClient, never()).requestDelivery(any(), any(), any());
            assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("배달 주문의 경우 배달 요청이 정상적으로 이루어진다.")
        void acceptOrderRequestsDeliveryForDeliveryOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, "123 Delivery St");
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            doNothing().when(kitchenridersClient).requestDelivery(orderId, BigDecimal.valueOf(40_000), "123 Delivery St");

            // when
            orderService.accept(orderId);

            // then
            verify(kitchenridersClient, times(1)).requestDelivery(orderId, BigDecimal.valueOf(40_000), "123 Delivery St");
            assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("존재하지 않는 주문 ID로 주문을 수락하려고 하면 NoSuchElementException이 발생한다.")
        void acceptOrderThrowsNoSuchElementExceptionForNonexistentOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            given(orderRepository.findById(orderId)).willReturn(Optional.empty());

            // when then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문 상태가 WAITING이 아닌 경우 IllegalStateException이 발생한다.")
        void acceptOrderThrowsIllegalStateExceptionForNonWaitingStatus() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.ACCEPTED, OrderType.TAKEOUT, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("OrderService.serve 메서드 테스트")
    @Nested
    class serve {
        @DisplayName("주문 상태가 ACCEPTED인 경우 주문 상태가 SERVED로 변경된다.")
        @Test
        void serve() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.ACCEPTED, OrderType.TAKEOUT, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            Order servedOrder = orderService.serve(orderId);

            // then
            assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        @DisplayName("주문 상태가 ACCEPTED가 아닌 경우 IllegalStateException이 발생한다.")
        void serveThrowsIllegalStateExceptionForNonAcceptedStatus() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when then
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("OrderService.startDelivery 메서드 테스트")
    @Nested
    class startDelivery {
        @DisplayName("주문 상태가 SERVED이고 타입이 DELIVERY인 경우 주문 상태가 DELIVERING으로 변경된다.")
        @Test
        void startDelivery() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.SERVED, OrderType.DELIVERY, "123 Delivery St");
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            Order deliveringOrder = orderService.startDelivery(orderId);

            // then
            assertThat(deliveringOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        @DisplayName("주문 타입이 DELIVERY가 아닌 경우 IllegalStateException이 발생한다.")
        void startDeliveryThrowsIllegalStateExceptionForNonDeliveryType() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.SERVED, OrderType.TAKEOUT, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when then
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문 상태가 SERVED가 아닌 경우 IllegalStateException이 발생한다.")
        void startDeliveryThrowsIllegalStateExceptionForNonServedStatus() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.ACCEPTED, OrderType.DELIVERY, "123 Delivery St");
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when then
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("OrderService.completeDelivery 메서드 테스트")
    @Nested
    class completeDelivery {
        @DisplayName("주문 상태가 DELIVERING인 경우 주문 상태가 DELIVERED로 변경된다.")
        @Test
        void completeDelivery() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.DELIVERING, OrderType.DELIVERY, "123 Delivery St");
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            Order deliveredOrder = orderService.completeDelivery(orderId);

            // then
            assertThat(deliveredOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("주문 상태가 DELIVERING이 아닌 경우 IllegalStateException이 발생한다.")
        @Test
        void completeDeliveryThrowsIllegalStateExceptionForNonDeliveringStatus() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.SERVED, OrderType.DELIVERY, "123 Delivery St");
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when then
            assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("OrderService.complete 메서드 테스트")
    @Nested
    class complete {
        @DisplayName("주문 타입이 EAT_IN 일 경우 주문 테이블의 상태가 초기화 된다.")
        @Test
        void complete_EAT_IN() {
            // given
            UUID orderId = UUID.randomUUID();
            OrderTable orderTable = new OrderTable();
            orderTable.setId(UUID.randomUUID());
            orderTable.setNumberOfGuests(2);
            orderTable.setOccupied(true);

            Order order = OrderFixture.createOrderWithTable(OrderStatus.SERVED, OrderType.EAT_IN, orderTable);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).willReturn(false);

            // when
            Order completedOrder = orderService.complete(orderId);

            // then
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(order.getOrderTable().getNumberOfGuests()).isEqualTo(0);
            assertThat(order.getOrderTable().isOccupied()).isFalse();
        }

        @DisplayName("주문 타입이 DELIVERY이고 상태가 DELIVERED일 때, 주문 상태가 COMPLETED로 변경된다.")
        @Test
        void complete_DELIVERY() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.DELIVERED, OrderType.DELIVERY, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            Order completedOrder = orderService.complete(orderId);

            // then
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("주문 타입이 TAKEOUT 또는 EAT_IN이고 상태가 SERVED일 때, 주문 상태가 COMPLETED로 변경된다.")
        @Test
        void complete_EAT_IN_TAKEOUT() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.SERVED, OrderType.TAKEOUT, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when
            Order completedOrder = orderService.complete(orderId);

            // then
            assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @DisplayName("DELIVERY 주문 상태가 DELIVERED가 아닌 경우 IllegalStateException이 발생한다.")
        @Test
        void nonDeliveredStatusInDeliveryOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.SERVED, OrderType.DELIVERY, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when then
            assertThatThrownBy(() -> orderService.complete(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("TAKEOUT 또는 EAT_IN 주문 상태가 SERVED가 아닌 경우 IllegalStateException이 발생한다.")
        @Test
        void nonServedStatusInTakeoutOrEatInOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = OrderFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, null);
            given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

            // when then
            assertThatThrownBy(() -> orderService.complete(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문 항목을 반환한다.")
    @Test
    void findAll() {
        // given
        List<Order> mockOrders = OrderFixture.createMockOrders();
        given(orderRepository.findAll()).willReturn(mockOrders);

        // when
        List<Order> orders = orderService.findAll();

        // then
        assertThat(orders).isEqualTo(mockOrders);
    }

    private void mockMenuRepositoryForOrderRequest(Order request) {
        List<Menu> menus = request.getOrderLineItems().stream()
                .map(OrderLineItem::getMenu)
                .toList();
        given(menuRepository.findAllByIdIn(any())).willReturn(menus);
        given(menuRepository.findById(any())).willReturn(Optional.of(menus.get(0)));
    }
}
