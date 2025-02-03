package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.MenuFixture.menu;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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

    @DisplayName("매장 식사 주문을 생성할 수 있습니다.")
    @Test
    void createOrder() {
        final Menu menu = menu();
        final long quantity = 1L;
        final OrderLineItem orderLineItem = orderLineItem(menu, quantity, menu.getPrice().multiply(BigDecimal.valueOf(quantity)));
        final OrderTable orderTable = orderTable(createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, 2, true);

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

    @DisplayName("포장 주문을 생성할 수 있습니다.")
    @Test
    void createTakeoutOrder() {
        final Menu menu = menu();
        final long quantity = 1L;
        final OrderLineItem orderLineItem = orderLineItem(menu, quantity, menu.getPrice().multiply(BigDecimal.valueOf(quantity)));

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
}
