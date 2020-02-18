package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderBo orderBo;

    private OrderLineItem orderLineItem;
    private Order order;
    private List<OrderLineItem> orderLineList;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setOrderId(1L);
        orderLineItem.setQuantity(1);

        orderLineList = new ArrayList<>();
        orderLineList.add(orderLineItem);

        order = new Order();
        order.setId(1L);
        order.setOrderLineItems(orderLineList);
        order.setOrderStatus(OrderStatus.COOKING.name());

        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setTableGroupId(1L);
        orderTable.setNumberOfGuests(3);
        orderTable.setEmpty(false);
    }

    @DisplayName("주문 리스트를 검색할 수 있다.")
    @Test
    void list() {
        // given
        given(orderDao.findAll())
                .willReturn(Collections.singletonList(order));

        // when
        List<Order> actualOrder = orderBo.list();

        // then
        assertThat(actualOrder.get(0).getId()).isEqualTo(order.getId());
    }

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create() {
        // given
        given(menuDao.countByIdIn(anyList()))
                .willReturn((long)orderLineList.size());

        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(orderDao.save(any(Order.class))).willReturn(order);

        // when
        final Order actual = orderBo.create(order);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(order.getId());
    }

    @DisplayName("주문이 생성시 주문 상태는 조리중으로 바뀐다.")
    @Test
    void orderStatusCooking() {
        // given
        given(menuDao.countByIdIn(anyList()))
                .willReturn((long)orderLineList.size());

        given(orderTableDao.findById(any())).willReturn(Optional.of(orderTable));
        given(orderDao.save(any(Order.class))).willReturn(order);

        // when
        final Order actual = orderBo.create(order);

        // then
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }

    @DisplayName("주문에 메뉴가 포함 되어야 한다.")
    @Test
    void menuExist() {
        // given
        Order expected = new Order();
        expected.setId(1L);
        expected.setOrderStatus(OrderStatus.COOKING.name());

        // when
        orderLineList = Collections.emptyList();
        expected.setOrderLineItems(orderLineList);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(expected));
    }

    @DisplayName("주문에 포함된 메뉴는 사용중인 메뉴에서 가져온 것이어야한다.")
    @Test
    void notExistedMenu() {
        // given
        List<OrderLineItem> existedList = new ArrayList<>();

        OrderLineItem sampleItem1 = new OrderLineItem();
        orderLineItem.setMenuId(2L);
        orderLineItem.setOrderId(2L);
        orderLineItem.setQuantity(2);
        existedList.add(sampleItem1);

        OrderLineItem sampleItem2 = new OrderLineItem();
        orderLineItem.setMenuId(3L);
        orderLineItem.setOrderId(3L);
        orderLineItem.setQuantity(3);
        existedList.add(sampleItem2);

        Order expected = new Order();
        expected.setId(1L);
        expected.setOrderStatus(OrderStatus.COOKING.name());
        expected.setOrderLineItems(existedList);

        // when
        when(menuDao.countByIdIn(anyList())).thenReturn((long) 1);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(expected));
    }

    @DisplayName("주문에 테이블이 존재해야 한다.")
    @Test
    void tableExist() {
        // given
        orderLineList = Collections.emptyList();
        order.setOrderLineItems(orderLineList);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문의 상태는 조리, 식사, 완료이다.")
    @Test
    void checkOrderStatus() {
        assertThat(OrderStatus.values().length).isEqualTo(3);
        assertThat(OrderStatus.COOKING.name()).isEqualTo("COOKING");
        assertThat(OrderStatus.MEAL.name()).isEqualTo("MEAL");
        assertThat(OrderStatus.COMPLETION.name()).isEqualTo("COMPLETION");
    }

    @DisplayName("주문 상태를 변경할 수 있다.")
    @Test
    void changeOrderStatus() {
        // given
        given(orderDao.findById(anyLong())).willReturn(Optional.of(order));
        order.setOrderStatus(OrderStatus.MEAL.name());

        // when
        Order actual = orderBo.changeOrderStatus(order.getId(), order);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(actual.getOrderStatus());
    }

    @DisplayName("주문 상태가 완료이면 변경할 수 없다.")
    @Test
    void orderStatusNotChange() {
        // given
        given(orderDao.findById(anyLong())).willReturn(Optional.of(order));
        order.setOrderStatus(OrderStatus.COMPLETION.name());

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), order));
    }
}
