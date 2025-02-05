package mission.step3;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("배달 주문을 생성한다")
        void createDeliveryOrder() {
            // given
            UUID menuId = UUID.randomUUID();
            Menu menu = createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000));

            OrderLineItem orderLineItem = createOrderLineItem(menuId, 2, BigDecimal.valueOf(4000));

            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setDeliveryAddress("서울시 강남구");
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));
            given(orderRepository.save(any(Order.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            Order created = orderService.create(request);

            // then
            assertThat(created.getId()).isNotNull();
            assertThat(created.getType()).isEqualTo(OrderType.DELIVERY);
            assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(created.getOrderDateTime()).isNotNull();
            assertThat(created.getDeliveryAddress()).isEqualTo("서울시 강남구");
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("포장 주문을 생성한다")
        void createTakeoutOrder() {
            // given
            UUID menuId = UUID.randomUUID();
            Menu menu = createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000));

            OrderLineItem orderLineItem = createOrderLineItem(menuId, 1, BigDecimal.valueOf(4000));

            Order request = new Order();
            request.setType(OrderType.TAKEOUT);
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            given(orderRepository.save(any(Order.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            Order created = orderService.create(request);

            // then
            assertThat(created.getType()).isEqualTo(OrderType.TAKEOUT);
            assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
            assertThat(created.getOrderLineItems()).hasSize(1);
            assertThat(created.getOrderLineItems().getFirst().getQuantity()).isEqualTo(1);


            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("매장 식사 주문을 생성한다")
        void createEatInOrder() {
            // given
            UUID menuId = UUID.randomUUID();
            UUID tableId = UUID.randomUUID();

            Menu menu = createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000));
            OrderTable orderTable = createOccupiedOrderTable(tableId);

            OrderLineItem orderLineItem = createOrderLineItem(menuId, 1, BigDecimal.valueOf(4000));

            Order request = new Order();
            request.setType(OrderType.EAT_IN);
            request.setOrderTableId(tableId);
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));
            given(orderTableRepository.findById(tableId))
                    .willReturn(Optional.of(orderTable));

            given(orderRepository.save(any(Order.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            Order created = orderService.create(request);

            // then
            assertThat(created.getType()).isEqualTo(OrderType.EAT_IN);
            assertThat(created.getOrderTable()).isEqualTo(orderTable);
            assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);  // 상태 검증 추가
            assertThat(created.getOrderLineItems()).hasSize(1);  // 주문 상품 검증 추가

            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("주문 타입이 없으면 예외가 발생한다")
        void createOrderWithoutType() {
            // given
            Order request = new Order();
            request.setType(null);

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문 항목이 없으면 예외가 발생한다")
        void createOrderWithoutLineItems() {
            // given
            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setOrderLineItems(List.of());

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문할 메뉴가 존재하지 않으면 예외가 발생한다")
        void createOrderWithNonExistentMenu() {
            // given
            OrderLineItem orderLineItem = createOrderLineItem(UUID.randomUUID(), 1, BigDecimal.valueOf(4000));

            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any()))
                    .willReturn(List.of());

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문할 메뉴가 판매 중이 아니면 예외가 발생한다")
        void createOrderWithNonDisplayedMenu() {
            // given
            UUID menuId = UUID.randomUUID();
            Menu menu = createMenu(menuId, "아메리카노", BigDecimal.valueOf(4000), false);

            OrderLineItem orderLineItem = createOrderLineItem(menuId, 1, BigDecimal.valueOf(4000));

            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문 항목의 가격이 메뉴 가격과 다르면 예외가 발생한다")
        void createOrderWithDifferentPrice() {
            // given
            UUID menuId = UUID.randomUUID();
            Menu menu = createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000));

            OrderLineItem orderLineItem = createOrderLineItem(menuId, 1, BigDecimal.valueOf(5000));

            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("배달 주문시 주소가 없으면 예외가 발생한다")
        void createDeliveryOrderWithoutAddress() {
            // given
            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setDeliveryAddress(null);

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("배달 주문시 주소가 공백이면 예외가 발생한다")
        void createDeliveryOrderWithEmptyAddress() {
            // given
            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setDeliveryAddress("");

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("배달/포장 주문시 수량이 0 미만이면 예외가 발생한다")
        void createOrderWithInvalidQuantity() {
            // given
            UUID menuId = UUID.randomUUID();
            OrderLineItem orderLineItem = createOrderLineItem(menuId, -1, BigDecimal.valueOf(4000));

            Order request = new Order();
            request.setType(OrderType.DELIVERY);
            request.setDeliveryAddress("서울시 강남구");
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000))));

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        // 헬퍼 메서드들
        private Menu createDisplayedMenu(UUID id, String name, BigDecimal price) {
            Menu menu = new Menu();
            menu.setId(id);
            menu.setName(name);
            menu.setPrice(price);
            menu.setDisplayed(true);
            return menu;
        }

        private OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
            OrderLineItem orderLineItem = new OrderLineItem();
            orderLineItem.setMenuId(menuId);
            orderLineItem.setQuantity(quantity);
            orderLineItem.setPrice(price);
            return orderLineItem;
        }

        @Test
        @DisplayName("매장 식사 주문시 테이블이 존재하지 않으면 예외가 발생한다")
        void createEatInOrderWithNonExistentTable() {
            // given
            UUID menuId = UUID.randomUUID();
            UUID tableId = UUID.randomUUID();
            Menu menu = createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000));
            OrderLineItem orderLineItem = createOrderLineItem(menuId, 1, BigDecimal.valueOf(4000));

            Order request = new Order();
            request.setType(OrderType.EAT_IN);
            request.setOrderTableId(tableId);
            request.setOrderLineItems(List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            // 테이블은 존재하지 않음
            given(orderTableRepository.findById(tableId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("매장 식사 주문시 테이블이 사용 중이 아니면 예외가 발생한다")
        void createEatInOrderWithNonOccupiedTable() {
            // given
            UUID menuId = UUID.randomUUID();
            UUID tableId = UUID.randomUUID();

            // 메뉴 설정
            Menu menu = createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000));
            OrderLineItem orderLineItem = createOrderLineItem(menuId, 1, BigDecimal.valueOf(4000));

            // 테이블 설정
            OrderTable orderTable = new OrderTable();
            orderTable.setId(tableId);
            orderTable.setOccupied(false);

            Order request = new Order();
            request.setType(OrderType.EAT_IN);
            request.setOrderTableId(tableId);
            request.setOrderLineItems(List.of(orderLineItem));  // 메뉴 항목 필수

            given(menuRepository.findAllByIdIn(List.of(menuId)))
                    .willReturn(List.of(menu));
            given(menuRepository.findById(menuId))
                    .willReturn(Optional.of(menu));

            given(orderTableRepository.findById(tableId))
                    .willReturn(Optional.of(orderTable));

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalStateException.class);  // IllegalArgument가 아닌 IllegalState 예외
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class ChangeOrderStatus {

        @Test
        @DisplayName("주문을 수락한다")
        void acceptOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.WAITING);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when
            Order accepted = orderService.accept(orderId);

            // then
            assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        @DisplayName("존재하지 않는 주문을 수락하면 예외가 발생한다")
        void acceptNonExistentOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            given(orderRepository.findById(orderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("대기 중이 아닌 주문을 수락하면 예외가 발생한다")
        void acceptNonWaitingOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달 주문을 수락하면 배달 기사를 요청한다")
        void acceptDeliveryOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.WAITING);
            order.setDeliveryAddress("서울시 강남구");

            Menu menu = createDisplayedMenu(UUID.randomUUID(), "아메리카노", BigDecimal.valueOf(4000));
            OrderLineItem orderLineItem = createOrderLineItem(menu.getId(), 2, BigDecimal.valueOf(4000));
            orderLineItem.setMenu(menu);
            order.setOrderLineItems(List.of(orderLineItem));

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when
            orderService.accept(orderId);

            // then
            verify(kitchenridersClient).requestDelivery(
                    orderId,
                    BigDecimal.valueOf(8000),
                    "서울시 강남구"
            );
        }

        @Test
        @DisplayName("주문을 서빙한다")
        void serveOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when
            Order served = orderService.serve(orderId);

            // then
            assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        @DisplayName("수락되지 않은 주문을 서빙하면 예외가 발생한다")
        void serveNonAcceptedOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.WAITING);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달 시작을 한다")
        void startDelivery() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when
            Order delivering = orderService.startDelivery(orderId);

            // then
            assertThat(delivering.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        @DisplayName("서빙되지 않은 주문의 배달 시작시 예외가 발생한다")
        void startDeliveryWithNonServedOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달 완료를 한다")
        void completeDelivery() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.DELIVERING);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when
            Order delivered = orderService.completeDelivery(orderId);

            // then
            assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        @DisplayName("배달 중이 아닌 주문의 배달 완료시 예외가 발생한다")
        void completeDeliveryWithNonDeliveringOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달 주문을 완료한다")
        void completeDeliveryOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when
            Order completed = orderService.complete(orderId);

            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("배달 완료되지 않은 배달 주문 완료시 예외가 발생한다")
        void completeNonDeliveredDeliveryOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERING);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.complete(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("매장/포장 주문을 완료한다")
        void completeNonDeliveryOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.TAKEOUT);
            order.setStatus(OrderStatus.SERVED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when
            Order completed = orderService.complete(orderId);

            // then
            assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("서빙되지 않은 매장/포장 주문 완료시 예외가 발생한다")
        void completeNonServedOrder() {
            // given
            UUID orderId = UUID.randomUUID();
            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.TAKEOUT);
            order.setStatus(OrderStatus.ACCEPTED);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));

            // when & then
            assertThatThrownBy(() -> orderService.complete(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("매장 주문 완료시 해당 테이블의 모든 주문이 완료되면 테이블을 비운다")
        void completeOrderAndClearTable() {
            // given
            UUID orderId = UUID.randomUUID();
            OrderTable orderTable = new OrderTable();
            orderTable.setOccupied(true);
            orderTable.setNumberOfGuests(4);

            Order order = new Order();
            order.setId(orderId);
            order.setType(OrderType.EAT_IN);
            order.setStatus(OrderStatus.SERVED);
            order.setOrderTable(orderTable);

            given(orderRepository.findById(orderId))
                    .willReturn(Optional.of(order));
            given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                    .willReturn(false);

            // when
            orderService.complete(orderId);

            // then
            assertThat(orderTable.getNumberOfGuests()).isZero();
            assertThat(orderTable.isOccupied()).isFalse();
        }
    }

    @Test
    @DisplayName("전체 주문을 조회한다")
    void findAll() {
        // given
        List<Order> orders = List.of(
                createOrder(UUID.randomUUID(), OrderType.DELIVERY),
                createOrder(UUID.randomUUID(), OrderType.TAKEOUT)
        );
        given(orderRepository.findAll())
                .willReturn(orders);

        // when
        List<Order> result = orderService.findAll();

        // then
        assertThat(result).hasSize(2);
    }

    private Menu createDisplayedMenu(UUID id, String name, BigDecimal price) {
        return createMenu(id, name, price, true);
    }

    private Menu createMenu(UUID id, String name, BigDecimal price, boolean displayed) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        return menu;
    }

    private OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    private OrderTable createOccupiedOrderTable(UUID id) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setOccupied(true);
        return orderTable;
    }

    private Order createOrder(UUID id, OrderType type) {
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        return order;
    }
}