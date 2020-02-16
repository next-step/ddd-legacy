package kitchenpos.bo;

import kitchenpos.builder.OrderBuilder;
import kitchenpos.builder.OrderLineItemBuilder;
import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

public class OrderBoTestWithBuilder extends MockTest {
    @Mock
    private MenuDao menuDao;
    @Mock private OrderDao orderDao;
    @Mock private OrderLineItemDao orderLineItemDao;
    @Mock private OrderTableDao orderTableDao;

    @InjectMocks
    private OrderBo orderBo;

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create() {
        //given
        OrderLineItem givenOrderLineItem = OrderLineItemBuilder.orderLineItem()
                .build();
        List<OrderLineItem> givenOrderLineItemList = Arrays.asList(givenOrderLineItem);
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .build();
        Order givenOrder = OrderBuilder.order()
                .withOrderLineItems(givenOrderLineItemList)
                .withOrderTableId(1L)
                .build();
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItemList.size()));
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(givenOrderTable));
        given(orderDao.save(any(Order.class)))
                .willAnswer(invocation -> {
                    givenOrder.setId(1L);
                    return givenOrder;
                });
        given(orderLineItemDao.save(any(OrderLineItem.class)))
                .willReturn(givenOrderLineItem);

        //when
        Order actualOrder = orderBo.create(givenOrder);

        //then
        assertThat(actualOrder.getOrderStatus())
                .isEqualTo(String.valueOf(OrderStatus.COOKING));
    }

    @DisplayName("주문메뉴가 반드시 있어야 한다.")
    @ParameterizedTest
    @NullSource
    void createMustHaveOrderItem(List<OrderLineItem> orderLineItems) {
        //given
        List<OrderLineItem> givenOrderLineItems = orderLineItems;
        Order givenOrder = OrderBuilder.order()
                .withOrderLineItems(givenOrderLineItems)
                .build();

        //when
        //then
        assertThatThrownBy(() ->{ orderBo.create(givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장에서 판매하지 않는 메뉴를 주문할 수 없다.")
    @Test
    void createMustHaveItemsServed() {
        //given
        OrderLineItem givenOrderLineItem = OrderLineItemBuilder.orderLineItem()
                .build();
        List<OrderLineItem> givenOrderLineItemList = Arrays.asList(givenOrderLineItem);
        Order givenOrder = OrderBuilder.order()
                .withOrderLineItems(givenOrderLineItemList)
                .withId(1L)
                .build();
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItemList.size() + 1));

        //when
        //then
        assertThatThrownBy(() ->{ orderBo.create(givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문할 때 테이블을 반드시 지정해야 한다.")
    @Test
    void createMustHaveDesignatedTable() {
        //given
        OrderLineItem givenOrderLineItem = OrderLineItemBuilder.orderLineItem()
                .build();
        List<OrderLineItem> givenOrderLineItems = Arrays.asList(givenOrderLineItem);
        Order givenOrder = OrderBuilder.order()
                .withOrderTableId(1L)
                .withOrderLineItems(givenOrderLineItems)
                .build();
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItems.size()));
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() ->{ orderBo.create(givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 들어오면 주문상태가 조리중(COOKING)으로 된다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = INCLUDE, names = {"COOKING"})
    void createThenChangeStatusToCooking(@NotNull OrderStatus orderStatus) {
        //given
        OrderLineItem givenOrderLineItem = OrderLineItemBuilder.orderLineItem()
                .build();
        List<OrderLineItem> givenOrderLineItemList = Arrays.asList(givenOrderLineItem);
        OrderTable givenOrderTable = OrderTableBuilder.orderTable()
                .build();
        Order givenOrder = OrderBuilder.order()
                .withOrderTableId(1L)
                .withOrderLineItems(givenOrderLineItemList)
                .build();
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItemList.size()));
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(givenOrderTable));
        given(orderDao.save(any(Order.class)))
                .willReturn(givenOrder);

        //when
        Order actualOrder = orderBo.create(givenOrder);

        //then
        assertThat(actualOrder.getOrderStatus())
                .isEqualTo(orderStatus.name());
    }

    @DisplayName("주문 목록을 볼 수 있다.")
    @Test
    void list() {
        //given
        Order givenOrder = OrderBuilder.order()
                .withId(1L)
                .build();
        List<Order> givenOrderList = Arrays.asList(givenOrder);
        OrderLineItem givenOrderLineItem = OrderLineItemBuilder.orderLineItem()
                .build();
        List<OrderLineItem> givenOrderLineItems = Arrays.asList(givenOrderLineItem);
        given(orderDao.findAll())
                .willReturn(givenOrderList);
        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(givenOrderLineItems);

        //when
        List<Order> actualOrderList = orderBo.list();

        //then
        assertThat(actualOrderList.size())
                .isEqualTo(givenOrderList.size());
    }

    @DisplayName("주문의 상태를 변경할 수 있다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = INCLUDE, names = {"COMPLETION"})
    void changeOrderStatus(@NotNull OrderStatus orderStatus) {
        //given
        Order givenOrder = OrderBuilder.order()
                .withOrderStatus(OrderStatus.MEAL.name())
                .withId(1L)
                .build();
        OrderLineItem givenOrderLineItem = OrderLineItemBuilder.orderLineItem()
                .build();
        List<OrderLineItem> givenOrderLineItems = Arrays.asList(givenOrderLineItem);
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.of(givenOrder));
        given(orderDao.save(any(Order.class)))
                .willReturn(givenOrder);
        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(givenOrderLineItems);

        //when
        Order actualOrder = orderBo.changeOrderStatus(givenOrder.getId(), givenOrder);

        //then
        assertThat(actualOrder.getOrderStatus())
                .isNotEqualTo(orderStatus.name());
    }

    @DisplayName("이미 생성된 주문의 상태만 변경할 수 있다.")
    @Test
    void changeOrderStatusOrderedOne() {
        //given
        Order givenOrder = OrderBuilder.order()
                .withOrderStatus(OrderStatus.MEAL.name())
                .withId(1L)
                .build();
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() ->{
            orderBo.changeOrderStatus(givenOrder.getId(), givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("식사가 끝나고 결재도 끝난 주문은 상태를 변경할 수 없다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = INCLUDE, names = {"COMPLETION"})
    void changeOrderStatusOfCompletedOrders(@NotNull OrderStatus orderStatus) {
        //given
        Order givenOrder = OrderBuilder.order()
                .withOrderStatus(orderStatus.name())
                .withId(1L)
                .build();

        //when
        //then
        assertThatThrownBy(() ->{
            orderBo.changeOrderStatus(givenOrder.getId(), givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }
}
