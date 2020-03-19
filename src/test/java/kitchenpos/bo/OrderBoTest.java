package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderBoTest {

    private MenuDao menuDao = new InMemoryMenuDao();
    private OrderDao orderDao = new InMemoryOrderDao();
    private OrderLineItemDao orderLineItemDao = new InMemoryOrderLineItemDao();
    private OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    private MenuProductDao menuProductDao = new InMemoryMenuProductDao();
    private ProductDao productDao = new InMemoryProductDao();

    private OrderBo orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);

    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        menuGroupDao.save(MenuGroupTest.ofSet());

        productDao.save(ProductTest.ofHalfFried());
        productDao.save(ProductTest.ofHalfChilly());

        menuProductDao.save(MenuProductTest.ofHalfFriedProduct());
        menuProductDao.save(MenuProductTest.ofHalfChillyProduct());

        menuDao.save(MenuTest.ofHalfAndHalf());

        orderTable = OrderTableTest.of();
        orderTable = orderTableDao.save(orderTable);

        orderLineItemDao.save(OrderLineItemTest.ofSingle());
    }

    @Test
    @DisplayName("주문을 생성할 수 있다.")
    void createTest() {
        Order order = OrderTest.of();
        Order orderResult = orderBo.create(order);
        assertAll(
                () -> assertThat(orderResult.getId()).isEqualTo(order.getId()),
                () -> assertThat(orderResult.getOrderStatus()).isEqualTo(order.getOrderStatus()),
                () -> assertThat(orderResult.getOrderTableId()).isEqualTo(order.getOrderTableId()),
                () -> assertThat(orderResult.getOrderLineItems()).containsOnlyElementsOf(order.getOrderLineItems())
        );
    }

    @Test
    @DisplayName("테이블이 할당되지 않으면 주문을 생성할 수 없다.")
    void createOrderWithoutTableException() {
        Order withoutOrderTable = OrderTest.of();
        withoutOrderTable.setOrderTableId(null);
        assertThrows(IllegalArgumentException.class, () -> orderBo.create(withoutOrderTable));
    }

    @Test
    @DisplayName("비어있는 테이블에는 주문을 생성할 수 없다.")
    void createOrderWithEmptyTableException() {
        OrderTable orderTable = OrderTableTest.ofEmpty();
        orderTable.setEmpty(true);
        orderTable = orderTableDao.save(orderTable);

        Order withEmptyTable = OrderTest.of();
        withEmptyTable.setOrderTableId(orderTable.getId());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(orderDao.save(withEmptyTable)));
    }

    @Test
    @DisplayName("주문의 상태는 변경할 수 있다.")
    void updateOrderStatusTest() {
        Order order = orderDao.save(OrderTest.of());
        order.setOrderStatus(OrderStatus.MEAL.toString());

        order = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.MEAL.toString());
    }

    @Test
    @DisplayName("완료된 주문의 상태는 변경할 수 없다.")
    void updateCompletionOrderException() {
        Order order = orderDao.save(OrderTest.ofCompleted());

        Order cookingOrder = OrderTest.ofCompleted();
        cookingOrder.setOrderStatus(OrderStatus.COOKING.toString());

        assertThrows(IllegalArgumentException.class,
                () -> orderBo.changeOrderStatus(order.getId(), cookingOrder));
    }

    @Test
    @DisplayName("주문은 하나 이상의 항목을 갖는다.")
    void createWithoutAnyMenuProductException() {
        Order order = OrderTest.of();
        order.setOrderLineItems(new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문이 생성되면 주문은 요리중 상태를 갖는다.")
    void createOrderHavingCookingStatusTest() {
        Order order = OrderTest.of();
        order.setOrderStatus(null);
        order = orderBo.create(order);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
    }

    @Test
    @DisplayName("전체 주문 목록을 조회할 수 있다.")
    void readAllOrderListTest() {
        Order order = OrderTest.of();
        order = orderDao.save(order);

        assertThat(orderBo.list()).contains(order);
    }
}
