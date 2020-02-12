package kitchenpos.bo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderBoTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @InjectMocks
    private OrderBo orderBo;

    private Order order;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setOrderId(1L);
        orderLineItem.setQuantity(1L);

        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(false);

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        order = new Order();
        order.setId(1L);
        order.setOrderTableId(1L);
        order.setOrderLineItems(orderLineItems);

        when(orderTableDao.findById(1L)).thenReturn(Optional.of(orderTable));
        when(menuDao.countByIdIn(anyList())).thenReturn(Long.valueOf(orderLineItems.size()));
        when(orderTableDao.findById(order.getOrderTableId())).thenReturn(Optional.of(orderTable));
        when(orderDao.save(order)).thenReturn(order);
        when(orderLineItemDao.save(orderLineItem)).thenReturn(orderLineItem);
    }

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create() {
        assertThat(orderBo.create(order)).isEqualTo(order);
    }

    @DisplayName("주문의 주문메뉴가 비어있는 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderMenu() {
        order.setOrderLineItems(new ArrayList<>());

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("존재하지 않는 주문메뉴가 포함 된 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithMenu() {
        when(menuDao.countByIdIn(anyList())).thenReturn(0L);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문이 속한 테이블이 존재하지 않는 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderTable() {
        when(orderTableDao.findById(anyLong())).thenThrow(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문이 속한 테이블이 `이용가능` 상태인 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderTableStatus() {
        orderTable.setEmpty(true);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문은 최초 생성 후 `조리중` 상태가 된다.")
    @Test
    void createdOrderStatus() {
        Order createdOrder = orderBo.create(order);

        assertThat(OrderStatus.valueOf(createdOrder.getOrderStatus()))
            .isEqualTo(OrderStatus.COOKING);
    }

    @DisplayName("주문의 상태를 수정할 수 있다. (`조리중`, `식사중`, `완료`)")
    @Test
    void changeOrderStatus() {
        when(orderDao.findById(anyLong())).thenReturn(Optional.of(order));
        order.setOrderStatus("MEAL");

        assertThat(orderBo.changeOrderStatus(1L, order)).isEqualTo(order);
    }

    @DisplayName("상태를 변경하려는 주문이 존재하지 않는 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrder() {
        when(orderDao.findById(anyLong())).thenThrow(IllegalArgumentException.class);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.changeOrderStatus(1L, order));
    }

    @DisplayName("상태를 변경하려는 주문이 이미 `완료` 상태인 경우, 예외를 발생시킨다.")
    @Test
    void exceptionWithOrderStatus() {
        order.setOrderStatus("COMPLETION");

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.changeOrderStatus(1L, order));
    }

    @DisplayName("주문 목록을 조회할 수 있다.")
    @Test
    void list() {
        when(orderDao.findAll()).thenReturn(new ArrayList<>());
        assertThat(orderBo.list()).isEmpty();
    }
}
