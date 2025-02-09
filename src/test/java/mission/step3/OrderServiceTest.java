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

        @Nested
        @DisplayName("배달 주문")
        class DeliveryOrder {
            @Test
            @DisplayName("배달 주문을 생성한다")
            void createDeliveryOrder() {

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

                Order created = orderService.create(request);

                assertThat(created.getId()).isNotNull();
                assertThat(created.getType()).isEqualTo(OrderType.DELIVERY);
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(created.getOrderDateTime()).isNotNull();
                assertThat(created.getDeliveryAddress()).isEqualTo("서울시 강남구");
                verify(orderRepository).save(any(Order.class));
            }

            @Test
            @DisplayName("배달 주문시 주소가 없으면 예외가 발생한다")
            void createDeliveryOrderWithoutAddress() {

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setDeliveryAddress(null);

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("배달 주문시 주소가 공백이면 예외가 발생한다")
            void createDeliveryOrderWithEmptyAddress() {

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setDeliveryAddress("");
                
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        @DisplayName("포장 주문")
        class TakeoutOrder {
            @Test
            @DisplayName("포장 주문을 생성한다")
            void createTakeoutOrder() {

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

                Order created = orderService.create(request);

                assertThat(created.getType()).isEqualTo(OrderType.TAKEOUT);
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(created.getOrderLineItems()).hasSize(1);
                assertThat(created.getOrderLineItems().getFirst().getQuantity()).isEqualTo(1);
                verify(orderRepository).save(any(Order.class));
            }
        }

        @Nested
        @DisplayName("매장 주문")
        class EatInOrder {
            @Test
            @DisplayName("매장 식사 주문을 생성한다")
            void createEatInOrder() {

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

                Order created = orderService.create(request);

                assertThat(created.getType()).isEqualTo(OrderType.EAT_IN);
                assertThat(created.getOrderTable()).isEqualTo(orderTable);
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(created.getOrderLineItems()).hasSize(1);
                verify(orderRepository).save(any(Order.class));
            }

            @Test
            @DisplayName("매장 식사 주문시 테이블이 존재하지 않으면 예외가 발생한다")
            void createEatInOrderWithNonExistentTable() {

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
                given(orderTableRepository.findById(tableId))
                        .willReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("매장 식사 주문시 테이블이 사용 중이 아니면 예외가 발생한다")
            void createEatInOrderWithNonOccupiedTable() {

                UUID menuId = UUID.randomUUID();
                UUID tableId = UUID.randomUUID();

                Menu menu = createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000));
                OrderTable orderTable = new OrderTable();
                orderTable.setId(tableId);
                orderTable.setOccupied(false);
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

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("공통 검증")
        class CommonValidation {
            @Test
            @DisplayName("주문 타입이 없으면 예외가 발생한다")
            void createOrderWithoutType() {

                Order request = new Order();
                request.setType(null);
 
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문 항목이 없으면 예외가 발생한다")
            void createOrderWithoutLineItems() {

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderLineItems(List.of());
 
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문할 메뉴가 존재하지 않으면 예외가 발생한다")
            void createOrderWithNonExistentMenu() {

                OrderLineItem orderLineItem = createOrderLineItem(UUID.randomUUID(), 1, BigDecimal.valueOf(4000));

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderLineItems(List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(any()))
                        .willReturn(List.of());

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문할 메뉴가 판매 중이 아니면 예외가 발생한다")
            void createOrderWithNonDisplayedMenu() {

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
 
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("주문 항목의 가격이 메뉴 가격과 다르면 예외가 발생한다")
            void createOrderWithDifferentPrice() {

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

                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("주문 수량이 0 미만이면 예외가 발생한다")
            void createOrderWithInvalidQuantity() {

                UUID menuId = UUID.randomUUID();
                OrderLineItem orderLineItem = createOrderLineItem(menuId, -1, BigDecimal.valueOf(4000));

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderLineItems(List.of(orderLineItem));

                given(menuRepository.findAllByIdIn(List.of(menuId)))
                        .willReturn(List.of(createDisplayedMenu(menuId, "아메리카노", BigDecimal.valueOf(4000))));
 
                assertThatThrownBy(() -> orderService.create(request))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
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

    @Nested
    @DisplayName("주문 상태 변경")
    class ChangeOrderStatus {

        @Nested
        @DisplayName("주문 수락")
        class AcceptOrder {
            @Test
            @DisplayName("주문을 수락한다")
            void acceptOrder() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setStatus(OrderStatus.WAITING);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));

                Order accepted = orderService.accept(orderId);

                assertThat(accepted.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }

            @Test
            @DisplayName("존재하지 않는 주문을 수락하면 예외가 발생한다")
            void acceptNonExistentOrder() {

                UUID orderId = UUID.randomUUID();
                given(orderRepository.findById(orderId))
                        .willReturn(Optional.empty());

                assertThatThrownBy(() -> orderService.accept(orderId))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("대기 중이 아닌 주문을 수락하면 예외가 발생한다")
            void acceptNonWaitingOrder() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setStatus(OrderStatus.ACCEPTED);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));
 
                assertThatThrownBy(() -> orderService.accept(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("배달 주문을 수락하면 배달 기사를 요청한다")
            void acceptDeliveryOrder() {

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

                orderService.accept(orderId);

                verify(kitchenridersClient).requestDelivery(
                        orderId,
                        BigDecimal.valueOf(8000),
                        "서울시 강남구"
                );
            }
        }

        @Nested
        @DisplayName("서빙 상태 변경")
        class ServeOrder {
            @Test
            @DisplayName("주문을 서빙한다")
            void serveOrder() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setStatus(OrderStatus.ACCEPTED);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));

                Order served = orderService.serve(orderId);

                assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
            }

            @Test
            @DisplayName("수락되지 않은 주문을 서빙하면 예외가 발생한다")
            void serveNonAcceptedOrder() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setStatus(OrderStatus.WAITING);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));
 
                assertThatThrownBy(() -> orderService.serve(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        @DisplayName("배달 상태 변경")
        class DeliveryStatus {
            @Test
            @DisplayName("배달을 시작한다")
            void startDelivery() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setType(OrderType.DELIVERY);
                order.setStatus(OrderStatus.SERVED);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));

                Order delivering = orderService.startDelivery(orderId);

                assertThat(delivering.getStatus()).isEqualTo(OrderStatus.DELIVERING);
            }

            @Test
            @DisplayName("배달을 완료한다")
            void completeDelivery() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setStatus(OrderStatus.DELIVERING);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));

                Order delivered = orderService.completeDelivery(orderId);

                assertThat(delivered.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            }
        }

        @Nested
        @DisplayName("주문 완료")
        class CompleteOrder {
            @Test
            @DisplayName("배달 주문을 완료한다")
            void completeDeliveryOrder() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setType(OrderType.DELIVERY);
                order.setStatus(OrderStatus.DELIVERED);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));

                Order completed = orderService.complete(orderId);

                assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @Test
            @DisplayName("매장/포장 주문을 완료한다")
            void completeNonDeliveryOrder() {

                UUID orderId = UUID.randomUUID();
                Order order = new Order();
                order.setId(orderId);
                order.setType(OrderType.TAKEOUT);
                order.setStatus(OrderStatus.SERVED);

                given(orderRepository.findById(orderId))
                        .willReturn(Optional.of(order));

                Order completed = orderService.complete(orderId);

                assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @Test
            @DisplayName("매장 주문 완료시 해당 테이블의 모든 주문이 완료되면 테이블을 비운다")
            void completeOrderAndClearTable() {

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

                orderService.complete(orderId);

                assertThat(orderTable.getNumberOfGuests()).isZero();
                assertThat(orderTable.isOccupied()).isFalse();
            }
        }
    }

    @Test
    @DisplayName("전체 주문을 조회한다")
    void findAll() {

        List<Order> orders = List.of(
                createOrder(UUID.randomUUID(), OrderType.DELIVERY),
                createOrder(UUID.randomUUID(), OrderType.TAKEOUT)
        );
        given(orderRepository.findAll())
                .willReturn(orders);

        List<Order> result = orderService.findAll();

        assertThat(result).hasSize(2);
    }

    private Order createOrder(UUID id, OrderType type) {
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        return order;
    }
}
