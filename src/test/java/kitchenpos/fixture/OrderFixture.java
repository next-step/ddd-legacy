package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.OrderLineItemFixture.ORDER_LINE_ITEM;
import static kitchenpos.fixture.OrderTableFixture.ORDER_TABLE;

public class OrderFixture {

    static OrderLineItem orderLineItem = ORDER_LINE_ITEM();

    public static Order ORDER_EAT_IN(OrderStatus orderStatus) {
        OrderTable orderTable = ORDER_TABLE();
        Order order = new Order();

        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setType(OrderType.EAT_IN);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(orderLineItem));
        order.setStatus(orderStatus);

        return order;
    }

    public static Order ORDER_DELIVERY(OrderStatus orderStatus) {
        Order order = new Order();

        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress("여기로 배달와줘");
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(orderLineItem));
        order.setStatus(orderStatus);

        return order;
    }

    public static Order ORDER_TAKEOUT(OrderStatus orderStatus) {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(UUID.randomUUID());
        order.setOrderLineItems(List.of(orderLineItem));
        order.setStatus(orderStatus);
        return order;
    }
}
