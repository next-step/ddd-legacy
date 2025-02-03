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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EatInOrderServiceTest {

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

    @DisplayName("매장 식사 주문을 생성할 때")
    @Nested
    class Create {

        private Menu menu;
        private long quantity;
        private BigDecimal price;
        private OrderLineItem orderLineItem;
        private OrderTable orderTable;

        @BeforeEach
        void setUp() {
            this.menu = menu();
            this.quantity = 1L;
            this.price = orderLineItemPrice(menu.getPrice(), quantity);
            this.orderLineItem = orderLineItem(null, menu, quantity, price);
            this.orderTable = orderTable(createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, 2, true);
        }

        @DisplayName("대기 상태의 주문을 생성할 수 있다")
        @Test
        void createOrder() {
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
            when(orderRepository.save(any(Order.class))).then(returnsFirstArg());

            final Order order = orderService.create(
                    eatInOrder(null, null, orderTable, OrderStatus.WAITING, List.of(orderLineItem))
            );
            assertAll(
                    () -> assertThat(order.getId()).isNotNull(),
                    () -> assertThat(order.getOrderDateTime()).isBeforeOrEqualTo(LocalDateTime.now()),
                    () -> assertThat(order.getDeliveryAddress()).isNull(),
                    () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(order.getType()).isEqualTo(OrderType.EAT_IN),
                    () -> assertThat(order.getOrderTable()).isEqualTo(orderTable)
            );
        }

        @DisplayName("주문 형식이 없으면 예외가 발생한다")
        @Test
        void createOrderWithoutType() {
            assertThatThrownBy(() -> orderService.create(
                    order(
                            null,
                            null,
                            null,
                            OrderStatus.WAITING,
                            OrderType.EAT_IN,
                            orderTable,
                            List.of(orderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목이 없거나 비어있으면 예외가 발생한다")
        @ParameterizedTest(name = "주문 항목: {0}")
        @NullAndEmptySource
        void createOrderWithoutOrderLineItems(final List<OrderLineItem> orderLineItems) {
            assertThatThrownBy(() -> orderService.create(
                    order(
                            null,
                            null,
                            null,
                            OrderStatus.WAITING,
                            OrderType.EAT_IN,
                            orderTable,
                            orderLineItems
                    )
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목의 메뉴가 존재하지 않으면 예외가 발생한다")
        @Test
        void createOrderWithoutMenus() {
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of());

            assertThatThrownBy(() -> orderService.create(
                    eatInOrder(null, null, orderTable, OrderStatus.WAITING, List.of(orderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목의 메뉴의 가격이 일치하지 않으면 예외가 발생한다")
        @Test
        void createOrderWithDifferentMenuPrice() {
            final BigDecimal differentPrice = menu.getPrice().add(BigDecimal.ONE);
            final Menu differentPriceMenu = menu(menu.getId(), menu.getName(), differentPrice, menu.getMenuGroup(), menu.getMenuProducts(), menu.isDisplayed());
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(differentPriceMenu));
            when(menuRepository.findById(differentPriceMenu.getId())).thenReturn(Optional.ofNullable(differentPriceMenu));

            assertThatThrownBy(() -> orderService.create(
                    eatInOrder(null, null, orderTable, OrderStatus.WAITING, List.of(orderLineItem))
            )).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 테이블이 존재하지 않으면 예외가 발생한다")
        @Test
        void createOrderWithoutOrderTable() {
            when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.ofNullable(menu));
            when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.create(
                    eatInOrder(null, null, orderTable, OrderStatus.WAITING, List.of(orderLineItem))
            )).isInstanceOf(NoSuchElementException.class);
        }
    }

    private BigDecimal orderLineItemPrice(final BigDecimal price, final long quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
