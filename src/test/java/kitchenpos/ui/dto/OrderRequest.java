package kitchenpos.ui.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderRequest {

    private UUID id;
    private OrderType type;
    private OrderStatus status;
    private LocalDateTime orderDateTime;

    private List<OrderLineItemRequest> orderLineItems;

    private String deliveryAddress;

    private OrderTableRequest orderTable;

    private UUID orderTableId;

    public OrderRequest(final OrderType type, final OrderTable orderTable,
        final OrderLineItemRequest... orderLineItems) {
        this.type = type;
        this.orderTable = new OrderTableRequest(orderTable);
        this.orderLineItems = Arrays.asList(orderLineItems);

        this.orderTableId = orderTable.getId();
    }

    public OrderRequest(final OrderType type, final String deliveryAddress,
        final OrderLineItemRequest... orderLineItems) {
        this.type = type;
        this.deliveryAddress = deliveryAddress;
        this.orderLineItems = Arrays.asList(orderLineItems);
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(final OrderType type) {
        this.type = type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(final LocalDateTime orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public List<OrderLineItemRequest> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(final List<OrderLineItemRequest> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(final String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public OrderTableRequest getOrderTable() {
        return orderTable;
    }

    public void setOrderTable(final OrderTableRequest orderTable) {
        this.orderTable = orderTable;
    }

    public void setOrderTable(final OrderTable orderTable) {
        this.orderTable = new OrderTableRequest(orderTable);
    }

    public UUID getOrderTableId() {
        return orderTableId;
    }

    public void setOrderTableId(final UUID orderTableId) {
        this.orderTableId = orderTableId;
    }
}
