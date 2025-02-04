package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenuId;
import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.MenuGroupFixture.menuGroup;
import static kitchenpos.fixture.MenuProductFixture.menuProduct;
import static kitchenpos.fixture.OrderFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TaekOutOrderServiceTest {

    @MockBean
    @Autowired
    OrderRepository orderRepository;

    @MockBean
    @Autowired
    MenuRepository menuRepository;

    @MockBean
    @Autowired
    OrderTableRepository orderTableRepository;

    @MockBean
    @Autowired
    KitchenridersClient kitchenridersClient;

    @Autowired
    OrderService orderService;


    @DisplayName("포장 주문을 생성할 때")
    @Nested
    class Create {

        private Menu menu;
        private long quantity;
        private BigDecimal price;
        private OrderLineItem orderLineItem;

        @BeforeEach
        void setUp() {
            this.menu = menu();
            this.quantity = 1L;
            this.price = menu.getPrice().multiply(BigDecimal.valueOf(quantity));
            this.orderLineItem = orderLineItem(null, menu, quantity, price);
        }

        @DisplayName("포장 주문을 생성할 수 있습니다.")
        @Test
        void createTakeoutOrder() {
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderRepository.save(any(Order.class))).then(returnsFirstArg());

            final Order order = orderService.create(
                    takeoutOrder(null, null, OrderStatus.WAITING, List.of(orderLineItem))
            );
            assertAll(
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getOrderDateTime()).isBeforeOrEqualTo(LocalDateTime.now()),
                    () -> assertThat(order.getDeliveryAddress()).isNull(),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(order.getType()).isEqualTo(OrderType.TAKEOUT),
                    () -> assertThat(order.getOrderTable()).isNull()
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
                            OrderStatus.WAITING,
                            OrderType.TAKEOUT,
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
                            OrderStatus.WAITING,
                            orderLineItems
                    )
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴의 개수와 주문 항목의 개수가 다르면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuCount() {
            final Menu otherMenu = menu(
                    createMenuId(),
                    "otherMenu",
                    BigDecimal.valueOf(10000),
                    menuGroup(),
                    List.of(menuProduct()),
                    true
            );
            final OrderLineItem otherOrderLineItem = orderLineItem(null, otherMenu, 1L, otherMenu.getPrice());
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(
                    takeoutOrder(
                            null,
                            null,
                            OrderStatus.WAITING,
                            List.of(orderLineItem, otherOrderLineItem)
                    )
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목 중 하나라도 수량이 0보다 작으면 예외가 발생합니다")
        @Test
        void createOrderWithNegativeQuantity() {
            final long negativeQuantity = -1L;
            final OrderLineItem negativeOrderLineItem = orderLineItem(null, menu, negativeQuantity, orderLineItemPrice(menu.getPrice(), negativeQuantity));

            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));

            assertThatThrownBy(() -> orderService.create(
                    takeoutOrder(null, null, OrderStatus.WAITING, List.of(negativeOrderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 존재하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithNonExistentMenu() {
            final Menu nonExistentMenu = menu(
                    createMenuId(),
                    "nonExistentMenu",
                    BigDecimal.valueOf(10000),
                    menuGroup(),
                    List.of(menuProduct()),
                    true
            );
            final OrderLineItem nonExistentOrderLineItem = orderLineItem(null, nonExistentMenu, 1L, nonExistentMenu.getPrice());
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(nonExistentMenu.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.create(
                    takeoutOrder(null, null, OrderStatus.WAITING, List.of(nonExistentOrderLineItem))
            )).isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문 항목의 메뉴의 가격이 일치하지 않으면 예외가 발생합니다")
        @Test
        void createOrderWithDifferentMenuPrice() {
            final BigDecimal differentPrice = menu.getPrice().add(BigDecimal.ONE);
            final Menu differentPriceMenu = menu(menu.getId(), menu.getName(), differentPrice, menu.getMenuGroup(), menu.getMenuProducts(), menu.isDisplayed());
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(differentPriceMenu));
            when(menuRepository.findById(differentPriceMenu.getId())).thenReturn(Optional.ofNullable(differentPriceMenu));

            assertThatThrownBy(() -> orderService.create(
                    takeoutOrder(null, null, OrderStatus.WAITING, List.of(orderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 미노출 상태이면 예외가 발생합니다")
        @Test
        void createOrderWithNonDisplayedMenu() {
            final Menu nonDisplayedMenu = menu(menu.getId(), menu.getName(), menu.getPrice(), menu.getMenuGroup(), menu.getMenuProducts(), false);
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(nonDisplayedMenu));
            when(menuRepository.findById(nonDisplayedMenu.getId())).thenReturn(Optional.ofNullable(nonDisplayedMenu));

            assertThatThrownBy(() -> orderService.create(
                    takeoutOrder(null, null, OrderStatus.WAITING, List.of(orderLineItem))
            )).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("포장 주문을 수락할 때")
    @Nested
    class Accept {
        private UUID orderId;
        private LocalDateTime orderDateTime;
        private OrderLineItem orderLineItem;
        private Order order;

        @BeforeEach
        void setUp() {
            this.orderId = createOrderId();
            this.orderDateTime = LocalDateTime.now();
            this.orderLineItem = orderLineItem(createOrderLineItemId(), menu(), 1L, BigDecimal.valueOf(10000));
            this.order = takeoutOrder(orderId, orderDateTime, OrderStatus.WAITING, List.of(orderLineItem));
        }

        @DisplayName("포장 주문을 수락할 수 있습니다.")
        @Test
        void acceptTakeOutOrder() {
            when(orderRepository.findById(order.getId())).thenReturn(Optional.ofNullable(order));
            doNothing().when(kitchenridersClient).requestDelivery(any(), any(), any());

            final Order acceptedOrder = orderService.accept(order.getId());

            assertAll(
                    () -> assertThat(acceptedOrder.getId()).isEqualTo(order.getId()),
                    () -> assertThat(acceptedOrder.getType()).isEqualTo(order.getType()),
                    () -> assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
            );
        }

        @DisplayName("대기 상태가 아닌 주문을 수락하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "COMPLETED"})
        void acceptNonWaitingOrder(final OrderStatus orderStatus) {
            final Order nonWaitingOrder = takeoutOrder(
                    orderId, orderDateTime, orderStatus, List.of(orderLineItem)
            );
            when(orderRepository.findById(nonWaitingOrder.getId())).thenReturn(Optional.ofNullable(nonWaitingOrder));

            assertThatThrownBy(() -> orderService.accept(nonWaitingOrder.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("포장 주문을 서빙할 때")
    @Nested
    class Server {
        private UUID orderId;
        private LocalDateTime orderDateTime;
        private OrderLineItem orderLineItem;
        private Order order;

        @BeforeEach
        void setUp() {
            this.orderId = createOrderId();
            this.orderDateTime = LocalDateTime.now();
            this.orderLineItem = orderLineItem(createOrderLineItemId(), menu(), 1L, BigDecimal.valueOf(10000));
            this.order = takeoutOrder(orderId, orderDateTime, OrderStatus.ACCEPTED, List.of(orderLineItem));
        }


        @DisplayName("포장 주문을 서빙할 수 있습니다.")
        @Test
        void serveTakeOutOrder() {
            when(orderRepository.findById(order.getId())).thenReturn(Optional.ofNullable(order));

            final Order servedOrder = orderService.serve(order.getId());

            assertAll(
                    () -> assertThat(servedOrder.getId()).isEqualTo(order.getId()),
                    () -> assertThat(servedOrder.getType()).isEqualTo(order.getType()),
                    () -> assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED)
            );
        }

        @DisplayName("수락 상태가 아닌 주문을 서빙하려고 하면 예외가 발생합니다")
        @ParameterizedTest(name = "주문 상태: {0}")
        @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "COMPLETED"})
        void serveNonAcceptedOrder(final OrderStatus orderStatus) {
            final Order nonAcceptedOrder = takeoutOrder(
                    orderId, orderDateTime, orderStatus, List.of(orderLineItem)
            );
            when(orderRepository.findById(nonAcceptedOrder.getId())).thenReturn(Optional.ofNullable(nonAcceptedOrder));

            assertThatThrownBy(() -> orderService.serve(nonAcceptedOrder.getId())).isInstanceOf(IllegalStateException.class);
        }
    }

    private BigDecimal orderLineItemPrice(final BigDecimal price, final long quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
