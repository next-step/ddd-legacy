package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderTable;
import kitchenpos.support.OrderBuilder;
import kitchenpos.support.OrderLineItemBuilder;
import kitchenpos.support.OrderTableBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static kitchenpos.model.OrderStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class OrderBoTest extends MockTest {
    @Mock
    private MenuDao menuDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderLineItemDao orderLineItemDao;
    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private OrderBo sut;

    @Test
    @DisplayName("주문 생성")
    void createOrder() {
        // given
        long orderId = 1;
        long orderTableId = 100;
        final OrderLineItemBuilder orderLineItemBuilder = OrderLineItemBuilder.orderLineItem()
                .withMenuId(10);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItemBuilder.build());
        final OrderTable orderTable = OrderTableBuilder.orderTable()
                .withId(orderTableId)
                .withEmpty(false)
                .build();
        final OrderBuilder orderBuilder = OrderBuilder.order()
                .withOrderTableId(orderTableId)
                .withOrderStatus(COOKING.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(
                        orderLineItems
                );

        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(orderLineItems.size()));
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.save(any(Order.class)))
                .willReturn(orderBuilder.withId(orderId).build());
        given(orderLineItemDao.save(any(OrderLineItem.class)))
                .willReturn(
                        orderLineItemBuilder.withSeq(1000).withOrderId(orderId).build()
                );

        // when
        sut.create(orderBuilder.build());

        // then
        verify(menuDao).countByIdIn(anyList());
        verify(orderTableDao).findById(anyLong());
        verify(orderDao).save(any(Order.class));
        verify(orderLineItemDao).save(any(OrderLineItem.class));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("provideEmptyOrderLineItems")
    @DisplayName("주문 아이템 목록이 없으면 예외 던짐")
    void shouldThrowExceptionWithEmptyOrderLineItems(List<OrderLineItem> items) {
        // given
        final Order order = OrderBuilder.order()
                .withOrderTableId(10)
                .withOrderStatus(COOKING.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(
                        items
                ).build();

        // when & then
        assertThatThrownBy(() -> sut.create(order))
                .isInstanceOf(IllegalArgumentException.class);

        // and
        verifyNoInteractions(menuDao);
        verifyNoInteractions(orderTableDao);
        verifyNoInteractions(orderDao);
        verifyNoInteractions(orderLineItemDao);
    }

    private static Stream provideEmptyOrderLineItems() {
        return Stream.of(
                Arguments.of(Collections.emptyList())
        );
    }

    @Test
    @DisplayName("저장된 메뉴의 갯수와 요청된 메뉴의 갯수가 다르면 예외 던짐")
    void shouldThrowExceptionWhenMenuCountIsNotEqualToFounded() {
        // given
        final OrderLineItemBuilder orderLineItemBuilder = OrderLineItemBuilder.orderLineItem()
                .withMenuId(10);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItemBuilder.build());
        final OrderBuilder orderBuilder = OrderBuilder.order()
                .withOrderTableId(100)
                .withOrderStatus(COOKING.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(
                        orderLineItems
                );

        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(orderLineItems.size() + 1));

        // when
        assertThatThrownBy(() -> sut.create(orderBuilder.build()))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(menuDao).countByIdIn(anyList());
        verifyNoInteractions(orderTableDao);
        verifyNoInteractions(orderDao);
        verifyNoInteractions(orderLineItemDao);
    }


    @Test
    @DisplayName("주문 테이블을 찾지 못하면 예외 던짐")
    void shouldThrowExceptionWhenNotFoundOrderTable() {
        // given
        final OrderLineItemBuilder orderLineItemBuilder = OrderLineItemBuilder.orderLineItem()
                .withMenuId(10);
        final List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItemBuilder.build());
        final OrderBuilder orderBuilder = OrderBuilder.order()
                .withOrderTableId(100)
                .withOrderStatus(COOKING.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(
                        orderLineItems
                );

        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(orderLineItems.size()));
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> sut.create(orderBuilder.build()))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(menuDao).countByIdIn(anyList());
        verify(orderTableDao).findById(anyLong());
        verifyNoInteractions(orderDao);
        verifyNoInteractions(orderLineItemDao);
    }

    @Test
    @DisplayName("주문 목록 조회")
    void getOrders() {
        // given
        final List<OrderLineItem> orderLineItems = Collections.singletonList(
                OrderLineItemBuilder.orderLineItem()
                        .withMenuId(10).build());
        final Order order =  OrderBuilder.order()
                .withId(1)
                .withOrderTableId(100)
                .withOrderStatus(COOKING.name())
                .withOrderedTime(LocalDateTime.now())
                .withOrderLineItems(
                        orderLineItems
                ).build();

        given(orderDao.findAll())
                .willReturn(Collections.singletonList(order));
        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(orderLineItems);

        // when
        sut.list();

        // then
        verify(orderDao).findAll();
        verify(orderLineItemDao).findAllByOrderId(anyLong());
        verifyNoInteractions(menuDao);
        verifyNoInteractions(orderTableDao);
    }

    @ParameterizedTest
    @DisplayName("주문이 완료상태가 아닌 경우에만 주문 상태를 수정할 수 있음")
    @MethodSource("provideOrderStatus")
    void changeOrderStatusWhenFoundOrderStatusIsNotCompletion(String prevOrderStatus, String orderStatus) {
        // given
        final List<OrderLineItem> orderLineItems = Collections.singletonList(
                OrderLineItemBuilder.orderLineItem()
                        .withMenuId(10)
                        .build()
        );
        final Order changingOrder = OrderBuilder.order()
                .withOrderStatus(orderStatus)
                .build();
        final Order foundOrder = OrderBuilder.order()
                .withOrderStatus(prevOrderStatus)
                .build();

        given(orderDao.findById(anyLong()))
                .willReturn(Optional.of(foundOrder));
        given(orderDao.save(any(Order.class)))
                .willReturn(OrderBuilder.order().build());
        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(orderLineItems);

        // when
        Order savedOrder = sut.changeOrderStatus(1L, changingOrder);

        // then
        for(int i = 0; i < orderLineItems.size(); i ++) {
            assertThat(savedOrder.getOrderLineItems().get(i))
                    .isEqualToComparingFieldByField(orderLineItems.get(i));
        }

        // and
        verify(orderDao).findById(anyLong());
        verify(orderDao).save(any(Order.class));
        verify(orderLineItemDao).findAllByOrderId(anyLong());
        verifyNoInteractions(menuDao);
        verifyNoInteractions(orderTableDao);
    }

    private static Stream provideOrderStatus() {
        return Stream.of(
                Arguments.of(COOKING.name(), MEAL.name()),
                Arguments.of(COOKING.name(), COMPLETION.name()),
                Arguments.of(MEAL.name(), COOKING.name()),
                Arguments.of(COOKING.name(), COMPLETION.name())
        );
    }

    @Test
    @DisplayName("주문을 찾지 못하면 예외 던짐")
    void shouldThrowExceptionWhenNotFoundOrderByOrderId() {
        // given
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> sut.changeOrderStatus(1L, OrderBuilder.order().build()))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(orderDao).findById(anyLong());
        verifyNoInteractions(orderLineItemDao);
    }


    @Test
    @DisplayName("찾은 주문이 완료 상태이면 예외 던짐")
    void shouldThrowExceptionWithFoundOrderStatusIsCompletion() {
        // given
        final Order order =  OrderBuilder.order()
                .withOrderStatus(COMPLETION.name())
                .build();
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.of(order));

        // when
        assertThatThrownBy(() -> sut.changeOrderStatus(1L, OrderBuilder.order().build()))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(orderDao).findById(anyLong());
        verifyNoInteractions(orderLineItemDao);
    }
}
