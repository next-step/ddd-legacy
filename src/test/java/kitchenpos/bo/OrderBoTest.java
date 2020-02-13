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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {

    @InjectMocks
    private OrderBo orderBo;

    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @Mock
    private OrderTableDao orderTableDao;

    @DisplayName("새로운 주문 등록")
    @Test
    void create() {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setOrderLineItems(orderLineItems)
                .setOrderTableId(1L)
                .build()
                ;

        Order savedOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus(OrderStatus.COOKING.name())
                .setOrderLineItems(orderLineItems)
                .setOrderTableId(1L)
                .build()
                ;

        OrderTable orderTable = new OrderTableBuilder()
                .setEmpty(false)
                .setNumberOfGuests(4)
                .setId(1L)
                .build()
                ;

        List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList())
                ;

        given(menuDao.countByIdIn(menuIds)).willReturn(Long.valueOf(orderLineItems.size()));
        given(orderTableDao.findById(requestOrder.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(requestOrder)).willReturn(savedOrder);

        Order order = orderBo.create(requestOrder);

        assertThat(order.getId()).isEqualTo(savedOrder.getId());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }

    @DisplayName("상세 주문 내역이 존재 하지 않을시 에러")
    @Test
    void createFailByNotExistOrderLineItem() {
        Order requestOrder = new OrderBuilder()
                .setOrderTableId(1L)
                .build()
                ;

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("등록되어 있지 않은 메뉴가 주문 내역에 포함 되어 있을 시 에러")
    @Test
    void createFailByNotInsertedOrderLineItem() {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setOrderLineItems(orderLineItems)
                .setOrderTableId(1L)
                .build()
                ;

        List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList())
                ;

        given(menuDao.countByIdIn(menuIds)).willReturn(Long.valueOf(orderLineItems.size() - 1));

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("존재 하지 않는 테이블이거나 테이블 정보가 없을때 일때 에러")
    @Test
    void createFailByNotExistTable() {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setOrderLineItems(orderLineItems)
                .setOrderTableId(1L)
                .build()
                ;

        List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());

        given(menuDao.countByIdIn(menuIds)).willReturn(Long.valueOf(orderLineItems.size()));
        given(orderTableDao.findById(requestOrder.getOrderTableId())).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("주문받은 음식의 상태 변경")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void changeOrderStatus(String value) {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus(value)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        Order savedOrder = new OrderBuilder()
                .setId(1L)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        given(orderDao.findById(requestOrder.getId())).willReturn(Optional.of(savedOrder));
        given(orderLineItemDao.findAllByOrderId(requestOrder.getId())).willReturn(orderLineItems);

        Order changedOrder = orderBo.changeOrderStatus(requestOrder.getId(), requestOrder);

        assertThat(changedOrder.getId()).isEqualTo(savedOrder.getId());
        assertThat(changedOrder.getOrderStatus()).isEqualTo(requestOrder.getOrderStatus());
    }

    @DisplayName("COMPLETE 상태의 주문 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void changeOrderStatusFailByComplete(String value) {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus(value)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        Order savedOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus("COMPLETION")
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        given(orderDao.findById(requestOrder.getId())).willReturn(Optional.of(savedOrder));

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }

    @DisplayName("등록되지 않은 주문은 상태를 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"MEAL"})
    void changeOrderStatusFailByNotInsert(String value) {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus(value)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        given(orderDao.findById(requestOrder.getId())).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }
}
