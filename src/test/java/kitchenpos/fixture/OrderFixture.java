package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    private OrderFixture() {
    }

    public static Order 주문_생성(OrderType orderType) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setType(orderType);

        return order;
    }

    public static Order 주문_생성(OrderType orderType, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.WAITING);
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);

        return order;
    }

    public static OrderLineItem 주문_항목_생성(UUID menuId) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(new BigDecimal(10_000L));

        return orderLineItem;
    }

    public static OrderLineItem 주문_항목_생성(UUID menuId, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(new BigDecimal(10_000L));

        return orderLineItem;
    }

    public static OrderLineItem 주문_항목_생성(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);

        return orderLineItem;
    }

    public static OrderTable 주문_테이블_생성() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(0);
        orderTable.setName("테이블");
        orderTable.setOccupied(false);

        return orderTable;
    }
}
