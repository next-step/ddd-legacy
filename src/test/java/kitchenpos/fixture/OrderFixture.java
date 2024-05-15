package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderFixture {
    private OrderFixture() {
    }

    public static Order createOrder(UUID id,
                                    OrderTable orderTable,
                                    List<OrderLineItem> orderLineItems,
                                    OrderType type,
                                    OrderStatus status,
                                    String deliveryAddress) {
        Order order = new Order();
        order.setId(id);
        order.setOrderTable(orderTable);
        if (Objects.nonNull(orderTable)) {
            order.setOrderTableId(orderTable.getId());
        }
        order.setOrderLineItems(orderLineItems);
        order.setType(type);
        order.setStatus(status);
        order.setDeliveryAddress(deliveryAddress);

        return order;
    }

    public static Order createOrder(UUID orderTableId,
                                    List<OrderLineItem> orderLineItems,
                                    OrderType type,
                                    OrderStatus status,
                                    String deliveryAddress) {
        Order order = new Order();
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(orderLineItems);
        order.setType(type);
        order.setStatus(status);
        order.setDeliveryAddress(deliveryAddress);

        return order;
    }

    public static Order createOrderWithId(OrderTable orderTable,
                                          List<OrderLineItem> orderLineItems,
                                          OrderType type,
                                          OrderStatus status,
                                          String deliveryAddress,
                                          LocalDateTime orderDateTime) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(orderTable);
        order.setOrderLineItems(orderLineItems);
        order.setType(type);
        order.setStatus(status);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderDateTime(orderDateTime);

        return order;
    }
}
