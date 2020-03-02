package kitchenpos.bo;

import kitchenpos.TestFixture;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {
    private static final int INT_ONE = 1;

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
        Order requestOrder = TestFixture.generateOrderOne();
        Order savedOrder = TestFixture.generateOrderOne();
        savedOrder.setOrderStatus(OrderStatus.COOKING.name());

        List<OrderLineItem> orderLineItems = requestOrder.getOrderLineItems();

        OrderTable orderTable = TestFixture.generateOrderTableNotEmpty();

        List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());

        orderLineItems.stream()
                .forEach(orderLineItem -> {
                    given(orderLineItemDao.save(orderLineItem)).willReturn(orderLineItem);
                });

        given(menuDao.countByIdIn(menuIds)).willReturn(Long.valueOf(orderLineItems.size()));
        given(orderTableDao.findById(requestOrder.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(requestOrder)).willReturn(savedOrder);

        Order order = orderBo.create(requestOrder);

        assertAll(
                () -> assertThat(order.getId()).isEqualTo(savedOrder.getId()),
                () -> assertThat(order.getOrderLineItems()).containsAll(orderLineItems),
                () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name())
        );
    }

    @DisplayName("상세 주문 내역이 존재 하지 않을시 에러")
    @Test
    void createFailByNotExistOrderLineItem() {
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderLineItems(null);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("등록되어 있지 않은 메뉴가 주문 내역에 포함 되어 있을 시 에러")
    @Test
    void createFailByNotInsertedOrderLineItem() {
        Order requestOrder = TestFixture.generateOrderOne();

        List<OrderLineItem> orderLineItems = requestOrder.getOrderLineItems();

        List<Long> menuIds = orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());

        given(menuDao.countByIdIn(menuIds)).willReturn(Long.valueOf(orderLineItems.size() - INT_ONE));

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("존재 하지 않는 테이블이거나 테이블 정보가 없을때 일때 에러")
    @Test
    void createFailByNotExistTable() {
        Order requestOrder = TestFixture.generateOrderOne();

        List<OrderLineItem> orderLineItems = requestOrder.getOrderLineItems();

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
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderStatus(value);

        List<OrderLineItem> orderLineItems = requestOrder.getOrderLineItems();

        Order savedOrder = TestFixture.generateOrderCooking();

        given(orderDao.findById(requestOrder.getId())).willReturn(Optional.of(savedOrder));
        given(orderLineItemDao.findAllByOrderId(requestOrder.getId())).willReturn(orderLineItems);

        Order changedOrder = orderBo.changeOrderStatus(requestOrder.getId(), requestOrder);

        assertAll(
                () -> assertThat(changedOrder.getId()).isEqualTo(savedOrder.getId()),
                () -> assertThat(changedOrder.getOrderStatus()).isEqualTo(requestOrder.getOrderStatus())
        );
    }

    @DisplayName("COMPLETE 상태의 주문 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void changeOrderStatusFailByComplete(String value) {
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderStatus(value);

        Order savedOrder = TestFixture.generateOrderCompletion();

        given(orderDao.findById(requestOrder.getId())).willReturn(Optional.of(savedOrder));

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }

    @DisplayName("등록되지 않은 주문은 상태를 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"MEAL"})
    void changeOrderStatusFailByNotInsert(String value) {
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderStatus(value);

        given(orderDao.findById(requestOrder.getId())).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }
}
