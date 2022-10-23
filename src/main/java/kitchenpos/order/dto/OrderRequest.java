package kitchenpos.order.dto;

import kitchenpos.order.domain.OrderType;

import java.util.List;
import java.util.UUID;

public class OrderRequest {

    private OrderType type;
    private List<OrderLineItemRequest> orderLineItems;
    private UUID id;
    private String deliveryAddress;

    public OrderRequest(List<OrderLineItemRequest> orderLineItemRequests, OrderType type) {
        this.orderLineItems = orderLineItemRequests;
        this.type = type;
    }

    public OrderType getType() {
        return this.type;
    }

    public List<OrderLineItemRequest> getOrderLineItems() {
        return this.orderLineItems;
    }

    public UUID getOrderTableId() {
        return this.id;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }
}
