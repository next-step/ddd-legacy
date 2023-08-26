package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order")
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    private static final UUID ORDER_ID = UUID.randomUUID();
    private static final UUID ORDER_TABLE_ID = UUID.randomUUID();
    private static final OrderType ORDER_TYPE = OrderType.DELIVERY;
    private static final OrderStatus ORDER_STATUS = OrderStatus.WAITING;
    private static final LocalDateTime ORDER_DATE_TIME = LocalDateTime.MIN;
    private static final String DELIVERY_ADDRESS = "address";
    private Order order;
    private OrderLineItem orderLineItem;
    private OrderTable orderTable;
    private Menu menu;

    @BeforeEach
    void setUp() {
        menu = new Menu();
        menu.setPrice(BigDecimal.ONE);

        orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);

        orderTable = new OrderTable();

        order = new Order();
        order.setId(ORDER_TABLE_ID);
        order.setType(ORDER_TYPE);
        order.setStatus(ORDER_STATUS);
        order.setOrderDateTime(ORDER_DATE_TIME);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setDeliveryAddress(DELIVERY_ADDRESS);
        order.setOrderTable(orderTable);
        order.setOrderTableId(ORDER_TABLE_ID);
    }

    @Test
    @DisplayName("주문은 식별키, 주문타입, 주문상태, 주문시간, 주문내역, 배달주소, 주문테이블을 가진다.")
    void order() {
        assertAll(
                () -> assertThat(order.getId()).isEqualTo(ORDER_TABLE_ID),
                () -> assertThat(order.getType()).isEqualTo(ORDER_TYPE),
                () -> assertThat(order.getStatus()).isEqualTo(ORDER_STATUS),
                () -> assertThat(order.getOrderDateTime()).isEqualTo(ORDER_DATE_TIME),
                () -> assertThat(order.getOrderLineItems()).isEqualTo(List.of(orderLineItem)),
                () -> assertThat(order.getDeliveryAddress()).isEqualTo(DELIVERY_ADDRESS),
                () -> assertThat(order.getOrderTable()).isEqualTo(orderTable),
                () -> assertThat(order.getOrderTableId()).isEqualTo(ORDER_TABLE_ID)
        );
    }

    @Nested
    @DisplayName("주문을 등록할 수 있다.")
    class create {

        @Test
        @DisplayName("주문타입이 null 일 경우 예외가 발생한다.")
        void create_1() {
            // When
            order.setType(null);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("주문내역은 존재하지 않으면 예외가 발생한다.")
        void create_2(List<OrderLineItem> orderLine) {
            // When
            order.setOrderLineItems(orderLine);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 개수와 주문 항목 개수가 일치하지 않을 경우 예외가 발생한다.")
        void create_3() {
            // When
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            order.setOrderLineItems(List.of(orderLineItem, orderLineItem));

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문타입이 매장 내 식사가 아닐경우 주문수량이 0이하이면 예외가 발생한다")
        void create_4() {
            // When
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            order.setType(OrderType.TAKEOUT);
            orderLineItem.setQuantity(-1);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("등록된 메뉴가 아니라면 예외가 발생한다.")
        void create_5() {
            // Given
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            // When
            when(menuRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("노출되지 않은 메뉴일 경우 예외가 발생한다.")
        void create_6() {
            // Given
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // When
            menu.setDisplayed(false);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문목록의 가격과 메뉴가격은 일치하지 않으면 예외가 발생한다.")
        void create_7() {
            // Given
            menu.setDisplayed(true);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // When
            orderLineItem.setPrice(BigDecimal.TEN);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문타입이 배달일 경우 배달주소가 존재하지 않으면 예외가 발생한다.")
        void create_8() {
            // Given
            menu.setDisplayed(true);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
            orderLineItem.setPrice(BigDecimal.ONE);

            // When
            order.setType(OrderType.DELIVERY);
            order.setDeliveryAddress(null);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문타입이 매장 내 식사일 경우 주문 테이블을 사용할 수 없으면 예외가 발생한다.")
        void create_9() {
            // Given
            menu.setDisplayed(true);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
            orderLineItem.setPrice(BigDecimal.ONE);

            // When
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            order.setType(OrderType.EAT_IN);
            orderTable.setOccupied(false);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }
        @Test
        @DisplayName("등록")
        void create_10() {
            // Given
            menu.setDisplayed(true);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
            orderLineItem.setPrice(BigDecimal.ONE);
            when(orderRepository.save(any())).thenReturn(order);

            // When
            Order result = orderService.create(order);

            // Then
            assertThat(result).isEqualTo(order);
        }
    }

    @Nested
    @DisplayName("주문상태를 접수로 변경할 수 있다.")
    class accept {

        @Test
        @DisplayName("존재하지 않는 주문일 경우 예외가 발생한다.")
        void accept_1() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.accept(ORDER_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문상태가 대기중이 아닐 경우 예외가 발생한다.")
        void accept_2() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // When
            order.setStatus(OrderStatus.COMPLETED);

            // Then
            assertThatThrownBy(() -> orderService.accept(ORDER_ID))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문타입이 배달이라면 배달기사 서비스를 요청한다.")
        void accept_3() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            order.setType(OrderType.DELIVERY);

            // When
            orderService.accept(ORDER_ID);

            // Then
            verify(kitchenridersClient).requestDelivery(eq(ORDER_ID), any(), any());
        }

        @Test
        @DisplayName("접수로 변경")
        void accept_4() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // When
            Order result = orderService.accept(ORDER_ID);

            // Then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }

    @Nested
    @DisplayName("주문상태를 서빙으로 변경할 수 있다.")
    class serve {

        @Test
        @DisplayName("존재하지 않는 주문일 경우 예외가 발생한다.")
        void serve_1() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.serve(ORDER_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문상태가 접수가 아닐경우 예외가 발생한다.")
        void serve_2() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // Then
            assertThatThrownBy(() -> orderService.serve(ORDER_ID))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("서빙으로 변경")
        void serve_3() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            order.setStatus(OrderStatus.ACCEPTED);

            // When
            Order result = orderService.serve(ORDER_ID);

            // Then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @Nested
    @DisplayName("주문상태를 배달중 으로 변경할 수 있다.")
    class startDelivery {

        @Test
        @DisplayName("존재하는 주문이어야 한다.")
        void startDelivery_1() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.startDelivery(ORDER_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문타입이 배달이 아닐경우 예외가 발생한다.")
        void startDelivery_2() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // When
            order.setType(OrderType.TAKEOUT);

            // Then
            assertThatThrownBy(() -> orderService.startDelivery(ORDER_ID))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문상태가 서빙이 아닐경우 예외가 발생한다.")
        void startDelivery_3() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // When
            order.setStatus(OrderStatus.COMPLETED);

            // Then
            assertThatThrownBy(() -> orderService.startDelivery(ORDER_ID))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달중으로 변경")
        void serve_3() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.SERVED);

            // When
            Order result = orderService.startDelivery(ORDER_ID);

            // Then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }
    }

    @Nested
    @DisplayName("주문상태를 배달완료로 변경할 수 있다.")
    class completeDelivery {

        @Test
        @DisplayName("존재하는 주문이어야 한다.")
        void completeDelivery_1() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.completeDelivery(ORDER_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문상태가 배달중이 아닐경우 예외가 발생한다.")
        void completeDelivery_2() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // When
            order.setStatus(OrderStatus.COMPLETED);

            // Then
            assertThatThrownBy(() -> orderService.completeDelivery(ORDER_ID))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달완료로 변경")
        void completeDelivery_3() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            order.setStatus(OrderStatus.DELIVERING);

            // When
            Order result = orderService.completeDelivery(ORDER_ID);

            // Then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Nested
    @DisplayName("주문상태를 완료로 변경할 수 있다.")
    class complete {

        @Test
        @DisplayName("존재하는 주문이어야 한다.")
        void complete_1() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.complete(ORDER_ID))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문타입이 배달일 경우 주문상태가 배달완료가 아니면 예외가 발생한다.")
        void complete_2() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

            // When
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.COMPLETED);

            // Then
            assertThatThrownBy(() -> orderService.complete(ORDER_ID))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Nested
        @DisplayName("주문타입이 포장 또는 매장 내 식사일 경우 주문상태가 서빙이 아니면 예외가 발생한다.")
        class complete_3 {

            @Test
            @DisplayName("주문타입이 포장일 경우")
            void complete_3_1() {
                // Given
                when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

                // When
                order.setType(OrderType.TAKEOUT);
                order.setStatus(OrderStatus.COMPLETED);

                // Then
                assertThatThrownBy(() -> orderService.complete(ORDER_ID))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("주문타입이 매장 내 식사일 경우")
            void complete_3_2() {
                // Given
                when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

                // When
                order.setType(OrderType.EAT_IN);
                order.setStatus(OrderStatus.COMPLETED);

                // Then
                assertThatThrownBy(() -> orderService.complete(ORDER_ID))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Test
        @DisplayName("완료로 변경")
        void complete_4() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERED);

            // When
            Order result = orderService.complete(ORDER_ID);

            // Then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("매장 주문이 완료되고 다른 주문이 없으면 테이블을 초기화한다.")
        void complete_5() {
            // Given
            when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
            order.setType(OrderType.EAT_IN);
            order.setStatus(OrderStatus.SERVED);

            // When
            Order result = orderService.complete(ORDER_ID);

            // Then
            assertAll(
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(result.getOrderTable().getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(result.getOrderTable().isOccupied()).isFalse()
            );
        }
    }

    @Test
    @DisplayName("주문의 전체목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<Order> orders = List.of(order, order);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<Order> findAllOrders = orderService.findAll();

        // Then
        assertThat(findAllOrders.size()).isEqualTo(2);
    }

}