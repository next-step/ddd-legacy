package kitchenpos.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderBuilder {
    private UUID id;
    private OrderType type;
    private OrderStatus status;
    private LocalDateTime orderDateTime;
    private List<OrderLineItem> orderLineItems;
    private String deliveryAddress;
    private OrderTable orderTable;
    private UUID orderTableId;

    public OrderBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public OrderBuilder type(OrderType type) {
        this.type = type;
        return this;
    }

    public OrderBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder orderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
        return this;
    }

    public OrderBuilder orderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        return this;
    }

    public OrderBuilder deliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderBuilder orderTable(OrderTable orderTable) {
        this.orderTable = orderTable;
        return this;
    }

    public OrderBuilder orderTableId(UUID orderTableId) {
        this.orderTableId = orderTableId;
        return this;
    }


    public Order build() {
        Order order = new Order();
        if (id == null) {
            id = UUID.randomUUID();
        }
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        return order;
    }

}
