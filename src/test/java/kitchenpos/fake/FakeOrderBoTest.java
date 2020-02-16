package kitchenpos.fake;

import kitchenpos.bo.OrderBo;
import kitchenpos.builder.*;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setId(1L)
                .setOrderLineItems(orderLineItems)
                .setOrderTableId(1L)
                .build()
                ;

        MenuProduct menuProduct = new MenuProductBuilder()
                .setMenuId(1L)
                .setQuantity(2)
                .setSeq(1L)
                .setProductId(1L)
                .build();

        Menu menu1 = new MenuBuilder()
                .setId(1L)
                .setMenuGroupId(1L)
                .setMenuProducts(Arrays.asList(menuProduct))
                .setPrice(BigDecimal.TEN)
                .setName("후라이드 치킨")
                .build()
                ;

        Menu menu2 = new MenuBuilder()
                .setId(2L)
                .setMenuGroupId(1L)
                .setMenuProducts(Arrays.asList(menuProduct))
                .setPrice(BigDecimal.TEN)
                .setName("후라이드 치킨")
                .build()
                ;

        OrderTable orderTable = new OrderTableBuilder()
                .setEmpty(false)
                .setId(1L)
                .setNumberOfGuests(4)
                .build();

        orderTableDao.save(orderTable);

        menuDao.save(menu1);
        menuDao.save(menu2);

        orderLineItemDao.save(orderLineItem1);
        orderLineItemDao.save(orderLineItem2);

        Order order = orderBo.create(requestOrder);

        assertThat(order.getId()).isEqualTo(requestOrder.getId());
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
    }

    @DisplayName("상세 주문 내역이 존재 하지 않을시 에러")
    @Test
    void createFailByNotExistOrderLineItem() {
        Order requestOrder = new OrderBuilder()
                .setOrderTableId(1L)
                .build()
                ;

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("등록되어 있지 않은 메뉴가 주문 내역에 포함 되어 있을 시 에러")
    @Test
    void createFailByNotInsertedOrderLineItem() {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setOrderLineItems(orderLineItems)
                .setOrderTableId(1L)
                .build()
                ;

        MenuProduct menuProduct = new MenuProductBuilder()
                .setMenuId(1L)
                .setQuantity(2)
                .setSeq(1L)
                .setProductId(1L)
                .build();

        Menu menu1 = new MenuBuilder()
                .setId(1L)
                .setMenuGroupId(1L)
                .setMenuProducts(Arrays.asList(menuProduct))
                .setPrice(BigDecimal.TEN)
                .setName("후라이드 치킨")
                .build()
                ;


        menuDao.save(menu1);

        orderLineItemDao.save(orderLineItem1);
        orderLineItemDao.save(orderLineItem2);


        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("존재 하지 않는 테이블이거나 테이블 정보가 없을때 일때 에러")
    @Test
    void createFailByNotExistTable() {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setOrderLineItems(orderLineItems)
                .setOrderTableId(1L)
                .build()
                ;

        MenuProduct menuProduct = new MenuProductBuilder()
                .setMenuId(1L)
                .setQuantity(2)
                .setSeq(1L)
                .setProductId(1L)
                .build();

        Menu menu1 = new MenuBuilder()
                .setId(1L)
                .setMenuGroupId(1L)
                .setMenuProducts(Arrays.asList(menuProduct))
                .setPrice(BigDecimal.TEN)
                .setName("후라이드 치킨")
                .build()
                ;

        Menu menu2 = new MenuBuilder()
                .setId(2L)
                .setMenuGroupId(1L)
                .setMenuProducts(Arrays.asList(menuProduct))
                .setPrice(BigDecimal.TEN)
                .setName("양념 치킨")
                .build()
                ;

        menuDao.save(menu1);
        menuDao.save(menu2);

        orderLineItemDao.save(orderLineItem1);
        orderLineItemDao.save(orderLineItem2);

        assertThrows(IllegalArgumentException.class, () -> orderBo.create(requestOrder));
    }

    @DisplayName("주문받은 음식의 상태 변경")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void changeOrderStatus(String value) {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus(value)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        Order savedOrder = new OrderBuilder()
                .setId(1L)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        orderDao.save(requestOrder);

        Order changedOrder = orderBo.changeOrderStatus(requestOrder.getId(), requestOrder);

        assertThat(changedOrder.getId()).isEqualTo(savedOrder.getId());
        assertThat(changedOrder.getOrderStatus()).isEqualTo(requestOrder.getOrderStatus());
    }

    @DisplayName("COMPLETE 상태의 주문 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"COOKING", "MEAL"})
    void changeOrderStatusFailByComplete(String value) {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus(value)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        Order savedOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus("COMPLETION")
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        orderDao.save(savedOrder);

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }

    @DisplayName("등록되지 않은 주문 상태 변경시 에러")
    @ParameterizedTest
    @ValueSource(strings = {"MEAL"})
    void changeOrderStatusFailByNotInsert(String value) {
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
                .setOrderId(1L)
                .setQuantity(3)
                .setMenuId(1L)
                .setSeq(1L)
                .build()
                ;

        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
                .setOrderId(2L)
                .setQuantity(1)
                .setMenuId(2L)
                .setSeq(2L)
                .build()
                ;

        List<OrderLineItem> orderLineItems = Arrays.asList(orderLineItem1, orderLineItem2);

        Order requestOrder = new OrderBuilder()
                .setId(1L)
                .setOrderStatus(value)
                .setOrderLineItems(orderLineItems)
                .build()
                ;

        assertThrows(IllegalArgumentException.class, () -> orderBo.changeOrderStatus(requestOrder.getId(), requestOrder));
    }
}
