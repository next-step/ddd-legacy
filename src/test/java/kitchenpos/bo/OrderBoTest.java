package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
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
    @Test
    void create() {
        when(menuDao.countByIdIn(anyList()))
                .thenReturn(Long.valueOf(orderLineItemList.size()));
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));
        when(orderDao.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    order.setId(1L);
                    return order;
                });
        when(orderLineItemDao.save(any(OrderLineItem.class)))
                .thenReturn(orderLineItem);

        Order result = orderBo.create(order);
        assertThat(result.getOrderStatus()).isEqualTo(String.valueOf(OrderStatus.COOKING));

    }

    @DisplayName("주문메뉴가 반드시 있어야 한다.")
    @Test
    void createMustHaveOrderItem() {
        orderLineItemList = null;
        Throwable thrown = catchThrowable(() ->{
            orderBo.create(order);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("매장에서 판매하지 않는 메뉴를 주문할 수 없다.")
    @Test
    void createMustHaveItemsServed() {
        when(menuDao.countByIdIn(anyList()))
                .thenReturn(Long.valueOf(orderLineItemList.size() + 1));
        Throwable thrown = catchThrowable(() ->{
            orderBo.create(order);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문할 때 테이블을 반드시 지정해야 한다.")
    @Test
    void createMustHaveDesignatedTable() {
        optionalOrderTable = Optional.empty();
        Throwable thrown = catchThrowable(() ->{
            orderBo.create(order);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문이 들어오면 주문상태가 조리중(COOKING)으로 된다.")
    @Test
    void createThenChangeStatusToCooking() {
        when(menuDao.countByIdIn(anyList()))
                .thenReturn(Long.valueOf(orderLineItemList.size()));
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));
        when(orderDao.save(any(Order.class)))
                .thenReturn(order);
        Order result = orderBo.create(order);
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }

    @DisplayName("주문 목록을 볼 수 있다.")
    @Test
    void list() {
        when(orderDao.findAll())
                .thenReturn(orderList);
        when(orderLineItemDao.findAllByOrderId(anyLong()))
                .thenReturn(orderLineItemList);
        List<Order> result = orderBo.list();
        assertThat(result.size()).isEqualTo(orderList.size());
    }

    @DisplayName("주문의 상태를 변경할 수 있다.")
    @Test
    void changeOrderStatus() {
        when(orderDao.findById(anyLong()))
                .thenReturn(optionalOrder);
        when(orderDao.save(any(Order.class)))
                .thenReturn(order);
        when(orderLineItemDao.findAllByOrderId(anyLong()))
                .thenReturn(orderLineItemList);
        Order result = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(result.getOrderStatus()).isNotEqualTo(OrderStatus.COMPLETION);
    }

    @DisplayName("이미 생성된 주문의 상태만 변경할 수 있다.")
    @Test
    void changeOrderStatusOrderedOne() {
        optionalOrder = Optional.empty();
        Throwable thrown = catchThrowable(() ->{
            orderBo.changeOrderStatus(order.getId(), order);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("식사가 끝나고 결재도 끝난 주문은 상태를 변경할 수 없다.")
    @Test
    void changeOrderStatusOfCompletedOrders() {
        order.setOrderStatus(OrderStatus.COMPLETION.name());
        Throwable thrown = catchThrowable(() ->{
            orderBo.changeOrderStatus(order.getId(), order);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }


}