package kitchenpos.application.testFixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public record OrderLineItemFixture() {

    public static OrderLineItem newOne() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(MenuFixture.newOne());
        orderLineItem.setQuantity(1);
        return orderLineItem;
    }

    public static OrderLineItem newOne(BigDecimal price) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(MenuFixture.newOne());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static OrderLineItem newOne(Menu menu) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        return orderLineItem;
    }

    public static OrderLineItem newOne(Menu menu, int price) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        return orderLineItem;
    }

    public static OrderLineItem newOne(int quantity) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(MenuFixture.newOne());
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }
}
