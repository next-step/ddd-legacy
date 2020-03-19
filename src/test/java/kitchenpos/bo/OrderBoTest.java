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
    private final MenuDao menuDao = new InMemoryMenuDao();
    private final OrderDao orderDao = new InMemoryOrderDao();
    private final OrderLineItemDao orderLineItemDao = new InMemoryOrderLineItemDao();
    private final OrderTableDao orderTableDao = new InMemoryOrderTableDao();
    private final MenuGroupDao menuGroupDao = new InMemoryMenuGroupDao();
    private final MenuProductDao menuProductDao = new InMemoryMenuProductDao();
    private final ProductDao productDao = new InMemoryProductDao();

    private final OrderBo orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);

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
        final Order order = OrderTest.of();
        final Order orderResult = orderBo.create(order);
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
        final Order withoutOrderTable = OrderTest.of();
        withoutOrderTable.setOrderTableId(null);
        assertThrows(IllegalArgumentException.class, () -> orderBo.create(withoutOrderTable));
    }

    @Test
    @DisplayName("비어있는 테이블에는 주문을 생성할 수 없다.")
    void createOrderWithEmptyTableException() {
        final OrderTable orderTable = OrderTableTest.ofEmpty();
        orderTable.setEmpty(true);
        final OrderTable orderTableResult = orderTableDao.save(orderTable);

        final Order withEmptyTable = OrderTest.of();
        withEmptyTable.setOrderTableId(orderTableResult.getId());

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(orderDao.save(withEmptyTable)));
    }

    @Test
    @DisplayName("주문의 상태는 변경할 수 있다.")
    void updateOrderStatusTest() {
        final Order order = orderDao.save(OrderTest.of());
        order.setOrderStatus(OrderStatus.MEAL.toString());

        final Order orderResult = orderBo.changeOrderStatus(order.getId(), order);
        assertThat(orderResult.getOrderStatus()).isEqualTo(OrderStatus.MEAL.toString());
    }

    @Test
    @DisplayName("완료된 주문의 상태는 변경할 수 없다.")
    void updateCompletionOrderException() {
        final Order order = orderDao.save(OrderTest.ofCompleted());

        final Order cookingOrder = OrderTest.ofCompleted();
        cookingOrder.setOrderStatus(OrderStatus.COOKING.toString());

        assertThrows(IllegalArgumentException.class,
                () -> orderBo.changeOrderStatus(order.getId(), cookingOrder));
    }

    @Test
    @DisplayName("주문은 하나 이상의 항목을 갖는다.")
    void createWithoutAnyMenuProductException() {
        final Order order = OrderTest.of();
        order.setOrderLineItems(new ArrayList<>());

        assertThrows(IllegalArgumentException.class,
                () -> orderBo.create(order));
    }

    @Test
    @DisplayName("주문이 생성되면 주문은 요리중 상태를 갖는다.")
    void createOrderHavingCookingStatusTest() {
        final Order order = OrderTest.of();
        order.setOrderStatus(null);
        final Order orderResult = orderBo.create(order);

        assertThat(orderResult.getOrderStatus()).isEqualTo(OrderStatus.COOKING.toString());
    }

    @Test
    @DisplayName("전체 주문 목록을 조회할 수 있다.")
    void readAllOrderListTest() {
        final Order order = OrderTest.of();
        final Order orderResult = orderDao.save(order);

        assertThat(orderBo.list()).contains(orderResult);
    }
}
