package kitchenpos.application.testFixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.OrderType.EAT_IN;

public record OrderFixture() {

    public static Order newOneEatIn(OrderTable orderTable, OrderStatus orderStatus) {
        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(OrderLineItemFixture.newOne()));
        order.setDeliveryAddress("");
        order.setOrderTable(orderTable);
        return order;
    }
}
