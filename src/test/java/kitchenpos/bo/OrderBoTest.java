package kitchenpos.bo;

import kitchenpos.Fixtures;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
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

    private Order defaultOrder;
    private List<OrderLineItem> defaultOrderLineItems;
    private List<Long> defaultMenuIdsOfOrderLineItems;
    private OrderTable defaultOrderTable;

    @BeforeEach
    public void setUP() {
        defaultOrder = Fixtures.getOrder(1L, LocalDateTime.now(), getDefaultOrderLineItem(), OrderStatus.MEAL.name(), 1L);
        defaultOrderLineItems = getDefaultOrderLineItem();
        defaultMenuIdsOfOrderLineItems = defaultOrderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());
        defaultOrderTable = Fixtures.getOrderTable(1L, false, 4);
    }

    @DisplayName("정상적인 값으로 주문이 생성된다.")
    @Test
    public void createNormal() {
        given(menuDao.countByIdIn(defaultMenuIdsOfOrderLineItems)).willReturn(Long.valueOf(defaultOrderLineItems.size()));
        given(orderTableDao.findById(defaultOrder.getOrderTableId())).willReturn(Optional.ofNullable(defaultOrderTable));
        given(orderDao.save(defaultOrder)).willReturn(defaultOrder);

        assertThat(orderBo.create(defaultOrder)).isEqualTo(defaultOrder);
    }

    @DisplayName("주문된 메뉴가 1개 이상 있어야 한다.")
    @Test
    public void createNoEmptyOrder() {
        Order order = Fixtures.getOrder(1L, LocalDateTime.now(), new ArrayList<>(), OrderStatus.MEAL.name(), 1L);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문된 메뉴는 반드시 팔고 있는 메뉴여야 한다.")
    @Test
    public void createWithMenus() {
        given(menuDao.countByIdIn(defaultMenuIdsOfOrderLineItems)).willReturn(1L);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(defaultOrder));
    }

    @DisplayName("주문된 테이블이 식당에 존재하는 테이블이어야 한다.")
    @Test
    public void createWithTable() {
        given(menuDao.countByIdIn(defaultMenuIdsOfOrderLineItems))
                .willReturn(Long.valueOf(defaultMenuIdsOfOrderLineItems.size()));
        given(orderTableDao.findById(defaultOrder.getOrderTableId())).willReturn(Optional.empty());
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(defaultOrder));
    }

    @DisplayName("주문된 테이블은 손님이 반드시 있어야 한다.")
    @Test
    public void createNoEmpty() {
        given(menuDao.countByIdIn(defaultMenuIdsOfOrderLineItems))
                .willReturn(Long.valueOf(defaultMenuIdsOfOrderLineItems.size()));
        defaultOrderTable.setEmpty(true);
        given(orderTableDao.findById(defaultOrder.getOrderTableId())).willReturn(Optional.ofNullable(defaultOrderTable));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(defaultOrder));
    }

    @DisplayName("주문 상태가 완료로 변경된 것은 상태를 변경할 수 없다.")
    @Test
    public void changeOrderStatus() {
        defaultOrder.setOrderStatus(OrderStatus.COMPLETION.name());
        given(orderDao.findById(defaultOrder.getId())).willReturn(Optional.ofNullable(defaultOrder));
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() ->
            orderBo.changeOrderStatus(defaultOrder.getId(), defaultOrder));
    }

    private List<OrderLineItem> getDefaultOrderLineItem() {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(Fixtures.getOrderLineItem(1L, 1L, 1L, 1));
        orderLineItems.add(Fixtures.getOrderLineItem(2L, 2L, 1L, 1));
        return orderLineItems;
    }
}