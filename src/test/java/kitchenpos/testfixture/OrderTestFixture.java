package kitchenpos.testfixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderTestFixture {

    public static Order createOrderRequest(OrderType type, OrderStatus status, LocalDateTime orderDateTime, OrderLineItem orderLineItem, OrderTable orderTable){
        Order order = new Order();
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());

        return order;
    }

    public static Order createOrder(UUID id, OrderType type, OrderStatus status, LocalDateTime orderDateTime, OrderLineItem orderLineItem, OrderTable orderTable){
        Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(List.of(orderLineItem));
        order.setOrderTable(orderTable);

        return order;
    }
}
