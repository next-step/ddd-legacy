package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.mock.OrderBuilder;
import kitchenpos.mock.OrderLineItemBuilder;
import kitchenpos.mock.OrderTableBuilder;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    OrderBo orderBo;

    @DisplayName("새로운 주문을 생성할 수 있다.")
    @Test
    void create() {
        // given
        // 주문은 테이블에서 받을 수 있다.
        // 테이블에서 다수의 주문을 받을 수 있다.
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(3)
                .withEmpty(false)
                .build();
        OrderLineItem orderLineItem = OrderLineItemBuilder.mock()
                .withMenuId(1L)
                .withQuantity(1)
                .build();
        Order newOrder = OrderBuilder.mock()
                .withOrderTableId(orderTable.getId())
                .withOrderLineItems(Collections.singletonList(orderLineItem))
                .build();

        given(menuDao.countByIdIn(any())).willReturn(1L);
        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(newOrder)).willAnswer(invocation -> {
            newOrder.setId(1L);
            return newOrder;
        });
        given(orderLineItemDao.save(any(OrderLineItem.class))).willAnswer(invocation -> {
            orderLineItem.setSeq(1L);
            orderLineItem.setOrderId(newOrder.getId());
            return orderLineItem;
        });

        // when
        final Order result = orderBo.create(newOrder);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderTableId()).isEqualTo(orderTable.getId());
        // 주문 시, 주문상태는 조리중이다.
        // 주문상태는 조리중/식사중/완료가 있다.
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(result.getOrderedTime()).isNotNull();
        assertThat(result.getOrderLineItems().size()).isEqualTo(1);
        assertThat(result.getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem);
    }

    @DisplayName("새로운 주문 생성 시, 테이블이 공석이면 안된다.")
    @Test
    void tableShouldNotBeEmpty() {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(3)
                .withEmpty(true) // 공석
                .build();
        OrderLineItem orderLineItem = OrderLineItemBuilder.mock()
                .withMenuId(1L)
                .withQuantity(1)
                .build();
        Order newOrder = OrderBuilder.mock()
                .withOrderTableId(orderTable.getId())
                .withOrderLineItems(Collections.singletonList(orderLineItem))
                .build();

        given(menuDao.countByIdIn(any())).willReturn(1L);
        given(orderTableDao.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        // when
        // then
        assertThatThrownBy(() -> {
            orderBo.create(newOrder);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 시 1개 이상의 메뉴를 시킬 수 있으며, 메뉴별로 1개 이상 시킬 수 있다.")
    @Test
    void orderShouldContainAtLeastOneMenu() {
        // given
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(3)
                .withEmpty(true) // 공석
                .build();
        Order newOrder = OrderBuilder.mock()
                .withOrderTableId(orderTable.getId())
                .withOrderLineItems(new ArrayList<>())
                .build();

        // when
        // then
        assertThatThrownBy(() -> {
            orderBo.create(newOrder);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("전체 주문 리스트를 조회할 수 있으며, 주문메뉴 리스트도 같이 조회된다.")
    @Test
    void list() {
        // given
        Long orderId = 1L;
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(3)
                .withEmpty(true) // 공석
                .build();
        OrderLineItem orderLineItem = OrderLineItemBuilder.mock()
                .withSeq(1L)
                .withOrderId(orderId)
                .withMenuId(1L)
                .withQuantity(1)
                .build();
        Order order = OrderBuilder.mock()
                .withId(orderId)
                .withOrderTableId(orderTable.getId())
                .withOrderStatus(OrderStatus.COMPLETION.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(Collections.singletonList(orderLineItem))
                .build();

        given(orderDao.findAll()).willReturn(new ArrayList<>(Arrays.asList(order)));
        given(orderLineItemDao.findAllByOrderId(order.getId())).willReturn(Collections.singletonList(orderLineItem));

        // when
        final List<Order> result = orderBo.list();

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(order.getId());
        assertThat(result.get(0).getOrderTableId()).isEqualTo(order.getOrderTableId());
        assertThat(result.get(0).getOrderStatus()).isEqualTo(order.getOrderStatus());
        assertThat(result.get(0).getOrderedTime()).isEqualTo(order.getOrderedTime());
        assertThat(result.get(0).getOrderLineItems().size()).isEqualTo(order.getOrderLineItems().size());
        assertThat(result.get(0).getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem);
    }

    @DisplayName("주문의 주문상태를 변경할 수 있다.")
    @ParameterizedTest
    @MethodSource(value = "provideOrderStatus")
    void changeOrderStatus(OrderStatus newOrderStatus) {
        // given
        Long orderId = 1L;
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(3)
                .withEmpty(true) // 공석
                .build();
        OrderLineItem orderLineItem = OrderLineItemBuilder.mock()
                .withSeq(1L)
                .withOrderId(orderId)
                .withMenuId(1L)
                .withQuantity(1)
                .build();
        Order order = OrderBuilder.mock()
                .withId(orderId)
                .withOrderTableId(orderTable.getId())
                .withOrderStatus(OrderStatus.COOKING.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(Collections.singletonList(orderLineItem))
                .build();

        Order newOrder = new Order();
        newOrder.setOrderStatus(newOrderStatus.name());

        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));
        given(orderLineItemDao.findAllByOrderId(order.getId())).willReturn(new ArrayList<>(Arrays.asList(orderLineItem)));

        // when
        final Order result = orderBo.changeOrderStatus(order.getId(), OrderBuilder.mock().withOrderStatus(newOrderStatus.name()).build());

        // then
        assertThat(result.getId()).isEqualTo(order.getId());
        assertThat(result.getOrderTableId()).isEqualTo(order.getOrderTableId());
        assertThat(result.getOrderStatus()).isEqualTo(newOrderStatus.name());
        assertThat(result.getOrderedTime()).isEqualTo(order.getOrderedTime());
        assertThat(result.getOrderLineItems().size()).isEqualTo(order.getOrderLineItems().size());
        assertThat(result.getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem);
    }

    private static Stream<OrderStatus> provideOrderStatus() {
        return Stream.of(OrderStatus.COOKING, OrderStatus.MEAL);
    }

    @DisplayName("주문의 주문상태 변경 시, 이미 주문상태가 완료인 경우에는 변경할 수 없다.")
    @Test
    void changeOrderStatusThrowErrorWhenCompletion() {
        // given
        Long orderId = 1L;
        OrderTable orderTable = OrderTableBuilder.mock()
                .withId(1L)
                .withNumberOfGuests(3)
                .withEmpty(true) // 공석
                .build();
        OrderLineItem orderLineItem = OrderLineItemBuilder.mock()
                .withSeq(1L)
                .withOrderId(orderId)
                .withMenuId(1L)
                .withQuantity(1)
                .build();
        Order order = OrderBuilder.mock()
                .withId(orderId)
                .withOrderTableId(orderTable.getId())
                .withOrderStatus(OrderStatus.COMPLETION.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(Collections.singletonList(orderLineItem))
                .build();

        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));

        // when
        // then
        assertThatThrownBy(() -> {
            orderBo.changeOrderStatus(order.getId(), OrderBuilder.mock().withOrderStatus(OrderStatus.COOKING.name()).build());
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
