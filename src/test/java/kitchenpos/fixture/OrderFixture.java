package kitchenpos.fixture;

import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    public static Order createTakeOutRequest(OrderLineItem... orderLineItem) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderLineItems(List.of(orderLineItem));

        return order;
    }

    public static Order createEatInRequest(final OrderTable orderTable,
            OrderLineItem... orderLineItem) {
        Order order = new Order();
        order.setType(OrderType.EAT_IN);
        if (orderTable != null) {
            order.setOrderTableId(orderTable.getId());
        }
        order.setOrderLineItems(List.of(orderLineItem));

        return order;
    }

    public static Order createDeliveryRequest(final String deliveryAddress,
            OrderLineItem... orderLineItem) {
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        if (deliveryAddress != null) {
            order.setDeliveryAddress(deliveryAddress);
        }
        order.setOrderLineItems(List.of(orderLineItem));

        return order;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, Integer quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static Order createEatInOrder(OrderTable orderTable, Menu menu) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setOrderTable(orderTable);
        order.setOrderLineItems(List.of(createOrderLineItem(menu, 2)));
        order.setStatus(OrderStatus.WAITING);
        return order;
    }
}
