package kitchenpos.fixture.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemFixture {

    public static OrderLineItem createOrderLineItem(UUID menuId, int price, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static OrderLineItem createOrderLineItem(long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static OrderLineItem createOrderLineItem(int price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        return orderLineItem;
    }

    public static OrderLineItem createOrderLineItem(UUID menuId, Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(20000));
        orderLineItem.setQuantity(1);
        return orderLineItem;
    }
}
