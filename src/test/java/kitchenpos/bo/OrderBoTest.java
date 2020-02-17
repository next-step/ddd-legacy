package kitchenpos.bo;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    private OrderLineItem orderLineItem1;
    private OrderLineItem orderLineItem2;
    private Order order;
    private Order order2;
    private OrderTable orderTable;
    private Order orderClone1;
    private Order orderClone2;

    @BeforeEach
    void setUp() {
        prepareFixtures();
        prepareMockito();
    }

    void prepareFixtures() {
        orderLineItem1 = new OrderLineItem();
        orderLineItem1.setOrderId(1L);
        orderLineItem1.setSeq(1L);
        orderLineItem1.setMenuId(1L);
        orderLineItem1.setQuantity(1);

        orderLineItem2 = new OrderLineItem();
        orderLineItem2.setOrderId(1L);
        orderLineItem2.setSeq(2L);
        orderLineItem2.setMenuId(2L);
        orderLineItem2.setQuantity(1);

        order = new Order();
        order.setOrderTableId(1L);

        order2 = new Order();
        order2.setOrderTableId(1L);

        orderClone1 = new Order();
        orderClone1.setId(1L);
        orderClone1.setOrderTableId(1L);
        orderClone1.setOrderedTime(LocalDateTime.now());
        orderClone1.setOrderStatus(OrderStatus.COOKING.name());

        orderClone2 = new Order();
        orderClone2.setId(2L);
        orderClone2.setOrderTableId(1L);
        orderClone2.setOrderedTime(LocalDateTime.now());
        orderClone2.setOrderStatus(OrderStatus.COOKING.name());

        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(4);

        order.setOrderLineItems(Arrays.asList(orderLineItem1, orderLineItem2));
        orderClone1.setOrderLineItems(Arrays.asList(orderLineItem1, orderLineItem2));
    }

    void prepareMockito() {
        Mockito.when(menuDao.countByIdIn(
                Arrays.asList(orderLineItem1.getMenuId(), orderLineItem2.getMenuId()))
        ).thenReturn(2L);

        Mockito.when(orderDao.findById(order.getId()))
                .thenReturn(Optional.of(orderClone1));

        Mockito.when(orderDao.save(order)).thenReturn(orderClone1);

        Mockito.when(orderTableDao.findById(order.getOrderTableId()))
                .thenReturn(Optional.of(orderTable));

        Mockito.when(orderLineItemDao.findAllByOrderId(order.getId()))
                .thenReturn(Arrays.asList(orderLineItem1, orderLineItem2));

        Mockito.when(orderLineItemDao.save(orderLineItem1)).thenReturn(orderLineItem1);
        Mockito.when(orderLineItemDao.save(orderLineItem2)).thenReturn(orderLineItem2);
    }

    @DisplayName("사용자는 주문 정보를 등록할 수 있다")
    @Test
    void create() {
        //given
        //when
        Order actual = orderBo.create(order);

        //then
        assertThat(actual.getId()).isEqualTo(orderClone1.getId());
        assertThat(actual.getOrderedTime()).isEqualTo(orderClone1.getOrderedTime());
        assertThat(actual.getOrderStatus()).isEqualTo(orderClone1.getOrderStatus());
        assertThat(actual.getOrderTableId()).isEqualTo(orderClone1.getOrderTableId());
        assertThat(actual.getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem1, orderLineItem2);
    }

    @DisplayName("주문정보에는 1개 이상의 '주문 상세 항목'이 포함되어야 한다")
    @Test
    void create_must_have_one_orderLineItem() {
        //given
        List<OrderLineItem> orderLineItems1 = new ArrayList<>();
        order.setOrderLineItems(orderLineItems1);
        List<Long> orderLineItems1Ids = orderLineItems1.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());
        Mockito.when(menuDao.countByIdIn(orderLineItems1Ids))
                .thenReturn(Long.valueOf(orderLineItems1.size()));

        order2.setOrderLineItems(null);

        //when
        //then
        assertThatThrownBy(() -> {
            orderBo.create(order);
        }).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> {
            orderBo.create(order2);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 메뉴는 주문할 수 없다")
    @Test
    void create_unregistered_menu() {
        Mockito.when(menuDao.countByIdIn(
                Arrays.asList(orderLineItem1.getMenuId(), orderLineItem2.getMenuId())))
                .thenReturn(0L);

        assertThatThrownBy(() -> {
            orderBo.create(order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 테이블의 주문 정보를 등록할 수 없다")
    @Test
    void create_unregistered_table() {
        //given
        Mockito.when(orderTableDao.findById(order.getOrderTableId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            orderBo.create(order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("미착석 중인 테이블의 주문 정보를 등록할 수 없다")
    @Test
    void create_order_with_empty_table() {
        //given
        orderTable.setEmpty(true);

        assertThatThrownBy(() -> {
            orderBo.create(order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 정보에는 테이블 번호, 주문일시, 주문 상세 항목이 포함되며 주문상태는 '요리중'이 된다")
    @Test
    void create_order_info() {
        order.setOrderedTime(null);
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        order.setOrderLineItems(Arrays.asList(orderLineItem1, orderLineItem2));
        Order actual = orderBo.create(order);

        assertThat(actual.getOrderTableId()).isEqualTo(1L);
        assertThat(actual.getOrderedTime()).isNotNull();
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        assertThat(actual.getOrderLineItems().get(0)).isEqualTo(orderLineItem1);
        assertThat(actual.getOrderLineItems().get(1)).isEqualTo(orderLineItem2);
    }

    @DisplayName("각 주문 상세 항목은 주문 번호, 메뉴 번호, 주문한 메뉴 수량을 포함한다")
    @Test
    void create_order_line_items() {
        //given
        order.setOrderLineItems(Arrays.asList(orderLineItem1, orderLineItem2));

        //when
        Order orderResult = orderBo.create(order);
        List<OrderLineItem> actual = orderResult.getOrderLineItems();

        //then
        assertThat(actual.size()).isEqualTo(2L);
        OrderLineItem actualItem1 = actual.get(0);
        OrderLineItem actualItem2 = actual.get(1);

        assertThat(orderLineItem1.getMenuId()).isEqualTo(actualItem1.getMenuId());
        assertThat(orderLineItem1.getOrderId()).isEqualTo(actualItem1.getOrderId());
        assertThat(orderLineItem1.getQuantity()).isEqualTo(actualItem1.getQuantity());
        assertThat(orderLineItem1.getSeq()).isEqualTo(actualItem1.getSeq());

        assertThat(orderLineItem2.getMenuId()).isEqualTo(actualItem2.getMenuId());
        assertThat(orderLineItem2.getOrderId()).isEqualTo(actualItem2.getOrderId());
        assertThat(orderLineItem2.getQuantity()).isEqualTo(actualItem2.getQuantity());
        assertThat(orderLineItem2.getSeq()).isEqualTo(actualItem2.getSeq());
    }

    @DisplayName("사용자는 주문의 상태를 변경할 수 있다")
    @Test
    void changeOrderStatus() {
        order.setOrderStatus(OrderStatus.COOKING.name());
        Order actual = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());

        order.setOrderStatus(OrderStatus.MEAL.name());
        actual = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());

        order.setOrderStatus(OrderStatus.COMPLETION.name());
        actual = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.COMPLETION.name());
    }

    @DisplayName("등록되지 않은 주문의 상태는 변경할 수 없다")
    @Test
    void changeOrderStatus_unregistered_order() {
        //given
        Mockito.when(orderDao.findById(order.getId()))
                .thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> {
            orderBo.changeOrderStatus(order.getId(), order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("'식사완료' 된 주문의 상태는 변경할 수 없다")
    @Test
    void changeOrderStatus_completion_status() {
        //given
        orderClone1.setOrderStatus(OrderStatus.COMPLETION.name());

        Mockito.when(orderDao.findById(order.getId()))
                .thenReturn(Optional.of(orderClone1));

        //when
        //then
        assertThatThrownBy(() -> {
            orderBo.changeOrderStatus(order.getId(), order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("사용자는 등록된 주문 목록을 조회할 수 있다")
    @Test
    void list() {
        //given
        Mockito.when(orderDao.findAll())
                .thenReturn(Arrays.asList(orderClone1, orderClone2));

        Mockito.when(orderLineItemDao.findAllByOrderId(orderClone1.getId()))
                .thenReturn(Collections.singletonList(orderLineItem1));

        Mockito.when(orderLineItemDao.findAllByOrderId(orderClone2.getId()))
                .thenReturn(Collections.singletonList(orderLineItem2));

        //when
        List<Order> actual = orderBo.list();

        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).containsExactlyInAnyOrder(orderClone1, orderClone2);
        assertThat(actual.get(0).getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem1);
        assertThat(actual.get(1).getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem2);
    }
}