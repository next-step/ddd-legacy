package kitchenpos.util;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderFactory {
    private OrderFactory() {
    }

    public static Order createOrder(UUID id, OrderType orderType, List<OrderLineItem> orderLineItems, UUID orderTableId, String deliveryAddress) {
        Order order = new Order();
        order.setId(id);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setType(orderType);
        return order;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, Integer quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }
}
