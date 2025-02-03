package kitchenpos.application.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order createOrder(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress, UUID orderTableId, LocalDateTime orderDateTime) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableId);
        order.setOrderDateTime(orderDateTime);
        return order;
    }

    public static OrderLineItem createOrderLineItem(UUID menuId, BigDecimal price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    private OrderFixture() {
    }
}
