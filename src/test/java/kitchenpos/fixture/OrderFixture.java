package kitchenpos.fixture;


import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    public static Order order(OrderType type, OrderStatus status, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order deliveryOrder(String address, List<OrderLineItem> orderLineItems) {
        Order order = order(OrderType.DELIVERY, OrderStatus.WAITING, orderLineItems);
        order.setDeliveryAddress(address);
        return order;
    }

    public static Order eatInOrder(UUID orderTableId, List<OrderLineItem> orderLineItems) {
        Order order = order(OrderType.EAT_IN, OrderStatus.WAITING, orderLineItems);
        order.setOrderTableId(orderTableId);
        return order;
    }

    public static Order takeoutOrder(List<OrderLineItem> orderLineItems) {
        return order(OrderType.TAKEOUT, OrderStatus.WAITING, orderLineItems);
    }

    public static OrderLineItem orderLineItem(Menu menu, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static OrderLineItem orderLineItem(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
