package kitchenpos.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.OrderLineItemFixture.CHICKEN_ORDER_LINE;

public class OrderFixture {

    public static Order DELIVERY_CHICKEN_ORDER =
        OrderFixture.builder()
                    .id(UUID.randomUUID())
                    .type(OrderType.DELIVERY)
                    .status(OrderStatus.WAITING)
                    .orderDateTime(LocalDateTime.now())
                    .orderLineItems(Collections.singletonList(CHICKEN_ORDER_LINE))
                    .deliveryAddress("address")
                    .build();

    private UUID id;
    private OrderType type;
    private OrderStatus status;
    private LocalDateTime orderDateTime;
    private List<OrderLineItem> orderLineItems;
    private String deliveryAddress;
    private OrderTable orderTable;
    private UUID orderTableId;

    public static OrderFixture builder() {
        return new OrderFixture();
    }

    public OrderFixture id(UUID id) {
        this.id = id;
        return this;
    }

    public OrderFixture type(OrderType type) {
        this.type = type;
        return this;
    }

    public OrderFixture status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderFixture orderDateTime(LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
        return this;
    }

    public OrderFixture orderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
        return this;
    }

    public OrderFixture deliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderFixture orderTable(OrderTable orderTable) {
        this.orderTable = orderTable;
        return this;
    }

    public OrderFixture orderTableId(UUID orderTableId) {
        this.orderTableId = orderTableId;
        return this;
    }

    public Order build() {
        Order order = new Order();
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
