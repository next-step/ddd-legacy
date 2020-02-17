package kitchenpos.bo;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

    @DisplayName("주문에 주문목록이 없는경우 IllegalArgumenetException이 발생한다.")
    @Test
    void createWithoutOrderLineItems (){
        Order order = new Order.Builder()
            .id(1L)
            .orderLineItems(null)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("메뉴목록에 중복이 있는경우 IllegalArgumentException이 발생한다.")
    @Test
    void createWithDuplicatedMenu (){
        Order order = new Order.Builder()
            .id(1L)
            .orderLineItems(new ArrayList<>())
            .build();

        OrderLineItem orderLineItem1 = new OrderLineItem.Builder()
            .seq(1L)
            .menuId(1L)
            .build();
        order.addOrderLineItem(orderLineItem1);

        OrderLineItem orderLineItem2 = new OrderLineItem.Builder()
            .seq(2L)
            .menuId(1L)
            .build();
        order.addOrderLineItem(orderLineItem2);

        List<Long> menuIds = new ArrayList<>();
        menuIds.add(1L);
        menuIds.add(1L);

        given(menuDao.countByIdIn(menuIds)).willReturn(1L);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));

    }

    @DisplayName("주문에 입력한 주문테이블이 null 이라, IllegalArgumentException이 발생한다.")
    @Test
    void createWithoutOrderTable(){
        Order order = new Order.Builder()
            .id(1L)
            .orderTableId(2L)
            .orderLineItems(new ArrayList<>())
            .build();

        OrderLineItem orderLineItem1 = new OrderLineItem.Builder()
            .seq(1L)
            .menuId(1L)
            .build();
        order.addOrderLineItem(orderLineItem1);

        OrderLineItem orderLineItem2 = new OrderLineItem.Builder()
            .seq(2L)
            .menuId(1L)
            .build();
        order.addOrderLineItem(orderLineItem2);

        List<Long> menuIds = new ArrayList<>();
        menuIds.add(1L);
        menuIds.add(1L);

        given(menuDao.countByIdIn(menuIds)).willReturn(2L);
        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.ofNullable(null));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문에 입력한 주문테이블이 이미 다른 손이 앉아있다면, IllegalArgumentException이 발생한다.")
    @Test
    void createWithOrderTableIsZero(){
        Order order = new Order.Builder()
            .id(1L)
            .orderTableId(2L)
            .orderLineItems(new ArrayList<>())
            .build();

        OrderLineItem orderLineItem1 = new OrderLineItem.Builder()
            .seq(1L)
            .menuId(1L)
            .build();
        order.addOrderLineItem(orderLineItem1);

        OrderLineItem orderLineItem2 = new OrderLineItem.Builder()
            .seq(2L)
            .menuId(1L)
            .build();
        order.addOrderLineItem(orderLineItem2);

        List<Long> menuIds = new ArrayList<>();
        menuIds.add(1L);
        menuIds.add(1L);

        OrderTable orderTable = new OrderTable.Builder()
            .id(1L)
            .empty(true)
            .build();

        given(menuDao.countByIdIn(menuIds)).willReturn(2L);
        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.ofNullable(orderTable));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문번호를 잘못 입력했을 때, IllegalArgumentException 이 발생한다.")
    @Test
    void changeOrderStatusWithWrongOrderId (){
        Order order = new Order.Builder()
            .id(5L)
            .build();

        given(orderDao.findById(order.getId())).willReturn(Optional.ofNullable(null));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), order));
    }

    @DisplayName("주문테이블 상태가 COMPLETION 상태라면, IllegalArgumentException 이 발생한다.")
    @Test
    void changeOrderStatusEmptyTable(){
        Order savedOrder = new Order.Builder()
            .id(1L)
            .orderTableId(1L)
            .orderStatus(OrderStatus.COMPLETION.name())
            .build();

        Order newOrder = new Order.Builder()
            .id(1L)
            .orderTableId(1L)
            .orderStatus(OrderStatus.COOKING.name())
            .build();

        given(orderDao.findById(newOrder.getId())).willReturn(Optional.ofNullable(savedOrder));

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.changeOrderStatus(newOrder.getId(), newOrder));
    }


}
