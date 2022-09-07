package kitchenpos.order;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderFixture {
    public static Order order(OrderStatus status, OrderType type, List<OrderLineItem> orderLineItems, UUID orderTableId) {
        Order order = new Order(type, orderLineItems, null, orderTableId);
        order.setStatus(status);
        return order;
    }

    public static Order order(List<OrderLineItem> orderLineItems, UUID orderTableId) {
        return new Order(OrderType.EAT_IN, orderLineItems, null, orderTableId);
    }

    public static Order order(OrderType type, List<OrderLineItem> orderLineItems) {
        return order(type, orderLineItems, null);
    }

    public static Order order(OrderType type, List<OrderLineItem> orderLineItems, String address) {
        return new Order(type, orderLineItems, address, null);
    }

    public static OrderLineItem orderLineItem(Menu menu, long quantity, int price) {
        return new OrderLineItem(menu, quantity, menu.getId(), BigDecimal.valueOf(price));
    }
}
