package kitchenpos.bo;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private OrderTable orderTable;
    private Optional<OrderTable> optionalOrderTable;

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

    }

    @DisplayName("주문을 생성한다.")
    @Test
    void create() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setMenuId(1L);
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);
        menuProduct.setSeq(1L);

        List<MenuProduct> menuProductList = new ArrayList<>();
        menuProductList.add(menuProduct);

        Menu menu = new Menu();
        menu.setName("후라이드치킨");
        menu.setPrice(new BigDecimal(16000));
        menu.setMenuProducts(menuProductList);
        menu.setMenuGroupId(2L);
        menu.setId(1L);

        Optional<Menu> optionalMenu = Optional.of(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setOrderId(1L);
        orderLineItem.setQuantity(2L);
        orderLineItem.setSeq(1L);

        List<OrderLineItem> orderLineItemList = new ArrayList<>();
        orderLineItemList.add(orderLineItem);

        Order order = new Order();
        order.setId(1L);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItemList);
        order.setOrderStatus(String.valueOf(OrderStatus.MEAL));
        order.setOrderTableId(1L);

        OrderTable orderTable = new OrderTable();
        orderTable.setEmpty(true);
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(3);
        orderTable.setTableGroupId(1L);

        Optional<OrderTable> optionalOrderTable = Optional.of(orderTable);

        when(menuDao.countByIdIn(anyList()))
                .thenReturn(Long.valueOf(orderLineItemList.size()));
        when(orderTableDao.findById(anyLong()))
                .thenReturn(Optional.of(orderTable));

        Order result = orderBo.create(order);
        assertThat(result.getId()).isEqualTo(1L);

    }

    @DisplayName("주문 목록을 볼 수 있다.")
    @Test
    void list() {
    }

    @DisplayName("주문은 상태를 가진다. (고객이 식사중인 주문: MEAL, 결재가 끝난 주문: COMPLETE)")
    @Test
    void changeOrderStatus() {
    }
}