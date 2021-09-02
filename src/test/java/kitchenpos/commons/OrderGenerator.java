package kitchenpos.commons;

import kitchenpos.application.OrderService;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class OrderGenerator {
    @Autowired
    private OrderService orderService;

    public Order generateByOrderTypeAndOrderTableAndOrderLineItems(OrderType orderType, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        Order order = this.generateRequestByOrderTypeAndOrderTableAndOrderLineItems(orderType, orderTable, orderLineItems);
        return orderService.create(order);
    }

    public Order generateRequestByOrderTypeAndOrderTableAndOrderLineItems(OrderType orderType, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setDeliveryAddress("address");
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(orderLineItems);
        return order;
    }
}
