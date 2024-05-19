package kitchenpos.application;

import kitchenpos.domain.*;

import java.util.List;

public class OrderRequestBuilder {

    private OrderType type;
    private List<OrderLineItem> orderLineItemRequests;
    private OrderStatus status;
    private String deliveryAddress;
    private OrderTable orderTable;

    public static OrderRequestBuilder eatInOrderRequest(OrderTable orderTable, List<OrderLineItem> orderLineItemRequests) {
        final OrderRequestBuilder orderRequestBuilder = new OrderRequestBuilder();
        orderRequestBuilder.type = OrderType.EAT_IN;
        orderRequestBuilder.orderTable = orderTable;
        orderRequestBuilder.orderLineItemRequests = orderLineItemRequests;
        return orderRequestBuilder;
    }

    public static OrderRequestBuilder deliveryOrderRequest(String deliveryAddress, List<OrderLineItem> orderLineItemRequests) {
        final OrderRequestBuilder orderRequestBuilder = new OrderRequestBuilder();
        orderRequestBuilder.type = OrderType.DELIVERY;
        orderRequestBuilder.deliveryAddress = deliveryAddress;
        orderRequestBuilder.orderLineItemRequests = orderLineItemRequests;
        return orderRequestBuilder;
    }

    public static OrderRequestBuilder takeoutOrderRequest(List<OrderLineItem> orderLineItemRequests) {
        final OrderRequestBuilder orderRequestBuilder = new OrderRequestBuilder();
        orderRequestBuilder.type = OrderType.TAKEOUT;
        orderRequestBuilder.orderLineItemRequests = orderLineItemRequests;
        return orderRequestBuilder;
    }

    public OrderRequestBuilder withType(OrderType type) {
        this.type = type;
        return this;
    }

    public OrderRequestBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderRequestBuilder withDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
        return this;
    }

    public OrderRequestBuilder withOrderTable(OrderTable orderTable) {
        this.orderTable = orderTable;
        return this;
    }

    public OrderRequestBuilder withOrderLineItemRequests(List<OrderLineItem> orderLineItemRequests) {
        this.orderLineItemRequests = orderLineItemRequests;
        return this;
    }

    public Order build() {
        final Order orderRequest = new Order();
        orderRequest.setType(type);
        orderRequest.setOrderLineItems(orderLineItemRequests);
        orderRequest.setStatus(status);
        orderRequest.setDeliveryAddress(deliveryAddress);
        orderRequest.setOrderTable(orderTable);
        orderRequest.setOrderTableId(orderTable != null ? orderTable.getId() : null);
        return orderRequest;
    }
}
