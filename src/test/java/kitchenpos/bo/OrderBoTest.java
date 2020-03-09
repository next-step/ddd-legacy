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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
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

    @DisplayName("사용자는 주문 정보를 등록할 수 있다")
    @Test
    void create() {
        //given
        given(menuDao.countByIdIn(Arrays.asList(orderLineItem1.getMenuId(), orderLineItem2.getMenuId())))
                .willReturn(2L);
        given(orderTableDao.findById(order.getOrderTableId()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(orderClone1);
        given(orderDao.save(order)).willReturn(orderClone1);
        given(orderLineItemDao.save(orderLineItem1)).willReturn(orderLineItem1);
        given(orderLineItemDao.save(orderLineItem2)).willReturn(orderLineItem2);

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

        order2.setOrderLineItems(null);

        //when & then
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
        //given
        given(menuDao.countByIdIn(
                Arrays.asList(orderLineItem1.getMenuId(), orderLineItem2.getMenuId())))
                .willReturn(0L);

        //when & then
        assertThatThrownBy(() -> {
            orderBo.create(order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되지 않은 테이블의 주문 정보를 등록할 수 없다")
    @Test
    void create_unregistered_table() {
        //given
        given(menuDao.countByIdIn(
                Arrays.asList(orderLineItem1.getMenuId(), orderLineItem2.getMenuId())))
                .willReturn(2L);

        given(orderTableDao.findById(order.getOrderTableId()))
                .willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> {
            orderBo.create(order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("미착석 중인 테이블의 주문 정보를 등록할 수 없다")
    @Test
    void create_order_with_empty_table() {
        //given
        List<OrderLineItem> orderLineItems1 = Arrays.asList(orderLineItem1, orderLineItem2);
        order.setOrderLineItems(orderLineItems1);
        List<Long> orderLineItems1Ids = orderLineItems1.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList());
        orderTable.setEmpty(true);

        given(menuDao.countByIdIn(orderLineItems1Ids))
                .willReturn(Long.valueOf(orderLineItems1.size()));
        orderTable.setEmpty(true);

        given(orderTableDao.findById(order.getOrderTableId()))
                .willReturn(Optional.of(orderTable));

        assertThatThrownBy(() -> {
            orderBo.create(order);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 정보에는 테이블 번호, 주문일시, 주문 상세 항목이 포함되며 주문상태는 '요리중'이 된다")
    @Test
    void create_order_info() {
        given(menuDao.countByIdIn(Arrays.asList(orderLineItem1.getMenuId(), orderLineItem2.getMenuId())))
                .willReturn(2L);
        given(orderTableDao.findById(order.getOrderTableId()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(orderClone1);
        given(orderDao.save(order)).willReturn(orderClone1);
        given(orderLineItemDao.save(orderLineItem1)).willReturn(orderLineItem1);
        given(orderLineItemDao.save(orderLineItem2)).willReturn(orderLineItem2);

        order.setOrderedTime(null);
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        order.setOrderLineItems(Arrays.asList(orderLineItem1, orderLineItem2));
        Order actual = orderBo.create(order);

        Assertions.assertAll(
                () -> Assertions.assertEquals(actual.getOrderTableId(), 1L),
                () -> Assertions.assertNotNull(actual.getOrderedTime()),
                () -> Assertions.assertEquals(actual.getOrderStatus(), OrderStatus.COOKING.name()),
                () -> Assertions.assertEquals(actual.getOrderLineItems().get(0), orderLineItem1),
                () -> Assertions.assertEquals(actual.getOrderLineItems().get(1), orderLineItem2)
        );
    }

    @DisplayName("각 주문 상세 항목은 주문 번호, 메뉴 번호, 주문한 메뉴 수량을 포함한다")
    @Test
    void create_order_line_items() {
        //given
        given(menuDao.countByIdIn(Arrays.asList(orderLineItem1.getMenuId(), orderLineItem2.getMenuId())))
                .willReturn(2L);
        given(orderTableDao.findById(order.getOrderTableId()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.save(order)).willReturn(orderClone1);
        given(orderDao.save(order)).willReturn(orderClone1);
        given(orderLineItemDao.save(orderLineItem1)).willReturn(orderLineItem1);
        given(orderLineItemDao.save(orderLineItem2)).willReturn(orderLineItem2);

        order.setOrderLineItems(Arrays.asList(orderLineItem1, orderLineItem2));

        //when
        Order orderResult = orderBo.create(order);
        List<OrderLineItem> actual = orderResult.getOrderLineItems();

        //then
        assertThat(actual.size()).isEqualTo(2L);
        OrderLineItem actualItem1 = actual.get(0);
        OrderLineItem actualItem2 = actual.get(1);

        Assertions.assertAll(
                () -> Assertions.assertEquals(orderLineItem1.getMenuId(), actualItem1.getMenuId()),
                () -> Assertions.assertEquals(orderLineItem1.getOrderId(), actualItem1.getOrderId()),
                () -> Assertions.assertEquals(orderLineItem1.getQuantity(), actualItem1.getQuantity()),
                () -> Assertions.assertEquals(orderLineItem1.getSeq(), actualItem1.getSeq())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals(orderLineItem2.getMenuId(), actualItem2.getMenuId()),
                () -> Assertions.assertEquals(orderLineItem2.getOrderId(), actualItem2.getOrderId()),
                () -> Assertions.assertEquals(orderLineItem2.getQuantity(), actualItem2.getQuantity()),
                () -> Assertions.assertEquals(orderLineItem2.getSeq(), actualItem2.getSeq())
        );
    }

    @DisplayName("사용자는 주문의 상태를 변경할 수 있다")
    @Test
    void changeOrderStatus() {
        given(orderDao.findById(order.getId()))
                .willReturn(Optional.of(orderClone1));

        given(orderDao.save(orderClone1)).willReturn(orderClone1);

        given(orderLineItemDao.findAllByOrderId(order.getId()))
                .willReturn(Arrays.asList(orderLineItem1, orderLineItem2));

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
        given(orderDao.findById(order.getId()))
                .willReturn(Optional.empty());

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

        given(orderDao.findById(order.getId()))
                .willReturn(Optional.of(orderClone1));

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
        given(orderDao.findAll())
                .willReturn(Arrays.asList(orderClone1, orderClone2));

        given(orderLineItemDao.findAllByOrderId(orderClone1.getId()))
                .willReturn(Collections.singletonList(orderLineItem1));

        given(orderLineItemDao.findAllByOrderId(orderClone2.getId()))
                .willReturn(Collections.singletonList(orderLineItem2));

        //when
        List<Order> actual = orderBo.list();
        assertThat(actual.size()).isEqualTo(2);
        assertThat(actual).containsExactlyInAnyOrder(orderClone1, orderClone2);
        assertThat(actual.get(0).getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem1);
        assertThat(actual.get(1).getOrderLineItems()).containsExactlyInAnyOrder(orderLineItem2);
    }
}