package kitchenpos.application;

import kitchenpos.application.fake.FakeMenuRepository;
import kitchenpos.application.fake.FakeOrderRepository;
import kitchenpos.application.fake.FakeOrderTableRepository;
import kitchenpos.application.support.TestFixture;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setup() {
        orderRepository = new FakeOrderRepository();
        menuRepository = new FakeMenuRepository();
        orderTableRepository = new FakeOrderTableRepository();
        kitchenridersClient = new KitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }


    @DisplayName("주문을 생성할 때 주문 타입이 Null이라면 IllegalArgumentException을 발생시킨다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void create_order_with_null_and_empty_order_type(final OrderType orderType) {
        final Order order = TestFixture.createOrderWithOrderType(orderType);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("주문을 생성 할 때 주문할 메뉴의 항목은 Null이거나 비어있다면 IllegalArgumentException을 발생시킨다")
    @ParameterizedTest
    @NullAndEmptySource
    void create_order_with_null_and_empty_menu(final List<OrderLineItem> orderLineItems) {
        Order order = TestFixture.createOrderWithOrderLineItems(orderLineItems);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("비공개 상태의 메뉴를 주문한다면 IllegalStateException를 발생시킨다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void create_order_with_none_display_menu(final OrderType orderType) {
        final Order order = TestFixture.createOrderWithOrderType(orderType);

        Menu menu = TestFixture.createMenuWithDisplayed(false);
        menuRepository.save(menu);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));
    }

    @DisplayName("메뉴의 가격과 주문을 통해 전달된 주문 가격이 다르면 IllegalArgumentException를 발생시킨다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void create_order_with_not_match_total_price(final OrderType orderType) {
        final Order order = TestFixture.createOrderWithOrderType(orderType);

        Menu menu = TestFixture.createMenuWithPrice(5000L);
        menuRepository.save(menu);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
    }


    @DisplayName("주문을 수락할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void accept(final OrderType orderType) {
        final Order order = TestFixture.createOrderWithOrderType(orderType);
        orderRepository.save(order);

        final Order result = orderService.accept(order.getId());
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }


    @DisplayName("주문을 수락할때 WAITING이 아니라면 IllegalStateException을 발생시킨다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "COMPLETED", "DELIVERED", "DELIVERING", "SERVED"})
    void accept_by_not_watting_status(final OrderStatus orderStatus) {
        Order order = TestFixture.createOrderWithTypeAndStatus(OrderType.DELIVERY, orderStatus);
        orderRepository.save(order);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(order.getId()));
    }

    @DisplayName("메뉴를 제공할 수 있다")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"DELIVERY", "TAKEOUT", "EAT_IN"})
    void serve(final OrderType orderType) {
        Order order = TestFixture.createOrderWithTypeAndStatus(orderType,OrderStatus.ACCEPTED);
        orderRepository.save(order);

        final Order result = orderService.serve(order.getId());
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문을 제공할때 ACCEPTED의 상태가 아니라면 IllegalStateException를 발생시킨다")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "COMPLETED", "DELIVERED", "DELIVERING", "SERVED"})
    void serve_with_not_accept_status(final OrderStatus orderStatus) {
        Order order = TestFixture.createOrderWithTypeAndStatus(OrderType.DELIVERY, orderStatus);
        orderRepository.save(order);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(order.getId()));
    }

    @Nested
    @DisplayName("배달 주문")
    class DeliveryOrderTest {
        private final OrderType orderType = OrderType.DELIVERY;

        @DisplayName("배달 주소가 Null이거나 비어있다면 IllegalArgumentException를 발생시킨다")
        @ParameterizedTest
        @NullAndEmptySource
        void create_delivery_order_without_address(final String address) {
            Order order = TestFixture.createOrderWithAddress(address);

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(order));
        }


        @DisplayName("주문 배송 완료 처리를 할 수 있다")
        @Test
        void complete_delivery() {
            Order order = TestFixture.createOrderWithTypeAndStatus(orderType, OrderStatus.DELIVERING);
            orderRepository.save(order);

            final Order result = orderService.completeDelivery(order.getId());
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }


        @DisplayName("배달 중이 아닌 상태에서 완료처리를 하면 IllegalStateException를 발생시킨다")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "COMPLETED", "DELIVERED", "SERVED"})
        void complete_delivery_with_not_deliverd_status(final OrderStatus orderStatus) {
            Order order = TestFixture.createOrderWithTypeAndStatus(orderType, orderStatus);
            orderRepository.save(order);

            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.completeDelivery(order.getId()));
        }


        @DisplayName("주문 수량이 음수라면 IllegalArgumentException를 발생시킨다")
        @Test
        void create_order_lower_than_zero_quantity() {
            List<OrderLineItem> orderLineItems = TestFixture.createGeneralOrderLineItemsWithQuantity(-1);
            Order order = TestFixture.createOrderWithOrderLineItems(orderLineItems);

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(order));
        }


        @DisplayName("메뉴 제공 상태일 때 배달이 가능하다")
        @Test
        void start_delivery() {
            Order order = TestFixture.createOrderWithTypeAndStatus(orderType, OrderStatus.SERVED);
            orderRepository.save(order);

            final Order result = orderService.startDelivery(order.getId());
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }


        @DisplayName("배달 상태일 때 배달 완료 처리가 가능하다")
        @Test
        void complete_delivering() {
            Order order = TestFixture.createOrderWithTypeAndStatus(orderType, OrderStatus.DELIVERING);
            orderRepository.save(order);

            final Order result = orderService.completeDelivery(order.getId());
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }
    }

    @Nested
    @DisplayName("포장 주문")
    class TakeoutOrderTest {

        private final OrderType orderType = OrderType.TAKEOUT;

        @DisplayName("주문 수량이 음수라면 IllegalArgumentException를 발생시킨다")
        @Test
        void create_order_lower_than_zero_quantity() {
            List<OrderLineItem> orderLineItems = TestFixture.createGeneralOrderLineItemsWithQuantity(-1);
            Order order = TestFixture.createOrderWithTypeAndOrderLineItems(orderType, orderLineItems);

            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(order));
        }

        @DisplayName("제공 상태일때 완료 처리가 가능하다")
        @Test
        void complete_eat_in_order() {
            Order order = TestFixture.createOrderWithTypeAndStatus(orderType, OrderStatus.SERVED);
            orderRepository.save(order);

            final Order result = orderService.complete(order.getId());
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("매장 주문")
    class EatInOrderTest {

        private final OrderType orderType = OrderType.EAT_IN;

        @DisplayName("주문 테이블이 존재하지 않으면 NoSuchElementException를 발생시킨다")
        @Test
        void create_eat_in_order_with_no_such_order_table() {
            final Order order = TestFixture.createOrderWithOrderType(orderType);
            menuRepository.save(TestFixture.createGeneralMenu());

            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.create(order));
        }


        @DisplayName("주문 테이블이 비어있지 않다면 IllegalStateException를 발생시킨다")
        @Test
        void create_eat_in_order_with_empty_order_table() {
            menuRepository.save(TestFixture.createGeneralMenu());

            final Order order = TestFixture.createOrderWithOrderType(orderType);

            OrderTable orderTable = TestFixture.createOrderTableWithOccupied(false);
            orderTableRepository.save(orderTable);
            
            assertThatExceptionOfType(IllegalStateException.class)
                    .isThrownBy(() -> orderService.create(order));
        }

        @DisplayName("매장 주문이거나, 포장 주문일 경우 상품 제공 상태일때 완료 처리가 가능하다")
        @Test
        void complete_take_out_order() {
            Order order = TestFixture.createOrderWithTypeAndStatus(orderType, OrderStatus.SERVED);
            orderRepository.save(order);

            final Order result = orderService.complete(order.getId());
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(result.getOrderTable().isOccupied()).isFalse();
            assertThat(result.getOrderTable().getNumberOfGuests()).isZero();
        }
    }

}
