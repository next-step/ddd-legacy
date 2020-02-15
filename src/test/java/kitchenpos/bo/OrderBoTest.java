package kitchenpos.bo;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @DisplayName("메뉴가 없는 주문을 할수 없다.")
    @Test
    void orderHasMenus() {
        //given
        Order order = createOrder();
        order.setOrderLineItems(new ArrayList<>());

        //when, then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("존재 하지 않는 메뉴로 주문을 할수 없다.")
    @Test
    void notExistMenu() {
        //given
        Order order = createOrder();
        given(menuDao.countByIdIn(anyList())).willReturn(0L);

        //when, then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("존재 하지 않는 테이블에는 주문을 할수 없다.")
    @Test
    void notExistTable() {
        //given
        Order order = createOrder();

        given(menuDao.countByIdIn(anyList()))
            .willReturn(Long.valueOf(order.getOrderLineItems().size()));
        given(orderTableDao.findById(order.getOrderTableId()))
            .willReturn(Optional.ofNullable(null));

        //when, then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문 즉시에는 상태는 조리중이다.")
    @Test
    void createOrderStatusIsCOOKING() {
        //given
        Order order = createOrder();

        OrderTable orderTable = new OrderTable();
        orderTable.setId(order.getOrderTableId());

        given(menuDao.countByIdIn(anyList()))
            .willReturn(Long.valueOf(order.getOrderLineItems().size()));
        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(order);

        for (OrderLineItem item : order.getOrderLineItems()) {
            given(orderLineItemDao.save(item)).willReturn(item);
        }

        //when, then
        Assertions.assertThat(orderBo.create(order).getOrderStatus())
            .isEqualTo(OrderStatus.COOKING.name());
    }

    @DisplayName("주문 현재 상태를 변경할수 있다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class)
    void changeStatus(OrderStatus orderStatus) {
        //given
        Order currentOrder = createOrder();
        currentOrder.setOrderStatus(OrderStatus.MEAL.name());

        Order requestOrder = new Order();
        requestOrder.setOrderStatus(orderStatus.name());

        given(orderDao.findById(currentOrder.getId())).willReturn(Optional.of(currentOrder));

        //when, then
        Assertions
            .assertThat(orderBo.changeOrderStatus(currentOrder.getId(), requestOrder).getOrderStatus())
            .isEqualTo(orderStatus.name());
    }

    @DisplayName("완료된 주문은 상태를 변경할수 없다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class)
    void noneChangeStatus(OrderStatus orderStatus) {
        //given
        Order dbOrder = createOrder();
        dbOrder.setOrderStatus(OrderStatus.COMPLETION.name());

        Order requestOrder = new Order();
        requestOrder.setOrderStatus(orderStatus.name());

        given(orderDao.findById(dbOrder.getId())).willReturn(Optional.of(dbOrder));

        //when, then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.changeOrderStatus(dbOrder.getId(), requestOrder));
    }

    @DisplayName("전체 주문을 조회할수 있다.")
    @Test
    void list() {
        //given
        Order dbOrder = createOrder();
        List<Order> orders = Arrays.asList(dbOrder);

        given(orderDao.findAll()).willReturn(orders);
        given(orderLineItemDao.findAllByOrderId(dbOrder.getId()))
            .willReturn(dbOrder.getOrderLineItems());

        //when, then
        Assertions.assertThat(orderBo.list()).containsAll(orders);
    }

    private Order createOrder() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(1L);

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem);

        Order order = new Order();
        order.setOrderLineItems(orderLineItems);
        order.setOrderTableId(1L);
        return order;
    }
}
