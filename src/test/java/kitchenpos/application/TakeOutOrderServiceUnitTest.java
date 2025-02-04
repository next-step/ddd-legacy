package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.TAKEOUT;
import static kitchenpos.fixture.MenuFixture.createMenuId;
import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.menuProduct;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@DisplayName("포장 주문 서비스 통합 테스트")
class TakeOutOrderServiceUnitTest {

    private OrderRepository orderRepository = mock(OrderRepository.class);
    private MenuRepository menuRepository = mock(MenuRepository.class);
    private OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private KitchenridersClient kitchenridersClient = mock(KitchenridersClient.class);
    private OrderService orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        menuRepository = mock(MenuRepository.class);
        orderTableRepository = mock(OrderTableRepository.class);
        kitchenridersClient = mock(KitchenridersClient.class);
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("포장 주문이 생성되지 않았다면")
    @Nested
    class OrderIsNotCreated {

        private Menu menu;
        private OrderLineItem orderLineItem;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(null, menu);
        }

        @DisplayName("대기 상태의 포장 주문을 생성할 수 있습니다.")
        @Test
        void createTakeoutOrder() {
            final Order takeOutOrder = takeoutOrder(null, null, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderRepository.save(any(Order.class))).then(returnsFirstArg());

            final Order actual = orderService.create(takeOutOrder);
            assertAll(
                    () -> assertThat(actual.getId()).isNotNull(),
                    () -> assertThat(actual.getOrderDateTime()).isBeforeOrEqualTo(LocalDateTime.now()),
                    () -> assertThat(actual.getDeliveryAddress()).isNull(),
                    () -> assertThat(actual.getStatus()).isEqualTo(WAITING),
                    () -> assertThat(actual.getType()).isEqualTo(TAKEOUT),
                    () -> assertThat(actual.getOrderTable()).isNull()
            );
        }

