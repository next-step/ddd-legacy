package kitchenpos.helper;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderTestHelper {
    private static OrderRepository orderRepository;

    public OrderTestHelper(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public static Order 대기_주문_생성(OrderType orderType, List<OrderLineItem> orderLineItems, OrderTable orderTable){
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(orderType);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        order.setOrderTable(orderTable);

        return orderRepository.save(order);
    }

    public static Order 생성한_주문_상태_변경(UUID orderId, OrderStatus status){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("can't find order"));
        order.setStatus(status);

        return orderRepository.save(order);
    }
}
