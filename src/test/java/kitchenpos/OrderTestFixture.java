package kitchenpos;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderTestFixture {
    private OrderTestFixture() {
    }

    public static OrderTable createOrderTableRequest(String name) {
        OrderTable table = new OrderTable();
        table.setName(name);
        return table;
    }

    public static OrderTable getSavedOrderTable(OrderTableService orderTableService, String name) {
        return orderTableService.create(createOrderTableRequest(name));
    }

    public static OrderTable changeOrderTableRequest(int numberOfGuest) {
        OrderTable table = new OrderTable();
        table.setNumberOfGuests(numberOfGuest);
        return table;
    }

    public static Order createDeliveryOrderRequest(String deliveryAddress, List<OrderLineItem> orderLineItems) {
        Order order = createOrderRequest(OrderType.DELIVERY);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createEatInOrderRequest(UUID orderTableId, List<OrderLineItem> orderLineItems) {
        Order order = createOrderRequest(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createTakeoutOrderRequest(List<OrderLineItem> orderLineItems) {
        Order order = createOrderRequest(OrderType.TAKEOUT);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static OrderLineItem createOrderLineItemRequest(UUID menuId, BigDecimal price, int quantity) {
        OrderLineItem item = new OrderLineItem();
        item.setQuantity(quantity);
        item.setMenuId(menuId);
        item.setPrice(price);
        return item;

    }

    public static Order createOrderRequest(OrderType type) {
        Order order = new Order();
        order.setType(type);
        return order;
    }
}
