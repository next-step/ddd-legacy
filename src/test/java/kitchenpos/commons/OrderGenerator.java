package kitchenpos.commons;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderGenerator {
    @Autowired
    private OrderService orderService;

    public Order generateByOrderTypeAndOrderTableAndOrderLineItems(OrderType orderType, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(orderType);
        order.setDeliveryAddress("address");
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(orderLineItems);
        return orderService.create(order);
    }
}
