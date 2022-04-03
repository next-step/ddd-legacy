package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;

import java.util.ArrayList;
import java.util.List;

public class OrderFixture {
    public static Order create(final OrderType orderType, final Menu menu, final int quantity) {
        Order order = new Order();
        order.setType(orderType);
        List<OrderLineItem> listOrderLineItems = new ArrayList<>();
        listOrderLineItems.add(OrderLineItemTest.create(menu, quantity));
        order.setOrderLineItems(listOrderLineItems);
        return order;
    }

}
