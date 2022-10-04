package kitchenpos.fixture;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderType;

public class OrderFixture {
    public static Order createOrder() {
        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        return order;
    }


    public static Order createDeliveryOrder() {
        return createOrder();
    }

    public static Order createTakeOutOrder() {
        final Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        return order;
    }

    public static Order createEatInOrder() {
        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        return order;
    }

}
