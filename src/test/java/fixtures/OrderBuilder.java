package fixtures;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderBuilder {

    private UUID id = UUID.randomUUID();
    private OrderType orderType = OrderType.EAT_IN;
    private OrderStatus orderStatus = OrderStatus.WAITING;
    private LocalDateTime orderDate = LocalDateTime.now();
    private List<OrderLineItem> orderLineItems = new ArrayList<>();
    private String deliveryAddress = "address";
    private OrderTable orderTable = new OrderTable();

    public OrderBuilder withOrderType(OrderType orderType) {
        this.orderType = orderType;
        return this;
    }

    public OrderBuilder withOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public OrderBuilder withDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderBuilder withOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        return this;
    }

    public OrderBuilder withOrderTable(OrderTable orderTable) {
        this.orderTable = orderTable;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderDateTime(orderDate);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }

}
