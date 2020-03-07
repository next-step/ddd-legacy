package kitchenpos.bo;

import kitchenpos.builder.OrderBuilder;
import kitchenpos.builder.OrderLineItemBuilder;
import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {
    @Mock
    private MenuDao menuDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderLineItemDao orderLineItemDao;
    @Mock
    private OrderTableDao orderTableDao;
    @InjectMocks
    private OrderBo orderBo;

    private OrderBuilder orderBuilder = new OrderBuilder();
    private OrderLineItemBuilder orderLineItemBuilder  = new OrderLineItemBuilder();
    private OrderTableBuilder orderTableBuilder = new OrderTableBuilder();

    @Test
    @DisplayName("주문은 세부 내역이 있어야 생성할 수 있다")
    void orderHasOrderItems() {
        Order order = orderBuilder
                .id(1L)
                .orderTableId(1L)
                .orderLineItems(null)
                .build();

        assertThatThrownBy(() -> orderBo.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문의 세부 내역은 모두 메뉴에 존재하는 것이어야 한다")
    void orderItemIsExistInMenu() {
        Order order = orderBuilder
                .id(1L)
                .orderTableId(1L)
                .orderLineItems(asList(orderLineItemBuilder
                        .seq(1L)
                        .menuId(1L)
                        .orderId(1L)
                        .quantity(1)
                        .build()
                ))
                .build();

        given(menuDao.countByIdIn(anyList()))
                .willReturn((long) order.getOrderLineItems().size() + 1);

        assertThatThrownBy(() -> orderBo.create(order))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("주문은 테이블 번호가 반드시 존재해야 한다")
    void orderHasOrderTable() {
        Order order = orderBuilder
                .id(1L)
                .orderTableId(1L)
                .orderLineItems(asList(orderLineItemBuilder
                        .seq(1L)
                        .menuId(1L)
                        .orderId(1L)
                        .quantity(1)
                        .build()
                ))
                .build();

        given(menuDao.countByIdIn(anyList()))
                .willReturn((long) order.getOrderLineItems().size());

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.ofNullable(any(OrderTable.class)));

        assertThatThrownBy(() -> orderBo.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문시에 해당 테이블이 비어있으면 에외가 발생한다")
    void orderTableIsNotEmpty() {
        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.TRUE)
                .build();

        Order order = orderBuilder
                .id(1L)
                .orderTableId(1L)
                .orderLineItems(asList(orderLineItemBuilder
                        .seq(1L)
                        .menuId(1L)
                        .orderId(1L)
                        .quantity(1)
                        .build()
                ))
                .build();

        given(menuDao.countByIdIn(anyList()))
                .willReturn((long) order.getOrderLineItems().size());

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.ofNullable(orderTable));

        assertThatThrownBy(() -> orderBo.create(order))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("새로 생성되면 \"COOKING\" 상태이다")
    void newOrderStatusInCooking() {
        OrderLineItem orderLineItem = orderLineItemBuilder
                .seq(1L)
                .menuId(1L)
                .orderId(1L)
                .quantity(1)
                .build();

        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .build();

        Order order = orderBuilder
                .id(1L)
                .orderTableId(1L)
                .orderLineItems(asList(orderLineItem))
                .build();

        given(menuDao.countByIdIn(anyList()))
                .willReturn((long) order.getOrderLineItems().size());

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.ofNullable(orderTable));

        given(orderDao.save(any(Order.class)))
                .willReturn(order);

        given(orderLineItemDao.save(any(OrderLineItem.class)))
                .willReturn(orderLineItem);

        Order savedOrder = orderBo.create(order);

        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());

    }

    @Test
    @DisplayName("주문의 상태가 \"COMPLETION\" 인 경우 상태를 변경할 수 없다")
    void notChangeOrderStatusInCompletion() {
        Long orderId = 1L;

        OrderLineItem orderLineItem = orderLineItemBuilder
                .seq(1L)
                .menuId(1L)
                .orderId(1L)
                .quantity(1)
                .build();

        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .build();

        Order order = orderBuilder
                .id(orderId)
                .orderTableId(1L)
                .orderStatus(OrderStatus.COMPLETION.name())
                .orderLineItems(asList(orderLineItem))
                .build();

        given(orderDao.findById(orderId))
                .willReturn(Optional.ofNullable(order));

        assertThatThrownBy(() -> orderBo.changeOrderStatus(orderId, order))
                .isInstanceOf(IllegalArgumentException.class);

    }


    @Test
    @DisplayName("주문번호를 지정하여 주문상태를 변경할 수 있다")
    void changeOrderStatus() {
        Long orderId = 1L;

        OrderLineItem orderLineItem = orderLineItemBuilder
                .seq(1L)
                .menuId(1L)
                .orderId(1L)
                .quantity(1)
                .build();

        OrderTable orderTable = orderTableBuilder
                .id(1L)
                .empty(Boolean.FALSE)
                .build();

        Order order = orderBuilder
                .id(orderId)
                .orderTableId(1L)
                .orderStatus(OrderStatus.COOKING.name())
                .orderLineItems(asList(orderLineItem))
                .build();

        given(orderDao.findById(orderId))
                .willReturn(Optional.ofNullable(order));

        given(orderLineItemDao.findAllByOrderId(orderId))
                .willReturn(asList(orderLineItem));

        Order savedOrder = orderBo.changeOrderStatus(orderId, order);

        assertThat(savedOrder)
                .isEqualTo(order);

    }

    @Test
    @DisplayName("주문번호 순서로 주문 목록을 조회할 수 있어야 한다")
    void listOrders() {
        List<Order> orders = asList(
                orderBuilder
                        .id(1L)
                        .orderTableId(1L)
                        .orderedTime(LocalDateTime.now())
                        .orderLineItems(asList(orderLineItemBuilder
                                .seq(1L)
                                .menuId(1L)
                                .orderId(1L)
                                .quantity(1)
                                .build()
                        ))
                        .build()
        );

        given(orderDao.findAll())
                .willReturn(orders);

        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(null);

        List<Order> expectedOrders = orderBo.list();

        assertThat(expectedOrders)
                .hasSameSizeAs(orders)
                .isEqualTo(orders);

    }


}