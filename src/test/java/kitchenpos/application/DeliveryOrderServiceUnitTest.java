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
import java.nio.channels.ConnectionPendingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.DELIVERY;
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

@DisplayName("배달 주문 서비스 단위 테스트")
class DeliveryOrderServiceUnitTest {

    private OrderRepository orderRepository = mock(OrderRepository.class);
    private MenuRepository menuRepository = mock(MenuRepository.class);
    private OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private KitchenridersClient kitchenridersClient = mock(KitchenridersClient.class);
    private OrderService orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

    @BeforeEach
    void setUp() {
        this.orderRepository = mock(OrderRepository.class);
        this.menuRepository = mock(MenuRepository.class);
        this.orderTableRepository = mock(OrderTableRepository.class);
        this.kitchenridersClient = mock(KitchenridersClient.class);
        this.orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("배달 주문이 생성되지 않았다면")
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

        @DisplayName("대기 상태의 배달 주문을 생성할 수 있습니다.")
        @Test
        void createDeliveryOrder() {
            final Order deliveryOrder = deliveryOrder(null, null, DELIVERY_ADDRESS, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderRepository.save(any(Order.class))).then(returnsFirstArg());

            final Order actual = orderService.create(deliveryOrder);
            assertAll(
                    () -> assertThat(actual.getId()).isNotNull(),
                    () -> assertThat(actual.getOrderDateTime()).isBeforeOrEqualTo(LocalDateTime.now()),
                    () -> assertThat(actual.getDeliveryAddress()).isEqualTo(DELIVERY_ADDRESS),
                    () -> assertThat(actual.getStatus()).isEqualTo(WAITING),
                    () -> assertThat(actual.getType()).isEqualTo(DELIVERY)
            );
        }

        @DisplayName("주문 형식이 없으면 예외가 발생합니다")
        @Test
        void createOrderWithoutType() {
            assertThatThrownBy(() -> orderService.create(
                    order(null,
                            null,
                            DELIVERY_ADDRESS,
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
            final Order deliveryOrder = deliveryOrder(
                    null,
                    null,
                    DELIVERY_ADDRESS,
                    WAITING,
                    orderLineItems
            );

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 개수와 주문 항목의 개수가 다르면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuCount() {
            final Menu otherMenu = menu(menuGroup(), List.of(menuProduct(product())));
            final OrderLineItem otherOrderLineItem = orderLineItem(null, otherMenu);
            final Order deliveryOrder = deliveryOrder(
                    null,
                    null,
                    DELIVERY_ADDRESS,
                    WAITING,
                    List.of(orderLineItem, otherOrderLineItem)
            );

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목 중 하나라도 수량이 0보다 작으면 예외가 발생합니다")
        @ParameterizedTest(name = "수량: {0}")
        @ValueSource(longs = {-1L, -10L, -100L})
        void createOrderWithNegativeQuantity(final long negativeQuantity) {
            final OrderLineItem negativeOrderLineItem = orderLineItem(null, menu, negativeQuantity);
            final Order deliveryOrder = deliveryOrder(null, null, DELIVERY_ADDRESS, WAITING, List.of(negativeOrderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목의 메뉴가 존재하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithoutMenus() {
            final Order deliveryOrder = deliveryOrder(null, null, DELIVERY_ADDRESS, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of());

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목의 메뉴의 가격이 일치하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuPrice() {
            final BigDecimal differentPrice = menu.getPrice().add(BigDecimal.ONE);
            final Menu differentPriceMenu = menu(menu.getId(), menu.getName(), differentPrice, menu.getMenuGroup(), menu.getMenuProducts(), menu.isDisplayed());
            final Order deliveryOrder = deliveryOrder(null, null, DELIVERY_ADDRESS, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(differentPriceMenu));
            when(menuRepository.findById(differentPriceMenu.getId())).thenReturn(Optional.ofNullable(differentPriceMenu));

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 미노출 상태이면 예외가 발생합니다")
        @Test
        void createOrderWithNonDisplayedMenu() {
            final Menu nonDisplayedMenu = menu(menu.getId(), menu.getName(), menu.getPrice(), menu.getMenuGroup(), menu.getMenuProducts(), false);
            final Order deliveryOrder = deliveryOrder(null, null, DELIVERY_ADDRESS, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(nonDisplayedMenu));
            when(menuRepository.findById(nonDisplayedMenu.getId())).thenReturn(Optional.ofNullable(nonDisplayedMenu));

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("배달 주소가 없으면 예외가 발생합니다")
        @ParameterizedTest(name = "배달 주소: {0}")
        @NullAndEmptySource
        void createOrderWithoutDeliveryAddress(final String deliveryAddress) {
            final Order deliveryOrder = deliveryOrder(null, null, deliveryAddress, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("배달 주문이 대기 상태라면")
    @Nested
    class OrderStatusIsWaited {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order deliveryOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.deliveryOrder = deliveryOrder(createOrderId(), createOrderDateTime(), DELIVERY_ADDRESS, WAITING, List.of(orderLineItem));
        }

        @DisplayName("배달 주문을 수락할 수 있습니다.")
        @Test
        void acceptDeliveryOrder() {
            when(orderRepository.findById(deliveryOrder.getId())).thenReturn(Optional.ofNullable(deliveryOrder));
            doNothing().when(kitchenridersClient).requestDelivery(any(), any(), any());

            final Order actual = orderService.accept(deliveryOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(deliveryOrder.getId()),
                    () -> assertThat(actual.getDeliveryAddress()).isEqualTo(deliveryOrder.getDeliveryAddress()),
                    () -> assertThat(actual.getType()).isEqualTo(deliveryOrder.getType()),
                    () -> assertThat(actual.getStatus()).isEqualTo(ACCEPTED)
            );
        }

        @DisplayName("대기 상태가 아닌 주문을 수락하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "COMPLETED"})
        void acceptNonWaitingOrder(final OrderStatus orderStatus) {
            final Order nonWaitingOrder = deliveryOrder(
                    deliveryOrder.getId(), deliveryOrder.getOrderDateTime(), deliveryOrder.getDeliveryAddress(), orderStatus, deliveryOrder.getOrderLineItems()
            );
            when(orderRepository.findById(nonWaitingOrder.getId())).thenReturn(Optional.ofNullable(nonWaitingOrder));

            assertThatThrownBy(() -> orderService.accept(nonWaitingOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("배달 서비스 요청 중 오류가 발생하면 예외가 발생합니다")
        @Test
        void acceptOrderWithDeliveryServiceError() {
            final Class<ConnectionPendingException> exception = ConnectionPendingException.class;
            when(orderRepository.findById(deliveryOrder.getId())).thenReturn(Optional.ofNullable(deliveryOrder));
            doThrow(exception).when(kitchenridersClient).requestDelivery(any(), any(), any());

            assertThatThrownBy(() -> orderService.accept(deliveryOrder.getId()))
                    .isInstanceOf(exception);
        }
    }

    @DisplayName("배달 주문이 수락 상태라면")
    @Nested
    class OrderStatusIsAccepted {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order deliveryOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.deliveryOrder = deliveryOrder(createOrderId(), createOrderDateTime(), DELIVERY_ADDRESS, ACCEPTED, List.of(orderLineItem));
        }

        @DisplayName("배달 주문을 서빙할 수 있습니다.")
        @Test
        void serveDeliveryOrder() {
            when(orderRepository.findById(deliveryOrder.getId())).thenReturn(Optional.ofNullable(deliveryOrder));

            final Order actual = orderService.serve(deliveryOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(deliveryOrder.getId()),
                    () -> assertThat(actual.getDeliveryAddress()).isEqualTo(deliveryOrder.getDeliveryAddress()),
                    () -> assertThat(actual.getType()).isEqualTo(deliveryOrder.getType()),
                    () -> assertThat(actual.getStatus()).isEqualTo(SERVED)
            );
        }

        @DisplayName("수락 상태가 아닌 주문을 서빙하려고 하면 예외가 발생합니다")
        @Test
        void serveNonAcceptedOrder() {
            final Order nonAcceptedOrder = deliveryOrder(
                    deliveryOrder.getId(), deliveryOrder.getOrderDateTime(), deliveryOrder.getDeliveryAddress(), WAITING, deliveryOrder.getOrderLineItems()
            );

            when(orderRepository.findById(nonAcceptedOrder.getId())).thenReturn(Optional.ofNullable(nonAcceptedOrder));

            assertThatThrownBy(() -> orderService.serve(nonAcceptedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("배달 주문이 서빙된 상태라면")
    @Nested
    class OrderStatusIsServed {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order deliveryOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.deliveryOrder = deliveryOrder(createOrderId(), createOrderDateTime(), DELIVERY_ADDRESS, SERVED, List.of(orderLineItem));
        }

        @DisplayName("배달 주문을 배달 시작할 수 있습니다.")
        @Test
        void startDeliveryOrder() {
            when(orderRepository.findById(deliveryOrder.getId())).thenReturn(Optional.ofNullable(deliveryOrder));

            final Order actual = orderService.startDelivery(deliveryOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(deliveryOrder.getId()),
                    () -> assertThat(actual.getDeliveryAddress()).isEqualTo(deliveryOrder.getDeliveryAddress()),
                    () -> assertThat(actual.getType()).isEqualTo(deliveryOrder.getType()),
                    () -> assertThat(actual.getStatus()).isEqualTo(DELIVERING)
            );
        }

        @DisplayName("주문이 존재하지 않으면 예외가 발생합니다")
        @Test
        void startDeliveryNonExistentOrder() {
            final UUID nonExistentOrderId = createOrderId();
            when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.startDelivery(nonExistentOrderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("서빙 상태가 아닌 주문을 배달 시작하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERED", "DELIVERING", "COMPLETED"})
        void startDeliveryNonServedOrder(final OrderStatus orderStatus) {
            final Order nonServedOrder = deliveryOrder(deliveryOrder.getId(), deliveryOrder.getOrderDateTime(), deliveryOrder.getDeliveryAddress(), orderStatus, deliveryOrder.getOrderLineItems());

            when(orderRepository.findById(nonServedOrder.getId())).thenReturn(Optional.ofNullable(nonServedOrder));

            assertThatThrownBy(() -> orderService.startDelivery(nonServedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("배달 주문이 아닌 주문을 배달 시작하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 형식: {0}")
        @EnumSource(value = OrderType.class, names = {"EAT_IN", "TAKEOUT"})
        void startDeliveryNonDeliveryOrder(final OrderType orderType) {
            final Order nonDeliveryOrder = order(
                    deliveryOrder.getId(),
                    deliveryOrder.getOrderDateTime(),
                    deliveryOrder.getDeliveryAddress(),
                    SERVED,
                    orderType,
                    null,
                    deliveryOrder.getOrderLineItems()
            );
            when(orderRepository.findById(nonDeliveryOrder.getId())).thenReturn(Optional.ofNullable(nonDeliveryOrder));

            assertThatThrownBy(() -> orderService.startDelivery(nonDeliveryOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("배달 주문이 배달중 상태라면")
    @Nested
    class OrderStatusIsDelivering {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order deliveryOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.deliveryOrder = deliveryOrder(createOrderId(), createOrderDateTime(), DELIVERY_ADDRESS, DELIVERING, List.of(orderLineItem));
        }

        @DisplayName("배달 주문을 배달 완료할 수 있습니다.")
        @Test
        void completeDeliveryOrder() {
            when(orderRepository.findById(deliveryOrder.getId())).thenReturn(Optional.ofNullable(deliveryOrder));

            final Order actual = orderService.completeDelivery(deliveryOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(deliveryOrder.getId()),
                    () -> assertThat(actual.getDeliveryAddress()).isEqualTo(deliveryOrder.getDeliveryAddress()),
                    () -> assertThat(actual.getType()).isEqualTo(deliveryOrder.getType()),
                    () -> assertThat(actual.getStatus()).isEqualTo(DELIVERED)
            );
        }

        @DisplayName("주문이 존재하지 않으면 예외가 발생합니다")
        @Test
        void completeDeliveryNonExistentOrder() {
            final UUID nonExistentOrderId = createOrderId();
            when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.completeDelivery(nonExistentOrderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("배달 중이 아닌 주문을 배달 완료하려고 하면 예외가 발생합니다")
        @Test
        void completeDeliveryNonDeliveringOrder() {
            final Order nonDeliveringOrder = deliveryOrder(
                    deliveryOrder.getId(), deliveryOrder.getOrderDateTime(), deliveryOrder.getDeliveryAddress(), SERVED, deliveryOrder.getOrderLineItems()
            );

            when(orderRepository.findById(nonDeliveringOrder.getId())).thenReturn(Optional.ofNullable(nonDeliveringOrder));

            assertThatThrownBy(() -> orderService.completeDelivery(nonDeliveringOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("배달 주문이 배달 완료 상태라면")
    @Nested
    class OrderStatusIsDelivered {
        private Menu menu;
        private OrderLineItem orderLineItem;
        private Order deliveryOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.deliveryOrder = deliveryOrder(createOrderId(), createOrderDateTime(), DELIVERY_ADDRESS, DELIVERED, List.of(orderLineItem));
        }

        @DisplayName("배달 주문을 완료할 수 있습니다.")
        @Test
        void completeDeliveryOrder() {
            when(orderRepository.findById(deliveryOrder.getId())).thenReturn(Optional.ofNullable(deliveryOrder));

            final Order completedOrder = orderService.complete(deliveryOrder.getId());

            assertAll(
                    () -> assertThat(completedOrder.getId()).isEqualTo(deliveryOrder.getId()),
                    () -> assertThat(completedOrder.getDeliveryAddress()).isEqualTo(deliveryOrder.getDeliveryAddress()),
                    () -> assertThat(completedOrder.getType()).isEqualTo(deliveryOrder.getType()),
                    () -> assertThat(completedOrder.getStatus()).isEqualTo(COMPLETED)
            );
        }

        @DisplayName("주문이 존재하지 않으면 예외가 발생합니다")
        @Test
        void completeNonExistentOrder() {
            final UUID nonExistentOrderId = createOrderId();

            when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.complete(nonExistentOrderId))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("배달 주문이 완료되지 않은 상태에서 완료하려고 하면 예외가 발생합니다")
        @Test
        void completeNonDeliveredOrder() {
            final Order nonDeliveredOrder = deliveryOrder(
                    deliveryOrder.getId(), deliveryOrder.getOrderDateTime(), deliveryOrder.getDeliveryAddress(), DELIVERING, deliveryOrder.getOrderLineItems()
            );
            when(orderRepository.findById(nonDeliveredOrder.getId())).thenReturn(Optional.ofNullable(nonDeliveredOrder));

            assertThatThrownBy(() -> orderService.complete(nonDeliveredOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}

