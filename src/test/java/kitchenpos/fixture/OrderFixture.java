package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static OrderTable 주문_테이블_생성(String name) {
        return 주문_테이블_생성(name, 0);
    }

    public static OrderTable 주문_테이블_생성(String name, int numberOfGuests) {
        return 주문_테이블_생성(UUID.randomUUID(), name, numberOfGuests, false);
    }

    public static OrderTable 주문_테이블_생성(UUID id, String name, int numberOfGuests, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderLineItem 주문_상품_생성(Menu menu, BigDecimal price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenu(menu);
        return orderLineItem;
    }

    public static Order 배달_주문_생성(String address, List<OrderLineItem> orderLineItem) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(orderLineItem);
        order.setDeliveryAddress(address);
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order 포장_주문_생성(List<OrderLineItem> orderLineItem) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(orderLineItem);
        order.setStatus(OrderStatus.WAITING);
        return order;
    }

    public static Order 매장_주문_생성(OrderTable orderTable, List<OrderLineItem> orderLineItem) {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(orderLineItem);
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);
        order.setStatus(OrderStatus.WAITING);
        return order;
    }
}
