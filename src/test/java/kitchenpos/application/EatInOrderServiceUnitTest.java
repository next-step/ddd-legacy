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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.fixture.MenuFixture.createMenuId;
import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.menuProduct;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderTableFixture.*;
import static kitchenpos.fixture.ProductFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("매장 주문 서비스 단위 테스트")
class EatInOrderServiceUnitTest {

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

    @DisplayName("매장 주문을 생성할 때")
    @Nested
    class Create {

        private Menu menu;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(null, menu);
            this.orderTable = orderTable(ORDER_TABLE_NAME, COUPLE_GUESTS, IS_OCCUPIED);
        }

        @DisplayName("대기 상태의 주문을 생성할 수 있습니다")
        @Test
        void createOrder() {
            final Order eatInOrder = eatInOrder(null, null, orderTable, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
            when(orderRepository.save(any(Order.class))).then(returnsFirstArg());

            final Order actual = orderService.create(eatInOrder);

            assertAll(
                    () -> assertThat(actual.getId()).isNotNull(),
                    () -> assertThat(actual.getOrderDateTime()).isBeforeOrEqualTo(LocalDateTime.now()),
                    () -> assertThat(actual.getDeliveryAddress()).isNull(),
                    () -> assertThat(actual.getStatus()).isEqualTo(WAITING),
                    () -> assertThat(actual.getType()).isEqualTo(EAT_IN),
                    () -> assertThat(actual.getOrderTable()).isEqualTo(orderTable)
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
                            orderTable,
                            List.of(orderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목이 없거나 비어있으면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 항목: {0}")
        @NullAndEmptySource
        void createOrderWithoutOrderLineItems(final List<OrderLineItem> orderLineItems) {
            assertThatThrownBy(() -> orderService.create(
                    order(
                            null,
                            null,
                            null,
                            WAITING,
                            EAT_IN,
                            orderTable,
                            orderLineItems
                    )
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 개수와 주문 항목의 개수가 다르면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuCount() {
            final Menu otherMenu = menu(menuGroup(), List.of(menuProduct(product())));
            final OrderLineItem otherOrderLineItem = orderLineItem(null, otherMenu);
            final Order deliveryOrder = eatInOrder(
                    null,
                    null,
                    orderTable,
                    WAITING,
                    List.of(orderLineItem, otherOrderLineItem)
            );
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(deliveryOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목의 메뉴가 존재하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithoutMenus() {
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of());

            assertThatThrownBy(() -> orderService.create(
                    eatInOrder(null, null, orderTable, WAITING, List.of(orderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 미노출 상태이면 예외가 발생합니다")
        @Test
        void createOrderWithNonDisplayedMenu() {
            final Menu nonDisplayedMenu = menu(menu.getId(), menu.getName(), menu.getPrice(), menu.getMenuGroup(), menu.getMenuProducts(), false);

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(nonDisplayedMenu));
            when(menuRepository.findById(nonDisplayedMenu.getId())).thenReturn(Optional.ofNullable(nonDisplayedMenu));

            assertThatThrownBy(() -> orderService.create(
                    eatInOrder(null, null, orderTable, WAITING, List.of(orderLineItem))
            )).isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문 항목의 메뉴의 가격이 일치하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuPrice() {
            final BigDecimal differentPrice = menu.getPrice().add(BigDecimal.ONE);
            final Menu differentPriceMenu = menu(menu.getId(), menu.getName(), differentPrice, menu.getMenuGroup(), menu.getMenuProducts(), menu.isDisplayed());
            final Order eatInOrder = eatInOrder(null, null, orderTable, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(differentPriceMenu));
            when(menuRepository.findById(differentPriceMenu.getId())).thenReturn(Optional.ofNullable(differentPriceMenu));

            assertThatThrownBy(() -> orderService.create(eatInOrder))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("가게 테이블이 존재하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithoutOrderTable() {
            final Order eatInOrder = eatInOrder(null, null, orderTable, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.create(eatInOrder))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("가게 테이블이 사용중인 상태가 아니라면 예외가 발생합니다.")
        @Test
        void createOrderWithEmptyOrderTable() {
            final OrderTable emptyOrderTable = orderTable(createOrderTableId(), ORDER_TABLE_NAME, 0, false);
            final Order eatInOrder = eatInOrder(null, null, emptyOrderTable, WAITING, List.of(orderLineItem));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderTableRepository.findById(emptyOrderTable.getId())).thenReturn(Optional.of(emptyOrderTable));

            assertThatThrownBy(() -> orderService.create(eatInOrder))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("매장 주문이 대기 상태일 때")
    @Nested
    class Waiting {

        private Menu menu;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;
        private Order eatInOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.orderTable = orderTable(createOrderTableId(), ORDER_TABLE_NAME, COUPLE_GUESTS, IS_OCCUPIED);
            this.eatInOrder = eatInOrder(createOrderId(), createOrderDateTime(), orderTable, WAITING, List.of(orderLineItem));
        }

        @DisplayName("대기 상태의 주문을 수락할 수 있습니다")
        @Test
        void acceptOrder() {
            when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.of(eatInOrder));

            final Order actual = orderService.accept(eatInOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(eatInOrder.getId()),
                    () -> assertThat(actual.getStatus()).isEqualTo(ACCEPTED)
            );
        }

        @DisplayName("존재하지 않는 주문을 수락하려고 하면 예외가 발생합니다")
        @Test
        void acceptNonExistentOrder() {
            when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.accept(eatInOrder.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("대기 상태가 아닌 주문을 수락하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "COMPLETED"})
        void acceptNotWaitingOrder(final OrderStatus orderStatus) {
            final Order acceptedOrder = eatInOrder(
                    eatInOrder.getId(), eatInOrder.getOrderDateTime(), eatInOrder.getOrderTable(), orderStatus, eatInOrder.getOrderLineItems()
            );
            when(orderRepository.findById(acceptedOrder.getId())).thenReturn(Optional.of(acceptedOrder));

            assertThatThrownBy(() -> orderService.accept(acceptedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("매장 주문이 접수 되었을 때")
    @Nested
    class Accepted {

        private Menu menu;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;
        private Order eatInOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.orderTable = orderTable(createOrderTableId(), ORDER_TABLE_NAME, COUPLE_GUESTS, IS_OCCUPIED);
            this.eatInOrder = eatInOrder(createOrderId(), createOrderDateTime(), orderTable, ACCEPTED, List.of(orderLineItem));
        }

        @DisplayName("접수 상태의 주문을 서빙할 수 있습니다")
        @Test
        void serveOrder() {
            when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.of(eatInOrder));

            final Order actual = orderService.serve(eatInOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(eatInOrder.getId()),
                    () -> assertThat(actual.getStatus()).isEqualTo(SERVED)
            );
        }

        @DisplayName("존재하지 않는 주문을 서빙하려고 하면 예외가 발생합니다")
        @Test
        void serveNonExistentOrder() {
            when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.serve(eatInOrder.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("접수 상태가 아닌 주문을 서빙하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "COMPLETED"})
        void serveNotAcceptedOrder(final OrderStatus orderStatus) {
            final Order servedOrder = eatInOrder(
                    eatInOrder.getId(), eatInOrder.getOrderDateTime(), eatInOrder.getOrderTable(), orderStatus, eatInOrder.getOrderLineItems()
            );

            when(orderRepository.findById(servedOrder.getId())).thenReturn(Optional.of(servedOrder));

            assertThatThrownBy(() -> orderService.serve(servedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("매장 주문이 서빙 되었을 때")
    @Nested
    class Served {

        private Menu menu;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;
        private Order eatInOrder;

        @BeforeEach
        void setUp() {
            this.menu = menu(menuGroup(), List.of(
                    menuProduct(product(createMenuId(), SEASONED_CHICKEN, SEASONED_CHICKEN_PRICE)),
                    menuProduct(product(createMenuId(), FRIED_CHICKEN, SEASONED_CHICKEN_PRICE))
            ));
            this.orderLineItem = orderLineItem(menu);
            this.orderTable = orderTable(createOrderTableId(), ORDER_TABLE_NAME, COUPLE_GUESTS, IS_OCCUPIED);
            this.eatInOrder = eatInOrder(createOrderId(), createOrderDateTime(), orderTable, SERVED, List.of(orderLineItem));
        }

        @DisplayName("주문을 완료할 수 있습니다")
        @Test
        void completeOrder() {
            when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.of(eatInOrder));
            when(orderRepository.existsByOrderTableAndStatusNot(orderTable, COMPLETED)).thenReturn(true);

            final Order actual = orderService.complete(eatInOrder.getId());

            assertAll(
                    () -> assertThat(actual.getId()).isEqualTo(eatInOrder.getId()),
                    () -> assertThat(actual.getStatus()).isEqualTo(COMPLETED),
                    () -> assertThat(actual.getOrderTable().isOccupied()).isTrue(),
                    () -> assertThat(actual.getOrderTable().getNumberOfGuests()).isEqualTo(COUPLE_GUESTS)
            );
        }

        @DisplayName("존재하지 않는 주문을 완료하려고 하면 예외가 발생합니다")
        @Test
        void completeNonExistentOrder() {
            when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.complete(eatInOrder.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("서빙 상태가 아닌 주문을 완료하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "COMPLETED"})
        void completeNotServedOrder(final OrderStatus orderStatus) {
            final Order completedOrder = eatInOrder(eatInOrder.getId(), eatInOrder.getOrderDateTime(), orderTable, orderStatus, List.of(orderLineItem));

            when(orderRepository.findById(completedOrder.getId())).thenReturn(Optional.of(completedOrder));

            assertThatThrownBy(() -> orderService.complete(completedOrder.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("모든 주문이 완료되었다면 가게 테이블을 비웁니다")
        @Test
        void completeOrderAndEmptyOrderTable() {
            when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.of(eatInOrder));
            when(orderRepository.existsByOrderTableAndStatusNot(orderTable, COMPLETED)).thenReturn(false);

            final Order completedOrder = orderService.complete(eatInOrder.getId());

            assertAll(
                    () -> assertThat(completedOrder.getId()).isEqualTo(eatInOrder.getId()),
                    () -> assertThat(completedOrder.getStatus()).isEqualTo(COMPLETED),
                    () -> assertThat(completedOrder.getOrderTable().isOccupied()).isFalse(),
                    () -> assertThat(completedOrder.getOrderTable().getNumberOfGuests()).isEqualTo(0)
            );
        }
    }
}
