package kitchenpos.objectmother;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

import java.util.List;
import java.util.UUID;

public class OrderMaker {

    public static Order makeEatin(OrderTable orderTable, OrderLineItem... orderLineItems) {
        return new Order(OrderType.EAT_IN, List.of(orderLineItems), null, orderTable, orderTable.getId());
    }

    public static Order makeDelivery(String deliveryAddress, OrderLineItem... orderLineItems) {
        return new Order(OrderType.DELIVERY, List.of(orderLineItems), deliveryAddress, null, null);
    }

    public static Order makeTakeout(OrderLineItem... orderLineItems) {
        return new Order(OrderType.TAKEOUT, List.of(orderLineItems), null, null, null);
    }

}
