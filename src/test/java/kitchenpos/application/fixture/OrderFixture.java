package kitchenpos.application.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static final OrderTable DEFAULT_ORDER_TABLE = makeOrderTable(
            "기본테이블", 1
    );

    public static final Order DEFAULT_ORDER = makeOrder(
            OrderType.EAT_IN,
            OrderLineItemFixture.DEFAULT_ORDER_LINE_ITEM
    );

    public static final Order DEFAULT_ORDER_2 = makeOrder(
            OrderType.EAT_IN,
            OrderLineItemFixture.DEFAULT_ORDER_LINE_ITEM,
            DEFAULT_ORDER_TABLE
    );

    public static Order makeOrder(OrderType type,  List<OrderLineItem> orderLIneItems){
        return makeOrder(type, orderLIneItems, DEFAULT_ORDER_TABLE);
    }

    public static Order makeOrder(OrderType type,  List<OrderLineItem> orderLIneItems, OrderTable orderTable){
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLIneItems);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }

    public static OrderTable makeOrderTable(String name, int guestCnt){
        OrderTable table = new OrderTable();
        table.setId(UUID.randomUUID());
        table.setName(name);
        table.setNumberOfGuests(guestCnt);
        table.setOccupied(true);
        return table;
    }
}
