package kitchenpos.bo;

import kitchenpos.dao.*;
import kitchenpos.model.Menu;
import kitchenpos.model.Order;
import kitchenpos.model.OrderLineItem;
import kitchenpos.model.OrderTable;
import kitchenpos.support.MenuBuilder;
import kitchenpos.support.OrderBuilder;
import kitchenpos.support.OrderLineItemBuilder;
import kitchenpos.support.OrderTableBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InMemoryOrderBoTest {
    private final MenuDao menuDao = new InMemoryMenuDao();
    private final OrderDao orderDao = new InMemoryOrderDao();
    private final OrderLineItemDao orderLineItemDao = new InMemoryOrderLineItemDao();
    private final OrderTableDao orderTableDao = new InMemoryOrderTableDao();

    private OrderBo orderBo;

    @BeforeEach
    void setup (){
        orderBo = new OrderBo(menuDao, orderDao, orderLineItemDao, orderTableDao);
    }

    @DisplayName("OrderLineItem이 비어있으면 안된다.")
    @Test
    void createWitoutOrderLineItem (){
        Order order = new OrderBuilder()
            .id(1L)
            .orderLineItems(new ArrayList<>())
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("OrderLineItem의 메뉴에 중복이 있으면 안된다.")
    @Test
    void createNotDuplicateMenu (){
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
            .seq(1L)
            .menuId(1L)
            .build();
        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
            .seq(2L)
            .menuId(1L)
            .build();

        Order order = new OrderBuilder()
            .id(1L)
            .orderLineItems(new ArrayList<>())
            .build();
        order.addOrderLineItem(orderLineItem1);
        order.addOrderLineItem(orderLineItem2);

        Menu menu = new MenuBuilder()
            .id(1L)
            .build();
        menuDao.save(menu);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문에 잘못된 주문테이블을 입력하면 안된다.")
    @Test
    void createWrongOrderTable (){
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
            .seq(1L)
            .menuId(1L)
            .build();
        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
            .seq(2L)
            .menuId(1L)
            .build();

        Order order = new OrderBuilder()
            .id(1L)
            .orderTableId(2L)
            .orderLineItems(new ArrayList<>())
            .build();
        order.addOrderLineItem(orderLineItem1);
        order.addOrderLineItem(orderLineItem2);

        Menu menu1 = new MenuBuilder()
            .id(1L)
            .build();
        Menu menu2 = new MenuBuilder()
            .id(2L)
            .build();

        menuDao.save(menu1);
        menuDao.save(menu2);

        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .build();

        orderTableDao.save(orderTable);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문테이블이 비어있으면 안된다.")
    @Test
    void createOrderTableIsEmpty(){
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
            .seq(1L)
            .menuId(1L)
            .build();
        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
            .seq(2L)
            .menuId(1L)
            .build();

        Order order = new OrderBuilder()
            .id(1L)
            .orderTableId(1L)
            .orderLineItems(new ArrayList<>())
            .build();
        order.addOrderLineItem(orderLineItem1);
        order.addOrderLineItem(orderLineItem2);

        Menu menu1 = new MenuBuilder()
            .id(1L)
            .build();
        Menu menu2 = new MenuBuilder()
            .id(2L)
            .build();

        menuDao.save(menu1);
        menuDao.save(menu2);

        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .empty(true)
            .build();

        orderTableDao.save(orderTable);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.create(order));
    }

    @DisplayName("주문을 생성한다.")
    @Test
    void create(){
        OrderLineItem orderLineItem1 = new OrderLineItemBuilder()
            .seq(1L)
            .menuId(1L)
            .orderId(1L)
            .build();
        OrderLineItem orderLineItem2 = new OrderLineItemBuilder()
            .seq(2L)
            .menuId(1L)
            .orderId(1L)
            .build();

        Order order = new OrderBuilder()
            .id(1L)
            .orderTableId(1L)
            .orderLineItems(new ArrayList<>())
            .build();
        order.addOrderLineItem(orderLineItem1);
        order.addOrderLineItem(orderLineItem2);

        Menu menu1 = new MenuBuilder()
            .id(1L)
            .build();
        Menu menu2 = new MenuBuilder()
            .id(2L)
            .build();

        menuDao.save(menu1);
        menuDao.save(menu2);

        OrderTable orderTable = new OrderTableBuilder()
            .id(1L)
            .empty(false)
            .build();

        orderTableDao.save(orderTable);

        Order savedOrder = orderBo.create(order);

        Order expectedOrder = new OrderBuilder()
            .id(order.getId())
            .orderTableId(orderTable.getId())
            .orderStatus("COOKING")
            .orderedTime(savedOrder.getOrderedTime())
            .orderLineItems(new ArrayList<>())
            .build();

        expectedOrder.addOrderLineItem(orderLineItem1);
        expectedOrder.addOrderLineItem(orderLineItem2);

        Assertions.assertThat(savedOrder).isEqualToComparingFieldByField(expectedOrder);
    }

    @DisplayName("잘못된 OrderId로 요청하면 안된다.")
    @Test
    void changeOrderStatusWrongOrderId(){
        orderDao.save(
            new OrderBuilder()
                .id(1L)
                .build()
        );

        Order order = new OrderBuilder()
            .id(2L)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), order));
    }

    @DisplayName("식사완료 일 땐 주문상태를 변경 할 수 없다.")
    @Test
    void changeOrderStatusWhenComplition (){
        orderDao.save(
            new OrderBuilder()
            .id(1L)
            .orderStatus("COMPLETION")
            .build()
        );

        Order order = new OrderBuilder()
            .id(1L)
            .build();

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderBo.changeOrderStatus(order.getId(), order));
    }

    @DisplayName("주문상태를 변경 할 수 있다.")
    @Test
    void changeOrderStatus (){
        orderDao.save(
            new OrderBuilder()
                .id(1L)
                .orderStatus("COOKING")
                .build()
        );

        Order order = new OrderBuilder()
            .id(1L)
            .orderStatus("MEAL")
            .build();

        Order savedOrder = orderBo.changeOrderStatus(order.getId(), order);

        assertThat(savedOrder.getOrderStatus()).isEqualTo("MEAL");
    }
}
