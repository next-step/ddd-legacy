package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final OrderTableRepository orderTableRepository = mock(OrderTableRepository.class);
    private final KitchenridersClient kitchenridersClient = mock(KitchenridersClient.class);
    private final OrderService orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);

    @DisplayName("매장 주문을 등록할 수 있다.")
    @Test
    void createEatInOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);
        OrderTable orderTable = makeTestOrderTable("1번 테이블", 0);
        orderTable.setOccupied(true);

        Order eatInOrder = makeTestEatInOrder(OrderStatus.WAITING, orderLineItem, orderTable);

        when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.save(any(Order.class))).thenReturn(eatInOrder);

        // when
        Order resultOrder = orderService.create(eatInOrder);

        // then
        assertThat(resultOrder.getId()).isNotNull();
        assertThat(resultOrder.getType()).isEqualTo(eatInOrder.getType());
        assertThat(resultOrder.getStatus()).isEqualTo(eatInOrder.getStatus());
        assertThat(resultOrder.getOrderDateTime()).isEqualTo(eatInOrder.getOrderDateTime());
        assertThat(resultOrder.getOrderLineItems()).isEqualTo(eatInOrder.getOrderLineItems());
        assertThat(resultOrder.getOrderTable()).isEqualTo(eatInOrder.getOrderTable());
    }

    @DisplayName("테이크 아웃 주문을 등록할 수 있다.")
    @Test
    void createTakeOutOrder() {
        // given
        Product product = makeTestProduct("짜장면", BigDecimal.valueOf(5000));
        MenuProduct menuProduct = makeTestMenuProduct(product);
        MenuGroup menuGroup = makeTestMenuGroup("추천메뉴");
        Menu menu = makeTestMenu("중식", BigDecimal.valueOf(5000), menuGroup, menuProduct);

        OrderLineItem orderLineItem = makeTestOrderLineItem(BigDecimal.valueOf(5000), menu, 1);

        Order takeOutOrder = makeTestTakeOutOrder(OrderStatus.WAITING, orderLineItem);

        when(menuRepository.findAllByIdIn(anyList())).thenReturn(List.of(menu));
        when(menuRepository.findById(any())).thenReturn(Optional.of(menu));
        when(orderRepository.save(any(Order.class))).thenReturn(takeOutOrder);

        // when
        Order resultOrder = orderService.create(takeOutOrder);

        // then
        assertThat(resultOrder.getId()).isNotNull();
        assertThat(resultOrder.getType()).isEqualTo(takeOutOrder.getType());
        assertThat(resultOrder.getStatus()).isEqualTo(takeOutOrder.getStatus());
        assertThat(resultOrder.getOrderDateTime()).isEqualTo(takeOutOrder.getOrderDateTime());
        assertThat(resultOrder.getOrderLineItems()).isEqualTo(takeOutOrder.getOrderLineItems());
    }

    @Test
    void accept() {
    }

    @Test
    void serve() {
    }

    @Test
    void startDelivery() {
    }

    @Test
    void completeDelivery() {
    }

    @Test
    void complete() {
    }
}