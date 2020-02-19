package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBoTest {

    DefaultMenuDao menuDao = new InMemoryMenuDao();
    DefaultOrderDao orderDao = new InMemoryOrderDao();
    DefaultOrderLineItemDao orderLineItemDao = new InMemoryOrderLineItemDao();
    DefaultOrderTableDao orderTableDao = new InMemoryOrderTableDao();
    DefaultMenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    DefaultMenuProductDao menuProductDao = new InMemoryMenuProductDao();
    DefaultProductDao productDao = new InMemoryProductDao();

    OrderBo orderBo;
    OrderLineItem orderLineItem;

    OrderTable orderTable;
    Menu menu;

    @BeforeEach
    void setUp() {
        orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);

        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(1L);
        menuGroup.setName("세트메뉴");
        menuGroupDao.save(menuGroup);

        Product halfFried = new Product();
        halfFried.setId(1L);
        halfFried.setName("후라이드 반마리");
        halfFried.setPrice(BigDecimal.valueOf(7000L));
        productDao.save(halfFried);

        Product halfChilly = new Product();
        halfChilly.setId(2L);
        halfChilly.setName("양념 반마리");
        halfChilly.setPrice(BigDecimal.valueOf(8000L));
        productDao.save(halfChilly);

        MenuProduct halfFriedProduct = new MenuProduct();
        halfFriedProduct.setMenuId(1L);
        halfFriedProduct.setProductId(halfFried.getId());
        halfFriedProduct.setQuantity(1);
        halfFriedProduct.setSeq(1L);
        menuProductDao.save(halfFriedProduct);

        MenuProduct halfChillyProduct = new MenuProduct();
        halfChillyProduct.setMenuId(2L);
        halfChillyProduct.setProductId(halfChilly.getId());
        halfChillyProduct.setQuantity(1);
        halfChillyProduct.setSeq(2L);
        menuProductDao.save(halfChillyProduct);

        menu = new Menu();
        menu.setId(1L);
        menu.setName("반반세트");
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(Arrays.asList(halfFriedProduct, halfChillyProduct));
        menu.setPrice(BigDecimal.valueOf(14000L));
        menu = menuDao.save(menu);

        orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setEmpty(false);
        orderTable = orderTableDao.save(orderTable);

        orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(1);
        orderLineItem.setSeq(1L);
        orderLineItem.setOrderId(1L);
        orderLineItem = orderLineItemDao.save(orderLineItem);
    }

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createTest() {
        Order order = new Order();
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderTableId(orderTable.getId());
        orderBo.create(order);
    }

    @Test
    @DisplayName("테이블이 할당되지 않거나 비어있는 테이블에는 주문을 생성할 수 없다.")
    void createOrderExceptionTest() {
        Order withoutOrderTable = new Order();
        withoutOrderTable.setOrderLineItems(Arrays.asList(orderLineItem));
        assertThrows(IllegalArgumentException.class, () -> orderBo.create(withoutOrderTable));

        OrderTable orderTable = new OrderTable();
        orderTable.setId(2L);
        orderTable.setEmpty(true);
        orderTableDao.save(orderTable);

        Order withEmptyTable = new Order();
        withEmptyTable.setOrderLineItems(Arrays.asList(orderLineItem));
        withEmptyTable.setOrderTableId(2L);
        assertThrows(IllegalArgumentException.class, () -> orderBo.create(withEmptyTable));
    }

    @Test
    @DisplayName("주문의 상태는 변경할 수 있다.")
    void updateOrderStatusTest() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderTableId(1L);
        order.setOrderStatus(OrderStatus.COOKING.toString());
        orderDao.save(order);

        order.setOrderStatus(OrderStatus.MEAL.toString());
        order = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.MEAL.toString());
    }

    @Test
    @DisplayName("완료된 주문의 상태는 변경할 수 없다.")
    void updateCompletionOrderException() {
        Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderStatus(OrderStatus.COMPLETION.toString());
        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(order.getId(), order));
    }

    @Test
    @DisplayName("주문은 하나 이상의 항목을 갖는다.")
    void createWithoutAnyMenuProductException() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(null);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문이 생성되면 주문은 요리중 상태를 갖는다.")
    void createWithCookingStatusTest() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order = orderBo.create(order);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
    }

    @Test
    @DisplayName("전체 주문 목록을 조회할 수 있다.")
    void readAllOrderListTest() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order = orderDao.save(order);

        assertThat(orderBo.list()).contains(order);
    }
}
