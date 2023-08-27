package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuFixture.createMenuWithDisplayed;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderTableFixture.createOrderTableWithOccupied;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Nested
    @DisplayName("주문을 등록할 수 있다.")
    class create {

        @Test
        @DisplayName("주문타입이 null 일 경우 예외가 발생한다.")
        void create_1() {
            // When
            Order order = createOrderWithType(null);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("주문내역은 존재하지 않으면 예외가 발생한다.")
        void create_2(List<OrderLineItem> orderLine) {
            // When
            Order order = createOrderWithOrderLineItems(orderLine);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("메뉴 개수와 주문 항목 개수가 일치하지 않을 경우 예외가 발생한다.")
        void create_3() {
            // When
            Menu menu = createMenu();
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            List<OrderLineItem> orderLineItems = List.of(
                    createOrderLineItem(1, BigDecimal.TEN),
                    createOrderLineItem(1, BigDecimal.TEN)
            );
            Order order = createOrderWithOrderLineItems(orderLineItems);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문타입이 매장 내 식사가 아닐경우 주문수량이 0이하이면 예외가 발생한다")
        void create_4() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            // When
            OrderLineItem orderLineItem = createOrderLineItem(-1, BigDecimal.ONE);
            Order order = createOrderWithOrderLineItems(List.of(orderLineItem));

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("등록된 메뉴가 아니라면 예외가 발생한다.")
        void create_5() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            // When
            when(menuRepository.findById(any())).thenReturn(Optional.empty());
            Order order = createOrder();

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("노출되지 않은 메뉴일 경우 예외가 발생한다.")
        void create_6() {
            // Given
            Order order = createOrder();

            Menu menu = createMenuWithDisplayed(false);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문목록의 가격과 메뉴가격은 일치하지 않으면 예외가 발생한다.")
        void create_7() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            // When
            OrderLineItem orderLineItem = createOrderLineItem(1, BigDecimal.TEN);
            Order order = createOrderWithOrderLineItems(List.of(orderLineItem));

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문타입이 배달일 경우 배달주소가 존재하지 않으면 예외가 발생한다.")
        void create_8() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            OrderLineItem orderLineItem = createOrderLineItem(1, BigDecimal.ONE);
            Order order = createOrderWithOrderLineItems(List.of(orderLineItem));

            // When
            order.setType(DELIVERY);
            order.setDeliveryAddress(null);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("주문타입이 매장 내 식사일 경우 주문 테이블을 사용할 수 없으면 예외가 발생한다.")
        void create_9() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            OrderLineItem orderLineItem = createOrderLineItem(1, BigDecimal.ONE);
            Order order = createOrderWithOrderLineItems(List.of(orderLineItem));

            // When
            OrderTable orderTable = createOrderTableWithOccupied(false);
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
            order.setType(EAT_IN);

            // Then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("등록")
        void create_10() {
            // Given
            Menu menu = createMenu();
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(any())).thenReturn(Optional.of(menu));

            OrderLineItem orderLineItem = createOrderLineItem(1, BigDecimal.ONE);
            Order order = createOrderWithOrderLineItems(List.of(orderLineItem));
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
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.accept(any()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문상태가 대기중이 아닐 경우 예외가 발생한다.")
        void accept_2() {
            // Given
            Order order = createOrderWithStatus(COMPLETED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // Then
            assertThatThrownBy(() -> orderService.accept(any()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문타입이 배달이라면 배달기사 서비스를 요청한다.")
        void accept_3() {
            // Given
            Order order = createOrderWithType(DELIVERY);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // When
            orderService.accept(any());

            // Then
            verify(kitchenridersClient).requestDelivery(any(), any(), any());
        }

        @Test
        @DisplayName("접수로 변경")
        void accept_4() {
            // Given
            Order order = createOrder();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // When
            Order result = orderService.accept(any());

            // Then
            assertThat(result.getStatus()).isEqualTo(ACCEPTED);
        }
    }

    @Nested
    @DisplayName("주문상태를 서빙으로 변경할 수 있다.")
    class serve {

        @Test
        @DisplayName("존재하지 않는 주문일 경우 예외가 발생한다.")
        void serve_1() {
            // Given
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.serve(any()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문상태가 접수가 아닐경우 예외가 발생한다.")
        void serve_2() {
            // Given
            Order order = createOrder();
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // Then
            assertThatThrownBy(() -> orderService.serve(any()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("서빙으로 변경")
        void serve_3() {
            // Given
            Order order = createOrderWithStatus(ACCEPTED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // When
            Order result = orderService.serve(any());

            // Then
            assertThat(result.getStatus()).isEqualTo(SERVED);
        }
    }

    @Nested
    @DisplayName("주문상태를 배달중 으로 변경할 수 있다.")
    class startDelivery {

        @Test
        @DisplayName("존재하는 주문이어야 한다.")
        void startDelivery_1() {
            // Given
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.startDelivery(any()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문타입이 배달이 아닐경우 예외가 발생한다.")
        void startDelivery_2() {
            // Given
            Order order = createOrderWithType(TAKEOUT);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // Then
            assertThatThrownBy(() -> orderService.startDelivery(any()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문상태가 서빙이 아닐경우 예외가 발생한다.")
        void startDelivery_3() {
            // Given
            Order order = createOrderWithStatus(COMPLETED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // Then
            assertThatThrownBy(() -> orderService.startDelivery(any()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달중으로 변경")
        void serve_3() {
            // Given
            Order order = createOrderWithTypeAndStatus(DELIVERY, SERVED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // When
            Order result = orderService.startDelivery(any());

            // Then
            assertThat(result.getStatus()).isEqualTo(DELIVERING);
        }
    }

    @Nested
    @DisplayName("주문상태를 배달완료로 변경할 수 있다.")
    class completeDelivery {

        @Test
        @DisplayName("존재하는 주문이어야 한다.")
        void completeDelivery_1() {
            // Given
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.completeDelivery(any()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문상태가 배달중이 아닐경우 예외가 발생한다.")
        void completeDelivery_2() {
            // Given
            Order order = createOrderWithStatus(COMPLETED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // Then
            assertThatThrownBy(() -> orderService.completeDelivery(any()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("배달완료로 변경")
        void completeDelivery_3() {
            // Given
            Order order = createOrderWithStatus(DELIVERING);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // When
            Order result = orderService.completeDelivery(any());

            // Then
            assertThat(result.getStatus()).isEqualTo(DELIVERED);
        }
    }

    @Nested
    @DisplayName("주문상태를 완료로 변경할 수 있다.")
    class complete {

        @Test
        @DisplayName("존재하는 주문이어야 한다.")
        void complete_1() {
            // Given
            when(orderRepository.findById(any())).thenReturn(Optional.empty());

            // Then
            assertThatThrownBy(() -> orderService.complete(any()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문타입이 배달일 경우 주문상태가 배달완료가 아니면 예외가 발생한다.")
        void complete_2() {
            // Given
            Order order = createOrderWithTypeAndStatus(DELIVERY, COMPLETED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // Then
            assertThatThrownBy(() -> orderService.complete(any()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Nested
        @DisplayName("주문타입이 포장 또는 매장 내 식사일 경우 주문상태가 서빙이 아니면 예외가 발생한다.")
        class complete_3 {

            @Test
            @DisplayName("주문타입이 포장일 경우")
            void complete_3_1() {
                // Given
                Order order = createOrderWithTypeAndStatus(TAKEOUT, COMPLETED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(order));

                // Then
                assertThatThrownBy(() -> orderService.complete(any()))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("주문타입이 매장 내 식사일 경우")
            void complete_3_2() {
                // Given
                Order order = createOrderWithTypeAndStatus(EAT_IN, COMPLETED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(order));

                // Then
                assertThatThrownBy(() -> orderService.complete(any()))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Test
        @DisplayName("완료로 변경")
        void complete_4() {
            // Given
            Order order = createOrderWithTypeAndStatus(DELIVERY, DELIVERED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // When
            Order result = orderService.complete(any());

            // Then
            assertThat(result.getStatus()).isEqualTo(COMPLETED);
        }

        @Test
        @DisplayName("매장 주문이 완료되고 다른 주문이 없으면 테이블을 초기화한다.")
        void complete_5() {
            // Given
            Order order = createOrderWithTypeAndStatus(EAT_IN, SERVED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            // When
            Order result = orderService.complete(any());

            // Then
            assertAll(
                    () -> assertThat(result.getStatus()).isEqualTo(COMPLETED),
                    () -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero(),
                    () -> assertThat(result.getOrderTable().isOccupied()).isFalse()
            );
        }
    }

    @Test
    @DisplayName("주문의 전체목록을 조회할 수 있다.")
    void findAll() {
        // Given
        List<Order> orders = List.of(createOrder(), createOrder());
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<Order> findAllOrders = orderService.findAll();

        // Then
        assertThat(findAllOrders).hasSize(orders.size());
    }

}