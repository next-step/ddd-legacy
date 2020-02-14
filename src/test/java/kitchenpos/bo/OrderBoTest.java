package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {
    @Mock
    private MenuDao menuDao;

    @Mock
    private OrderTableDao orderTableDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderLineItemDao orderLineItemDao;

    @InjectMocks
    private OrderBo orderBo;

    @DisplayName("주문을 할 수 있다")
    @Test
    void createOrder() {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("엄청이득메뉴");
        menu.setPrice(new BigDecimal(10000));
        menu.setMenuGroupId(1L);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(2);
        ArrayList<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(1L);
        orderTable.setEmpty(false);

        Order expected = new Order();
        expected.setId(1L);
        expected.setOrderLineItems(orderLineItems);
        expected.setOrderedTime(LocalDateTime.now());
        expected.setOrderTableId(orderTable.getId());
        expected.setOrderStatus(OrderStatus.COOKING.name());

        //given
        given(menuDao.countByIdIn(orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList()))
        ).willReturn((long) orderLineItems.size());
        given(orderTableDao.findById(expected.getOrderTableId())).willReturn(Optional.of(orderTable));
        given(orderDao.save(expected)).willReturn(expected);

        //when
        Order result = orderBo.create(expected);

        //then
        assertAll(
                () -> assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name()),
                () -> assertThat(result.getOrderLineItems().size()).isEqualTo(expected.getOrderLineItems().size())
        );
    }

    @DisplayName("메뉴가 입력되지 않으면 주문할 수 없다")
    @ParameterizedTest
    @MethodSource("createInvalidMenu")
    void createOrder_invalidMenuOrder(List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(orderLineItems);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(1L);
        order.setOrderStatus(OrderStatus.COOKING.name());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    static Stream<List<OrderLineItem>> createInvalidMenu() {
        List<OrderLineItem> emptyOrderLineItems = Collections.emptyList();
        return Stream.of(null, emptyOrderLineItems);
    }

    @DisplayName("존재하지 않는 메뉴는 주문할 수 없다")
    @Test
    void createOrder_nonExistMenuOrder() {

        OrderLineItem item1 = new OrderLineItem();
        item1.setMenuId(-1L);
        OrderLineItem item2 = new OrderLineItem();
        item2.setMenuId(-2L);
        List<OrderLineItem> nonExistOrderLineItems = Arrays.asList(item1, item2);

        Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(nonExistOrderLineItems);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(1L);
        order.setOrderStatus(OrderStatus.COOKING.name());

        //given
        given(menuDao.countByIdIn(nonExistOrderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList()))
        ).willReturn((long) nonExistOrderLineItems.size() - 1);


        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("비어있는 테이블에서는 메뉴는 주문할 수 없다")
    @Test
    void createOrder_nonExistOrderTable() {

        OrderLineItem item1 = new OrderLineItem();
        item1.setMenuId(-1L);
        OrderLineItem item2 = new OrderLineItem();
        item2.setMenuId(-2L);
        List<OrderLineItem> orderLineItems = Arrays.asList(item1, item2);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(1L);
        orderTable.setEmpty(true);

        Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(orderLineItems);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(orderTable.getId());
        order.setOrderStatus(OrderStatus.COOKING.name());

        //given
        given(menuDao.countByIdIn(orderLineItems.stream()
                .map(OrderLineItem::getMenuId)
                .collect(Collectors.toList()))
        ).willReturn((long) orderLineItems.size());
        given(orderTableDao.findById(order.getOrderTableId())).willReturn(Optional.of(orderTable));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문을 조회할 수 있다")
    @Test
    void listOrder() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(2);
        ArrayList<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        Order expected = new Order();
        expected.setId(1L);
        expected.setOrderLineItems(orderLineItems);
        expected.setOrderedTime(LocalDateTime.now());
        expected.setOrderTableId(1L);

        //given
        given(orderDao.findAll()).willReturn(Arrays.asList(expected));
        given(orderLineItemDao.findAllByOrderId(1L)).willReturn(orderLineItems);

        //when
        List<Order> result = orderBo.list();

        //then
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("주문의 상태를 변경 할 수 있다")
    @Test
    void changeOrderStatus() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(2);
        ArrayList<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(1L);
        orderTable.setEmpty(false);

        Order expected = new Order();
        expected.setId(1L);
        expected.setOrderLineItems(orderLineItems);
        expected.setOrderedTime(LocalDateTime.now());
        expected.setOrderTableId(orderTable.getId());
        expected.setOrderStatus(OrderStatus.MEAL.name());

        Order previousOrder = new Order();
        previousOrder.setId(expected.getId());
        previousOrder.setOrderLineItems(orderLineItems);
        previousOrder.setOrderedTime(LocalDateTime.now());
        previousOrder.setOrderTableId(orderTable.getId());
        previousOrder.setOrderStatus(OrderStatus.COOKING.name());

        //given
        given(orderDao.findById(previousOrder.getId())).willReturn(Optional.of(previousOrder));
        given(orderDao.save(previousOrder)).willReturn(expected);
        given(orderLineItemDao.findAllByOrderId(expected.getId())).willReturn(expected.getOrderLineItems());

        //when
        Order result = orderBo.changeOrderStatus(previousOrder.getId(), expected);

        //then
        assertThat(result.getOrderStatus()).isEqualTo(expected.getOrderStatus());
    }


    @DisplayName("종료된 주문의 상태는 변경 할 수 없다")
    @Test
    void changeOrderStatus_sameOrderStatus() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(2);
        ArrayList<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);

        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(1L);
        orderTable.setEmpty(false);

        Order previousOrder = new Order();
        previousOrder.setId(1L);
        previousOrder.setOrderLineItems(orderLineItems);
        previousOrder.setOrderedTime(LocalDateTime.now());
        previousOrder.setOrderTableId(orderTable.getId());
        previousOrder.setOrderStatus(OrderStatus.COMPLETION.name());

        //given
        given(orderDao.findById(previousOrder.getId())).willReturn(Optional.of(previousOrder));

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderBo.changeOrderStatus(previousOrder.getId(), previousOrder));
    }
}
