package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
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

    private Order expectedOrder = null;
    private List<OrderLineItem> expectedOrderLineItems = new ArrayList<>();

    private OrderTable orderTable = null;

    @BeforeEach
    void setUp() {
        expectedOrder = new Order();
        expectedOrder.setOrderStatus(OrderStatus.COOKING.name());
        OrderLineItem orderLineItem = new OrderLineItem();
        expectedOrderLineItems.add(orderLineItem);
        expectedOrder.setOrderLineItems(expectedOrderLineItems);
        orderTable = new OrderTable();
        orderTable.setId(1L);
        expectedOrder.setOrderTableId(orderTable.getId());

    }

    @DisplayName("주문을 받는다.")
    @Test
    void createOrder() {
        given(menuDao.countByIdIn(anyList())).willReturn(Long.valueOf(expectedOrderLineItems.size()));
        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(orderTable));
        given(orderDao.save(any(Order.class))).willReturn(expectedOrder);

        Order actualOrder = orderBo.create(expectedOrder);

        assertThat(actualOrder).isEqualTo(expectedOrder);

    }

    @DisplayName("주문 아이템이 없을 경우 주문을 받을수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForNoneOrderLineItems() {
        expectedOrder.setOrderLineItems(new ArrayList<>());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(expectedOrder));
    }

    @DisplayName("주문 아이템의 수와 메뉴의 수가 다를 경우 주문을 받을 수 없다..")
    @Test
    void shouldThrowIllegalArgumentExceptionForDifferentSizeOfOrderLineItems() {
        given(menuDao.countByIdIn(anyList())).willReturn(Long.valueOf(3));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(expectedOrder));
    }

    @DisplayName("존재하지 않는 주문테이블에 주문을 받을수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForNoneOrderTable() {
        given(menuDao.countByIdIn(anyList())).willReturn(Long.valueOf(expectedOrderLineItems.size()));
        given(orderTableDao.findById(anyLong())).willReturn(Optional.empty());
    
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(expectedOrder));
    }

    @DisplayName("주문테이블이 비어있다면 주문을 받을 수 없다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForEmptyOrderTable() {
        given(menuDao.countByIdIn(anyList())).willReturn(Long.valueOf(expectedOrderLineItems.size()));
        orderTable.setEmpty(true);
        given(orderTableDao.findById(anyLong())).willReturn(Optional.of(orderTable));

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(expectedOrder));

    }

    @DisplayName("주문 목록을 조회한다.")
    @Test
    void getOrders() {
        //given
        given(orderDao.findAll()).willReturn(Arrays.asList(expectedOrder));

        //when
        List<Order> actual = orderBo.list();

        //then
        assertThat(actual).isEqualTo(orderDao.findAll());
    }

    @DisplayName("주문의 상태를 변경한다.")
    @Test
    void changeOrderStatus() {
        given(orderDao.findById(anyLong())).willReturn(Optional.of(expectedOrder));
        expectedOrder.setOrderStatus(OrderStatus.COOKING.name());

        Order actualOrder = orderBo.changeOrderStatus(expectedOrder.getOrderTableId(), expectedOrder);

        assertThat(actualOrder.getOrderStatus()).isEqualTo(expectedOrder.getOrderStatus());
    }

    @DisplayName("없는 주문의 상태를 변경하지 못한다.")
    @Test
    void shouldThrowIllegalArgumentExceptionForNoneOrder() {
        given(orderDao.findById(anyLong())).willReturn(Optional.empty());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.changeOrderStatus(expectedOrder.getOrderTableId(), expectedOrder));

    }

    @DisplayName("주문의 상태가 completion 인경우 주문상태를 변경하지 못한다.")
    @Test
    void shouldThrowIllegalArgumentExceptionWhenOrderStatusUnderCompletion() {

        given(orderDao.findById(anyLong())).willReturn(Optional.of(expectedOrder));
        expectedOrder.setOrderStatus(OrderStatus.COMPLETION.name());

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.changeOrderStatus(expectedOrder.getOrderTableId(), expectedOrder));
    }
}
