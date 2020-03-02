package kitchenpos.fake;

import kitchenpos.TestFixture;
import kitchenpos.bo.OrderBo;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderLineItemDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FakeOrderBoTest {

    private OrderBo orderBo;

    private MenuDao menuDao = new FakeMenuDao();

    private OrderDao orderDao = new FakeOrderDao();

    private OrderLineItemDao orderLineItemDao = new FakeOrderLineItemDao();

    private OrderTableDao orderTableDao = new FakeOrderTableDao();

    @BeforeEach
    void setUp() {
        this.orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);
    }

    @DisplayName("새로운 주문 등록")
    @Test
    void create() {
        Order requestOrder = TestFixture.generateOrderOne();

        Menu menu1 = TestFixture.generateMenuOne();
        Menu menu2 = TestFixture.generateMenuTwo();

        OrderTable orderTable = TestFixture.generateOrderTableNotEmpty();

        orderTableDao.save(orderTable);

        menuDao.save(menu1);
        menuDao.save(menu2);

        Order order = orderBo.create(requestOrder);

        assertAll(
                () -> assertThat(order.getId()).isEqualTo(order.getId()),
                () -> assertThat(order.getOrderLineItems()).containsAll(order.getOrderLineItems()),
                () -> assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name())
        );
    }

    @DisplayName("상세 주문 내역이 존재 하지 않을시 에러")
    @Test
    void createFailByNotExistOrderLineItem() {
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderLineItems(null);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("등록되어 있지 않은 메뉴가 주문 내역에 포함 되어 있을 시 에러")
    @Test
    void createFailByNotInsertedOrderLineItem() {
        OrderLineItem orderLineItem1 = TestFixture.generateOrderLineItemOne();
        OrderLineItem orderLineItem2 = TestFixture.generateOrderLineItemTwo();

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderLineItems(orderLineItems);

        Menu menu = TestFixture.generateMenuOne();

        menuDao.save(menu);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("존재 하지 않는 테이블이거나 테이블 정보가 없을때 일때 에러")
    @Test
    void createFailByNotExistTable() {
        OrderLineItem orderLineItem = TestFixture.generateOrderLineItemOne();

        Order requestOrder = TestFixture.generateOrderOne();

        Menu menu = TestFixture.generateMenuOne();

        menuDao.save(menu);

        orderLineItemDao.save(orderLineItem);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("주문받은 음식의 상태 변경")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void changeOrderStatus(String orderStatus) {
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderStatus(orderStatus);

        Order savedOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderStatus(OrderStatus.COOKING.name());

        orderDao.save(savedOrder);

        Order changedOrder = orderBo.changeOrderStatus(requestOrder.getId(), requestOrder);

        assertAll(
                () -> assertThat(changedOrder.getId()).isEqualTo(savedOrder.getId()),
                () -> assertThat(changedOrder.getOrderStatus()).isEqualTo(requestOrder.getOrderStatus())
        );
    }

    @DisplayName("COMPLETE 상태의 주문 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void changeOrderStatusFailByComplete(String orderStatus) {
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderStatus(orderStatus);

        Order savedOrder = TestFixture.generateOrderOne();
        savedOrder.setOrderStatus(OrderStatus.COMPLETION.name());

        orderDao.save(savedOrder);

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }

    @DisplayName("등록되지 않은 주문 상태 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"MEAL"})
    void changeOrderStatusFailByNotInsert(String orderStatus) {
        Order requestOrder = TestFixture.generateOrderOne();
        requestOrder.setOrderStatus(orderStatus);

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }
}
