package kitchenpos.application;

import kitchenpos.application.fixture.MenuTestFixture;
import kitchenpos.application.fixture.OrderTableTestFixture;
import kitchenpos.application.fixture.OrderTestFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    OrderService orderService;
    MenuTestFixture menuTestFixture;
    OrderTestFixture orderTestFixture;
    OrderTableTestFixture orderTableTestFixture;
    @Mock
    OrderRepository orderRepository;
    @Mock
    MenuRepository menuRepository;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    KitchenridersClient kitchenridersClient;

    @BeforeEach
    void setup() {
        this.orderService = new OrderService(orderRepository, menuRepository,orderTableRepository, kitchenridersClient);
        this.menuTestFixture = new MenuTestFixture();
        this.orderTestFixture = new OrderTestFixture();
        this.orderTableTestFixture = new OrderTableTestFixture();
    }

    @Nested
    @DisplayName("주문 신규 시")
    class Order_create {

        @DisplayName("주문 유형이 입력되지 않으면 예외를 반환한다.")
        @Test
        void createOrderType() {
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 유형 필수");
        }


        @DisplayName("매장 식사가 아닐 때, 수량이 0 이상이 아니면 예외를 반환한다.")
        @Test
        void orderQuantity() {
            Menu menu = menuTestFixture.createMenu();
            OrderLineItem orderLineItem = orderTestFixture.createOrderLineItem(menu, -1);
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수량 오류");

        }

        @DisplayName("표시되지 않은 메뉴를 주문 시 예외를 반환한다.")
        @Test
        void displayMenu() {
            Menu menu = menuTestFixture.createMenu(false);
            OrderLineItem orderLineItem = orderTestFixture.createOrderLineItem(menu, 10);
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("표시 오류");
        }

        @DisplayName("주문 항목의 가격과 메뉴의 가격이 동일하지 않으면 예외를 반환한다.")
        @Test
        void menuPrice() {
            Menu menu = menuTestFixture.createMenu(BigDecimal.valueOf(100L));
            OrderLineItem orderLineItem = orderTestFixture.createOrderLineItem(menu, BigDecimal.valueOf(1000L), 10);
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("가격 오류");
        }

        @DisplayName("배달인 경우 배송지가 비어있으면 예외를 반환한다.")
        @Test
        void deliveryAddress() {
            Menu menu = menuTestFixture.createMenu(BigDecimal.valueOf(100L));
            OrderLineItem orderLineItem = orderTestFixture.createOrderLineItem(menu, BigDecimal.valueOf(100L), 10);
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY, List.of(orderLineItem));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("배송지 오류");

        }

        @DisplayName("매장 식사인 경우 주문 테이블이 비어있으면 예외를 반환한다.")
        @Test
        void orderTable() {
            Menu menu = menuTestFixture.createMenu(BigDecimal.valueOf(100L));
            OrderLineItem orderLineItem = orderTestFixture.createOrderLineItem(menu, BigDecimal.valueOf(100L), 10);
            OrderTable orderTable = orderTableTestFixture.createOrderTable(false, 1);
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN, List.of(orderLineItem), orderTable);


            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("테이블 오류");
        }
    }


    @DisplayName("주문 승인 시 주문 상태가 대기가 아니면 예외를 반환한다.")
    @Test
    void acceptOrderStatus() {
        Order order = orderTestFixture.createOrder(OrderStatus.ACCEPTED,OrderType.DELIVERY);
        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문승인상태오류");

    }


    @DisplayName("주문 제공시 주문상태가 수락이 아니면 예외를 반환한다.")
    @Test
    void serveStatus() {
        Order order = orderTestFixture.createOrder(OrderStatus.SERVED,OrderType.DELIVERY);
        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.serve(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문제공상태오류");


    }

    @Nested
    @DisplayName("배달 시작 시")
    class Delivery_start {

        @DisplayName("주문 유형이 배달이 아니면 예외를 반환한다.")
        @Test
        void deliveryType() {
            Order order = orderTestFixture.createOrder(OrderStatus.SERVED, OrderType.EAT_IN);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("배달 주문 유형 오류");

        }

        @DisplayName("주문 상태가 제공됨이 아니면 예외를 반환한다.")
        @Test
        void deliveryStatus() {
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.startDelivery(order.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("배달 주문 상태 오류");

        }
    }

    @DisplayName("배달중 주문 상태가 배달중이 아니면 예외를 반환한다.")
    @Test
    void completeDeliveryStatus() {
        Order order = orderTestFixture.createOrder(OrderStatus.SERVED,OrderType.EAT_IN);
        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.completeDelivery(order.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("배달중 주문 상태 오류");


    }
    @Nested
    @DisplayName("주문 완료 시")
    class Order_complete {

        @DisplayName("배달 완료후 주문 유형이 배달이고, 상태가 배달완료가 아니면 예외를 반환한다.")
        @Test
        void completeOrderStatus() {
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.DELIVERY);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("배달 완료 상태 오류");

        }

        @DisplayName("포장이거나, 매장 식사는 상태가 제공됨이 아니면 예외를 반환한다.")
        @Test
        void completeOrderEatIn() {
            Order order = orderTestFixture.createOrder(OrderStatus.WAITING, OrderType.EAT_IN);
            given(orderRepository.findById(any())).willReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.complete(order.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("제공 완료 상태 오류");
        }
    }

    @DisplayName("매장 식사 시 완료 처리 되지 않은 주문이 없는 경우 손님수를 0으로 설정하고, 테이블 상태를 비어있음으로 변경한다.")
    @Test
    void tableClean() {
        OrderTable orderTable = orderTableTestFixture.createOrderTable(true,3);
        Order order = orderTestFixture.createOrder(OrderStatus.SERVED,OrderType.EAT_IN,orderTable);

        given(orderRepository.findById(any())).willReturn(Optional.of(order));
        given(orderRepository.existsByOrderTableAndStatusNot(any(),any())).willReturn(false);

        Order orderResult = orderService.complete(order.getId());
        assertThat(orderResult.getOrderTable().getNumberOfGuests()).isEqualTo(0);
        assertThat(orderResult.getOrderTable().isOccupied()).isFalse();

    }


}
