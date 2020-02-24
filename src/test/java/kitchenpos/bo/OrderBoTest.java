package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBoTest {

    private MenuDao menuDao = new InMemoryMenuDao();
    private OrderDao orderDao = new InMemoryOrderDao();
    private OrderLineItemDao orderLineItemDao = new InMemoryOrderLineItemDao();
    private OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    private MenuProductDao menuProductDao = new InMemoryMenuProductDao();
    private ProductDao productDao = new InMemoryProductDao();

    private OrderBo orderBo;

    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);

        menuGroupDao.save(MenuGroupTest.ofSet());

        productDao.save(ProductTest.ofHalfFried());
        productDao.save(ProductTest.ofHalfChilly());

        menuProductDao.save(MenuProductTest.ofHalfFriedProduct());
        menuProductDao.save(MenuProductTest.ofHalfChillyProduct());

        menuDao.save(MenuTest.ofHalfAndHalf());

        orderTable = orderTableDao.save(OrderTableTest.ofSingle());

        orderLineItemDao.save(OrderLineItemTest.of());
    }

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createTest() {
        Order order = OrderTest.ofOneHalfAndHalfInSingleTable();
        orderBo.create(order);
    }

    @Test
    @DisplayName("테이블이 할당되지 않거나 비어있는 테이블에는 주문을 생성할 수 없다.")
    void createOrderExceptionTest() {
        Order withoutOrderTable = OrderTest.ofOneHalfAndHalfInSingleTable();
        withoutOrderTable.setOrderTableId(null);
        assertThrows(IllegalArgumentException.class, () -> orderBo.create(withoutOrderTable));

        Order withEmptyTable = OrderTest.ofOneHalfAndHalfInSingleTable();
        orderTable.setEmpty(true);
        assertThrows(IllegalArgumentException.class, () -> orderBo.create(withEmptyTable));
    }

    @Test
    @DisplayName("주문의 상태는 변경할 수 있다.")
    void updateOrderStatusTest() {
        Order order = OrderTest.ofOneHalfAndHalfInSingleTable();
        order = orderDao.save(order);

        order.setOrderStatus(OrderStatus.MEAL.toString());
        order = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.MEAL.toString());
    }

    @Test
    @DisplayName("완료된 주문의 상태는 변경할 수 없다.")
    void updateCompletionOrderException() {
        Order order = OrderTest.ofOneHalfAndHalfInSingleTable();
        order.setOrderStatus(OrderStatus.COMPLETION.toString());

        assertThrows(IllegalArgumentException.class,
                () -> orderBo.changeOrderStatus(order.getId(), order));
    }

    @Test
    @DisplayName("주문은 하나 이상의 항목을 갖는다.")
    void createWithoutAnyMenuProductException() {
        Order order = OrderTest.ofOneHalfAndHalfInSingleTable();
        order.setOrderLineItems(new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문이 생성되면 주문은 요리중 상태를 갖는다.")
    void createOrderHavingCookingStatusTest() {
        Order order = OrderTest.ofOneHalfAndHalfInSingleTable();
        order = orderBo.create(order);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
    }

    @Test
    @DisplayName("전체 주문 목록을 조회할 수 있다.")
    void readAllOrderListTest() {
        Order order = OrderTest.ofOneHalfAndHalfInSingleTable();
        order = orderDao.save(order);

        assertThat(orderBo.list()).contains(order);
    }
}
