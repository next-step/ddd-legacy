package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderBoTest {
    @Mock private MenuDao menuDao;
    @Mock private OrderDao orderDao;
    @Mock private OrderLineItemDao orderLineItemDao;
    @Mock private OrderTableDao orderTableDao;

    @InjectMocks private OrderBo orderBo;

    private MenuProduct menuProduct;
    private List<MenuProduct> menuProductList;
    private Menu menu;
    private Optional<Menu> optionalMenu;
    private OrderLineItem orderLineItem;
    private List<OrderLineItem> orderLineItemList;
    private Order order;
    private List<Order> orderList;
    private OrderTable orderTable;
    private Optional<OrderTable> optionalOrderTable;
    private Optional<Order> optionalOrder;

    @BeforeEach
    void setup() {
        menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);
        menuProduct.setSeq(1L);

        menuProductList = new ArrayList<>();
        menuProductList.add(menuProduct);

        menu = new Menu();
        menu.setName("후라이드치킨");
        menu.setPrice(new BigDecimal(16000));
        menu.setMenuProducts(menuProductList);
        menu.setMenuGroupId(2L);
        menu.setId(1L);

        optionalMenu = Optional.of(menu);

        orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setOrderId(1L);
        orderLineItem.setQuantity(2L);
        orderLineItem.setSeq(1L);

        orderLineItemList = new ArrayList<>();
        orderLineItemList.add(orderLineItem);

        order = new Order();
        order.setId(1L);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItemList);
        order.setOrderStatus(String.valueOf(OrderStatus.MEAL));
        order.setOrderTableId(1L);

        orderList = new ArrayList<>();
        orderList.add(order);

        orderTable = new OrderTable();
        orderTable.setEmpty(false);
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(3);
        orderTable.setTableGroupId(1L);

        optionalOrderTable = Optional.of(orderTable);
        optionalOrder = Optional.of(order);
    }

    @DisplayName("주문을 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(longs = {1L})
    void create(Long orderId) {
        //given
        List<OrderLineItem> givenOrderLineItemList = orderLineItemList;
        OrderTable givenOrderTable = orderTable;
        Order givenOrder = order;
        OrderLineItem givenOrderLineItem = orderLineItem;
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItemList.size()));
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(givenOrderTable));
        given(orderDao.save(any(Order.class)))
                .willAnswer(invocation -> {
                    givenOrder.setId(orderId);
                    return givenOrder;
                });
        given(orderLineItemDao.save(any(OrderLineItem.class)))
                .willReturn(givenOrderLineItem);

        //when
        Order actualOrder = orderBo.create(givenOrder);

        //then
        assertThat(actualOrder.getOrderStatus())
                .isEqualTo(String.valueOf(OrderStatus.COOKING));
    }

    @DisplayName("주문메뉴가 반드시 있어야 한다.")
    @ParameterizedTest
    @NullSource
    void createMustHaveOrderItem(List<OrderLineItem> orderLineItemList) {
        //given
        Order givenOrder = order;
        givenOrder.setOrderLineItems(orderLineItemList);

        //when
        //then
        assertThatThrownBy(() ->{ orderBo.create(givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장에서 판매하지 않는 메뉴를 주문할 수 없다.")
    @Test
    void createMustHaveItemsServed() {
        //given
        List<OrderLineItem> givenOrderLineItemList = orderLineItemList;
        Order givenOrder = order;
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItemList.size() + 1));

        //when
        //then
        assertThatThrownBy(() ->{ orderBo.create(givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문할 때 테이블을 반드시 지정해야 한다.")
    @Test
    void createMustHaveDesignatedTable() {
        //given
        Order givenOrder = order;
        List<OrderLineItem> givenOrderLineItemList = orderLineItemList;
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItemList.size()));
        given(orderTableDao.findById(givenOrder.getId()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() ->{ orderBo.create(givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 들어오면 주문상태가 조리중(COOKING)으로 된다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = INCLUDE, names = {"COOKING"})
    void createThenChangeStatusToCooking(@NotNull OrderStatus orderStatus) {
        //given
        List<OrderLineItem> givenOrderLineItemList = orderLineItemList;
        OrderTable givenOrderTable = orderTable;
        Order givenOrder = order;
        given(menuDao.countByIdIn(anyList()))
                .willReturn(Long.valueOf(givenOrderLineItemList.size()));
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(givenOrderTable));
        given(orderDao.save(any(Order.class)))
                .willReturn(givenOrder);

        //when
        Order actualOrder = orderBo.create(givenOrder);

        //then
        assertThat(actualOrder.getOrderStatus())
                .isEqualTo(orderStatus.name());
    }

    @DisplayName("주문 목록을 볼 수 있다.")
    @Test
    void list() {
        //given
        List<Order> givenOrderList = orderList;
        List<OrderLineItem> givenOrderLineItemList = orderLineItemList;
        given(orderDao.findAll())
                .willReturn(givenOrderList);
        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(givenOrderLineItemList);

        //when
        List<Order> actualOrderList = orderBo.list();

        //then
        assertThat(actualOrderList.size())
                .isEqualTo(givenOrderList.size());
    }

    @DisplayName("주문의 상태를 변경할 수 있다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = INCLUDE, names = {"COMPLETION"})
    void changeOrderStatus(@NotNull OrderStatus orderStatus) {
        //given
        Optional<Order> givenOptionalOrder = optionalOrder;
        Order givenOrder = order;
        List<OrderLineItem> givenOrderLineItemList = orderLineItemList;
        given(orderDao.findById(anyLong()))
                .willReturn(givenOptionalOrder);
        given(orderDao.save(any(Order.class)))
                .willReturn(givenOrder);
        given(orderLineItemDao.findAllByOrderId(anyLong()))
                .willReturn(givenOrderLineItemList);

        //when
        Order actualOrder = orderBo.changeOrderStatus(givenOrder.getId(), givenOrder);

        //then
        assertThat(actualOrder.getOrderStatus())
                .isNotEqualTo(orderStatus.name());
    }

    @DisplayName("이미 생성된 주문의 상태만 변경할 수 있다.")
    @Test
    void changeOrderStatusOrderedOne() {
        //given
        Order givenOrder = order;
        given(orderDao.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() ->{
            orderBo.changeOrderStatus(givenOrder.getId(), givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("식사가 끝나고 결재도 끝난 주문은 상태를 변경할 수 없다.")
    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, mode = INCLUDE, names = {"COMPLETION"})
    void changeOrderStatusOfCompletedOrders(@NotNull OrderStatus orderStatus) {
        //given
        Order givenOrder = order;
        givenOrder.setOrderStatus(orderStatus.name());

        //when
        //then
        assertThatThrownBy(() ->{
            orderBo.changeOrderStatus(givenOrder.getId(), givenOrder); })
                .isInstanceOf(IllegalArgumentException.class);
    }


}