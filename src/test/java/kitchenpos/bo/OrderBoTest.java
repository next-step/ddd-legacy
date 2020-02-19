package kitchenpos.bo;

import kitchenpos.Fixtures;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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
    @BeforeEach
    public void setUP() {
        defaultOrder = Fixtures.getOrder(1L, LocalDateTime.now(), getDefaultOrderLineItem(), OrderStatus.MEAL.name(), 1L);
        defaultOrderLineItems = getDefaultOrderLineItem();
    }

    @DisplayName("주문된 메뉴가 1개 이상 있어야 한다.")
    @Test
    public void createNoEmptyOrder() {
        Order order = Fixtures.getOrder(1L, LocalDateTime.now(), new ArrayList<>(), OrderStatus.MEAL.name(), 1L);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문된 메뉴는 반드시 팔고 있는 메뉴여야 한다.")
    @Test
    public void createWithMenus() {
        final List<Long> menuIds = defaultOrderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());

        given(menuDao.countByIdIn(menuIds)).willReturn(1L);
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderBo.create(defaultOrder));
    }

    private List<OrderLineItem> getDefaultOrderLineItem() {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(Fixtures.getOrderLineItem(1L, 1L, 1L, 1));
        orderLineItems.add(Fixtures.getOrderLineItem(2L, 2L, 1L, 1));
        return orderLineItems;
    }
}