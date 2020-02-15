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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

/**
 * @author Geonguk Han
 * @since 2020-02-15
 */
@ExtendWith(MockitoExtension.class)
class OrderBoTest extends Fixtures {

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

    @Test
    @DisplayName("주문의 아이템이 없는 경우")
    void create_empty_orderLineItem() {
        final Order order = new Order();
        order.setOrderLineItems(Collections.emptyList());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문의 아이템과 메뉴의 숫자가 일치하지 않는 경우")
    void create_notEqual_menuItem_size() {
        final Order order = new Order();
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1l);

        final OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setMenuId(2l);
        order.setOrderLineItems(Arrays.asList(orderLineItem, orderLineItem1));

        given(menuDao.countByIdIn(Arrays.asList(orderLineItem.getMenuId(),
                orderLineItem1.getMenuId()))).willReturn(3l);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문 테이블이 존재하지 않는 경우")
    void create_notExist_orderTable() {
        final Order order = new Order();
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1l);

        final OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setMenuId(2l);
        order.setOrderTableId(1l);
        order.setOrderLineItems(Arrays.asList(orderLineItem, orderLineItem1));

        given(menuDao.countByIdIn(Arrays.asList(orderLineItem.getMenuId(),
                orderLineItem1.getMenuId()))).willReturn(2l);
        given(orderTableDao.findById(order.getOrderTableId())).willThrow(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문 테이블이 빈 테이블인 경우")
    void create_empty_orderTable() {
        final Order order = new Order();
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1l);

        final OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setMenuId(2l);
        order.setOrderTableId(1l);
        order.setOrderLineItems(Arrays.asList(orderLineItem, orderLineItem1));

        final OrderTable orderTable = Fixtures.orderTable;
        orderTable.setEmpty(true);

        given(menuDao.countByIdIn(Arrays.asList(orderLineItem.getMenuId(),
                orderLineItem1.getMenuId()))).willReturn(2l);
        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문 테이블이 정상 등록")
    void create_success() {
        final Order order = new Order();
        order.setOrderTableId(1l);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1l);

        final OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setMenuId(2l);

        order.setOrderLineItems(Arrays.asList(orderLineItem, orderLineItem1));

        final OrderTable orderTable = Fixtures.orderTable;
        orderTable.setEmpty(false);

        given(menuDao.countByIdIn(Arrays.asList(orderLineItem.getMenuId(),
                orderLineItem1.getMenuId()))).willReturn(2l);
        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(order);
        given(orderLineItemDao.save(orderLineItem)).willReturn(orderLineItem);
        given(orderLineItemDao.save(orderLineItem1)).willReturn(orderLineItem1);

        final Order result = orderBo.create(order);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("주문 목록을 조회 할 수 있다.")
    void list() {
        final Order order = new Order();
        order.setId(1l);

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setOrderId(1l);

        final OrderLineItem orderLineItem1 = new OrderLineItem();
        orderLineItem1.setOrderId(1l);

        final List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem, orderLineItem1);
        order.setOrderLineItems(orderLineItems);

        given(orderDao.findAll()).willReturn(Arrays.asList(order));

        given(orderLineItemDao.findAllByOrderId(order.getId())).willReturn(orderLineItems);

        final List<Order> list = orderBo.list();

        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getOrderLineItems().size()).isEqualTo(orderLineItems.size());
    }

    @Test
    @DisplayName("주문이 존재 하지 않는 경우")
    void changeOrderStatus_notExist_order() {
        final Order order = new Order();
        order.setId(1l);

        given(orderDao.findById(order.getId())).willThrow(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), order));
    }

    @Test
    @DisplayName("주문이 완료된 경우")
    void changeOrderStatus_completed_order() {
        final Order order = new Order();
        order.setId(1l);
        order.setOrderStatus(OrderStatus.COMPLETION.name());

        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), order));
    }

    @Test
    @DisplayName("주문 상태를 변경 하는 경우")
    void changeOrderStatus_success() {
        final Order order = new Order();
        order.setId(1l);
        order.setOrderStatus(OrderStatus.COOKING.name());

        final Order updatedOrder = new Order();
        updatedOrder.setId(1l);
        updatedOrder.setOrderStatus(OrderStatus.MEAL.name());

        given(orderDao.findById(order.getId())).willReturn(Optional.of(order));
        given(orderDao.save(order)).willReturn(updatedOrder);
        given(orderLineItemDao.findAllByOrderId(order.getId())).willReturn(Arrays.asList(new OrderLineItem()));

        final Order result = orderBo.changeOrderStatus(order.getId(), updatedOrder);

        assertThat(result).isNotNull();
        assertThat(result.getOrderStatus()).isEqualTo(updatedOrder.getOrderStatus());
    }
}