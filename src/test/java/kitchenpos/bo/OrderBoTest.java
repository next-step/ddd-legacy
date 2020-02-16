package kitchenpos.bo;

import kitchenpos.dao.Interface.MenuDao;
import kitchenpos.dao.Interface.OrderDao;
import kitchenpos.dao.Interface.OrderLineItemDao;
import kitchenpos.dao.Interface.OrderTableDao;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderStatus;
import kitchenpos.model.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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

    private Order input;
    private Order saved;
    private OrderTable savedOrderTable;
    private OrderLineItem orderLineItem;

    @BeforeEach
    void setUp() {
        orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);

        input = new Order();
        input.setOrderTableId(1L);
        input.setOrderStatus(OrderStatus.COOKING.name());
        input.setOrderedTime(LocalDateTime.of(2020, 2, 11, 17, 44));
        input.setOrderLineItems(Collections.singletonList(orderLineItem));

        saved = new Order();
        saved.setId(1L);
        saved.setOrderTableId(1L);
        saved.setOrderStatus(OrderStatus.COOKING.name());
        saved.setOrderedTime(LocalDateTime.of(2020, 2, 11, 17, 44));
        saved.setOrderLineItems(Collections.singletonList(orderLineItem));

        savedOrderTable = new OrderTable();
        savedOrderTable.setId(1L);
    }

    @DisplayName("주문 생성시 주문내역이 없으면 IllegalArgumentException 발생")
    @ParameterizedTest
    @NullAndEmptySource
    void createNullOrderLines(List<OrderLineItem> parameter) {
        input.setOrderLineItems(parameter);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(input));
    }

    @DisplayName("주문 생성시 주문내역과 메뉴 size 가 다르면 IllegalArgumentException 발생")
    @Test
    void createDiffSize() {
        given(menuDao.countByIdIn(anyList()))
                .willReturn(2L);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(input));
    }

    @DisplayName("주문 생성시 테이블이 없으면 IllegalArgumentException 발생")
    @Test
    void createNullTable() {
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(input));
    }

    @DisplayName("주문 생성시 테이블이 비어있으면 IllegalArgumentException 발생")
    @Test
    void createEmptyTable() {
        savedOrderTable.setEmpty(true);
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(savedOrderTable));

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(input));
    }

    @DisplayName("주문 생성")
    @Test
    void create() {
        given(menuDao.countByIdIn(anyList()))
                .willReturn(1L);

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(savedOrderTable));

        given(orderDao.save(input))
                .willReturn(saved);

        given(orderLineItemDao.save(orderLineItem))
                .willReturn(new OrderLineItem());

        Order result = orderBo.create(input);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOrderTableId()).isEqualTo(1L);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(result.getOrderLineItems().get(0)).isEqualTo(new OrderLineItem());
    }

    @DisplayName("주문 목록 조회")
    @Test
    void list() {
        given(orderDao.findAll())
                .willReturn(Collections.singletonList(saved));

        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(Collections.singletonList(orderLineItem));

        List<Order> result = orderBo.list();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getOrderLineItems().size()).isEqualTo(1);
        assertThat(result.get(0).getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(result.get(0).getOrderTableId()).isEqualTo(1L);
    }

    @DisplayName("주문 상태 변경 시 주문이 없으면 IllegalArgumentException 발생")
    @Test
    void changeOrderStatusNullOrder() {
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(1L, input));
    }

    @DisplayName("주문 상태 변경 시 주문이 완료된 상태면 IllegalArgumentException 발생")
    @Test
    void changeOrderStatusCompletion() {
        saved.setOrderStatus(OrderStatus.COMPLETION.name());
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.of(saved));

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(1L, input));
    }

    @DisplayName("주문 상태 변경")
    @Test
    void changeOrderStatus() {
        input.setOrderStatus(OrderStatus.MEAL.name());
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.of(saved));

        Order result = orderBo.changeOrderStatus(1L, input);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
    }
}