        @DisplayName("주문 형식이 없으면 예외가 발생합니다")
        @Test
        void createOrderWithoutType() {
            assertThatThrownBy(() -> orderService.create(
                    order(
                            null,
                            null,
                            null,
                            WAITING,
                            null,
                            null,
                            List.of(orderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목이 없거나 비어있으면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 항목: {0}")
        @NullAndEmptySource
        void createOrderWithoutOrderLineItems(final List<OrderLineItem> orderLineItems) {
            assertThatThrownBy(() -> orderService.create(
                    takeoutOrder(
                            null,
                            null,
                            WAITING,
                            orderLineItems
                    )
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 개수와 주문 항목의 개수가 다르면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuCount() {
            final Menu otherMenu = menu(menuGroup(), List.of(menuProduct(product())));
            final OrderLineItem otherOrderLineItem = orderLineItem(null, otherMenu);
            final Order takeoutOrder = takeoutOrder(
                    null,
                    null,
                    WAITING,
                    List.of(orderLineItem, otherOrderLineItem)
            );

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(
                    takeoutOrder
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목 중 하나라도 수량이 0보다 작으면 예외가 발생합니다")
        @ParameterizedTest(name = "수량 : {0}")
        @ValueSource(longs = {-1L, -10L, -100L})
        void createOrderWithNegativeQuantity(final long negativeQuantity) {
            final OrderLineItem negativeOrderLineItem = orderLineItem(null, menu, negativeQuantity);
            final Order takeoutOrder = takeoutOrder(null, null, WAITING, List.of(negativeOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(takeoutOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목의 메뉴가 존재하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithoutMenus() {
            final Order takeoutOrder = takeoutOrder(null, null, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of());

            assertThatThrownBy(() -> orderService.create(takeoutOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목의 메뉴의 가격이 일치하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuPrice() {
            final BigDecimal differentPrice = BigDecimal.ONE.add(menu.getPrice());
            final Menu differentPriceMenu = menu(menu.getId(), menu.getName(), differentPrice, menu.getMenuGroup(), menu.getMenuProducts(), menu.isDisplayed());
            final Order takeoutOrder = takeoutOrder(null, null, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(differentPriceMenu));
            when(menuRepository.findById(differentPriceMenu.getId())).thenReturn(Optional.ofNullable(differentPriceMenu));

            assertThatThrownBy(() -> orderService.create(takeoutOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 미노출 상태이면 예외가 발생합니다")
        @Test
        void createOrderWithNonDisplayedMenu() {
            final Menu nonDisplayedMenu = menu(menu.getId(), menu.getName(), menu.getPrice(), menu.getMenuGroup(), menu.getMenuProducts(), false);
            final Order takeoutOrder = takeoutOrder(null, null, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(nonDisplayedMenu));
            when(menuRepository.findById(nonDisplayedMenu.getId())).thenReturn(Optional.ofNullable(nonDisplayedMenu));

            assertThatThrownBy(() -> orderService.create(takeoutOrder))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("포장 주문이 대기 상태라면")
    @Nested
    class OrderStatusIsWaiting {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order takeOutOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.takeOutOrder = takeoutOrder(createOrderId(), createOrderDateTime(), WAITING, List.of(orderLineItem));
        }

        @DisplayName("포장 주문을 수락할 수 있습니다.")
        @Test
        void acceptTakeOutOrder() {
            when(orderRepository.findById(takeOutOrder.getId())).thenReturn(Optional.ofNullable(takeOutOrder));
            doNothing().when(kitchenridersClient).requestDelivery(any(), any(), any());

            final Order actual = orderService.accept(takeOutOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(takeOutOrder.getId()),
                    () -> assertThat(actual.getType()).isEqualTo(takeOutOrder.getType()),
                    () -> assertThat(actual.getStatus()).isEqualTo(ACCEPTED)
            );
        }

        @DisplayName("주문이 존재하지 않으면 예외가 발생합니다")
        @Test
        void acceptNonExistentOrder() {
            when(orderRepository.findById(takeOutOrder.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.accept(takeOutOrder.getId())).isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("대기 상태가 아닌 주문을 수락하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "COMPLETED"})
        void acceptNonWaitingOrder(final OrderStatus orderStatus) {
            final Order nonWaitingOrder = takeoutOrder(
                    takeOutOrder.getId(), takeOutOrder.getOrderDateTime(), orderStatus, takeOutOrder.getOrderLineItems()
            );
            when(orderRepository.findById(nonWaitingOrder.getId())).thenReturn(Optional.ofNullable(nonWaitingOrder));

            assertThatThrownBy(() -> orderService.accept(nonWaitingOrder.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("포장 주문아 수락 상태라면")
    @Nested
    class OrderStatusIsAccepted {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order takeOutOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.takeOutOrder = takeoutOrder(createOrderId(), createOrderDateTime(), ACCEPTED, List.of(orderLineItem));
        }

        @DisplayName("포장 주문을 서빙할 수 있습니다.")
        @Test
        void serveTakeOutOrder() {
            when(orderRepository.findById(takeOutOrder.getId())).thenReturn(Optional.ofNullable(takeOutOrder));

            final Order actual = orderService.serve(takeOutOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(takeOutOrder.getId()),
                    () -> assertThat(actual.getType()).isEqualTo(takeOutOrder.getType()),
                    () -> assertThat(actual.getStatus()).isEqualTo(SERVED)
            );
        }

        @DisplayName("주문이 존재하지 않으면 예외가 발생합니다")
        @Test
        void serveNonExistentOrder() {
            when(orderRepository.findById(takeOutOrder.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.serve(takeOutOrder.getId())).isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("수락 상태가 아닌 주문을 서빙하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "COMPLETED"})
        void serveNonAcceptedOrder(final OrderStatus orderStatus) {
            final Order nonAcceptedOrder = takeoutOrder(
                    takeOutOrder.getId(), takeOutOrder.getOrderDateTime(), orderStatus, takeOutOrder.getOrderLineItems()
            );
            when(orderRepository.findById(nonAcceptedOrder.getId())).thenReturn(Optional.ofNullable(nonAcceptedOrder));

            assertThatThrownBy(() -> orderService.serve(nonAcceptedOrder.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("포장 주문이 서빙된 상태라면")
    @Nested
    class OrderStatusIsServed {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order takeOutOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.takeOutOrder = takeoutOrder(createOrderId(), createOrderDateTime(), SERVED, List.of(orderLineItem));
        }

        @DisplayName("포장 주문을 완료할 수 있습니다.")
        @Test
        void completeDeliveryOrder() {
            when(orderRepository.findById(takeOutOrder.getId())).thenReturn(Optional.ofNullable(takeOutOrder));

            final Order actual = orderService.complete(takeOutOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(takeOutOrder.getId()),
                    () -> assertThat(actual.getType()).isEqualTo(takeOutOrder.getType()),
                    () -> assertThat(actual.getStatus()).isEqualTo(COMPLETED)
            );
        }

        @DisplayName("서빙 상태가 아닌 주문을 완료하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "COMPLETED"})
        void completeNonServedOrder(final OrderStatus orderStatus) {
            final Order nonServedOrder = takeoutOrder(
                    takeOutOrder.getId(), takeOutOrder.getOrderDateTime(), orderStatus, List.of(orderLineItem)
            );
            when(orderRepository.findById(nonServedOrder.getId())).thenReturn(Optional.ofNullable(nonServedOrder));

            assertThatThrownBy(() -> orderService.complete(nonServedOrder.getId())).isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문이 존재하지 않으면 예외가 발생합니다")
        @Test
        void completeNonExistentOrder() {
            when(orderRepository.findById(takeOutOrder.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.complete(takeOutOrder.getId())).isInstanceOf(NoSuchElementException.class);
        }
    }
}
