package kitchenpos.fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

    private OrderFixture() {
    }

    public static Order createDelivery() {
        return createDelivery("서울 서초구 반포대로 310-6 103동 1401호");
    }

    public static Order createDelivery(String deliveryAddress) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.of(2023, 9, 4, 2, 0));
        order.setDeliveryAddress(deliveryAddress);
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));

        return order;
    }

    public static Order createDelivery(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.of(2023, 9, 4, 2, 0));
        order.setDeliveryAddress("서울 서초구 반포대로 310-6 103동 1401호");
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));

        return order;
    }

    public static Order createEatIn(OrderTable orderTable) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.of(2023, 9, 4, 2, 0));
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());

        return order;
    }

    public static Order createEatIn(OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.of(2023, 9, 4, 2, 0));
        order.setType(OrderType.EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderLineItems(List.of(OrderLineItemFixture.create()));

        OrderTable orderTable = OrderTableFixture.create();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());

        return order;
    }

    public static Order create(List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.of(2023, 9, 4, 2, 0));
        order.setDeliveryAddress("서울 서초구 반포대로 310-6 103동 1401호");
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(orderLineItems);

        return order;
    }

    public static Order createDelivery(Menu menu) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.of(2023, 9, 4, 2, 0));
        order.setDeliveryAddress("서울 서초구 반포대로 310-6 103동 1401호");
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderLineItems(List.of(OrderLineItemFixture.create(menu)));

        return order;
    }
}
