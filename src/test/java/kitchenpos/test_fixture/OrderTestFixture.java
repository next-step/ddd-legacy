package kitchenpos.test_fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class OrderTestFixture {
    private Order order;

    public OrderTestFixture(Order order) {
        this.order = order;
    }

    public static OrderTestFixture create() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.ACCEPTED);
        OrderTable orderTable = OrderTableTestFixture.create().getOrderTable();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setDeliveryAddress("서울시 강남구 역삼동");
        order.setType(OrderType.EAT_IN);
        order.setOrderLineItems(Collections.singletonList(OrderLineItemTestFixture.create().getOrderLineItem()));
        order.setOrderDateTime(LocalDateTime.now());
        return new OrderTestFixture(order);
    }

    public OrderTestFixture changeId(Object o) {
        Order newOrder = new Order();
        newOrder.setId((UUID) o);
        newOrder.setStatus(order.getStatus());
        newOrder.setOrderTable(order.getOrderTable());
        newOrder.setOrderTableId(order.getOrderTableId());
        newOrder.setDeliveryAddress(order.getDeliveryAddress());
        newOrder.setType(order.getType());
        newOrder.setOrderLineItems(order.getOrderLineItems());
        newOrder.setOrderDateTime(order.getOrderDateTime());
        this.order = newOrder;
        return this;
    }

    public OrderTestFixture changeOrderTable(OrderTable orderTable) {
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(order.getStatus());
        newOrder.setOrderTable(orderTable);
        if (orderTable != null) {
            newOrder.setOrderTableId(orderTable.getId());
        } else {
            newOrder.setOrderTableId(null);
        }
        newOrder.setDeliveryAddress(order.getDeliveryAddress());
        newOrder.setType(order.getType());
        newOrder.setOrderLineItems(order.getOrderLineItems());
        newOrder.setOrderDateTime(order.getOrderDateTime());
        this.order = newOrder;
        return this;
    }

    public OrderTestFixture changeOrderTableId(OrderTable orderTable) {
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(order.getStatus());
        newOrder.setOrderTable(order.getOrderTable());
        newOrder.setOrderTableId(orderTable.getId());
        newOrder.setDeliveryAddress(order.getDeliveryAddress());
        newOrder.setType(order.getType());
        newOrder.setOrderLineItems(order.getOrderLineItems());
        newOrder.setOrderDateTime(order.getOrderDateTime());
        this.order = newOrder;
        return this;
    }

    public OrderTestFixture changeOrderLineItems(List<OrderLineItem> orderLineItems) {
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(order.getStatus());
        newOrder.setOrderTable(order.getOrderTable());
        newOrder.setOrderTableId(order.getOrderTableId());
        newOrder.setDeliveryAddress(order.getDeliveryAddress());
        newOrder.setType(order.getType());
        newOrder.setOrderLineItems(orderLineItems);
        newOrder.setOrderDateTime(order.getOrderDateTime());
        this.order = newOrder;
        return this;
    }

    public OrderTestFixture changeStatus(OrderStatus orderStatus) {
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(orderStatus);
        newOrder.setOrderTable(order.getOrderTable());
        newOrder.setOrderTableId(order.getOrderTableId());
        newOrder.setDeliveryAddress(order.getDeliveryAddress());
        newOrder.setType(order.getType());
        newOrder.setOrderLineItems(order.getOrderLineItems());
        newOrder.setOrderDateTime(order.getOrderDateTime());
        this.order = newOrder;
        return this;
    }

    public OrderTestFixture changeType(OrderType orderType) {
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(order.getStatus());
        newOrder.setOrderTable(order.getOrderTable());
        newOrder.setOrderTableId(order.getOrderTableId());
        newOrder.setDeliveryAddress(order.getDeliveryAddress());
        newOrder.setType(orderType);
        newOrder.setOrderLineItems(order.getOrderLineItems());
        newOrder.setOrderDateTime(order.getOrderDateTime());
        this.order = newOrder;
        return this;
    }

    public Order getOrder() {
        return order;
    }

    public OrderTestFixture changeDeliveryAddress(String deliveryAddress) {
        Order newOrder = new Order();
        newOrder.setId(order.getId());
        newOrder.setStatus(order.getStatus());
        newOrder.setOrderTable(order.getOrderTable());
        newOrder.setOrderTableId(order.getOrderTableId());
        newOrder.setDeliveryAddress(deliveryAddress);
        newOrder.setType(order.getType());
        newOrder.setOrderLineItems(order.getOrderLineItems());
        newOrder.setOrderDateTime(order.getOrderDateTime());
        this.order = newOrder;
        return this;
    }
}
