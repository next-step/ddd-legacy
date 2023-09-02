package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixtures {
    public static Order createOrder(
            String deliveryAddress,
            OrderTable orderTable,
            LocalDateTime orderDateTime,
            OrderStatus orderStatus,
            OrderType orderType,
            List<OrderLineItem> orderLineItems
    ) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderDateTime(orderDateTime);
        order.setStatus(orderStatus);
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, Long quantity, BigDecimal price, Long seq) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        orderLineItem.setSeq(seq);
        return orderLineItem;
    }
}
