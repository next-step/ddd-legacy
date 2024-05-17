package kitchenpos.application.testfixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderLineItemFixture() {

    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(5000);
    public static final int DEFAULT_QUANTITY = 1;

    public static OrderLineItem newOne() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        var menu = MenuFixture.newOne();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(DEFAULT_QUANTITY);
        orderLineItem.setPrice(DEFAULT_PRICE);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }

    public static OrderLineItem newOne(BigDecimal price) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        var menu = MenuFixture.newOne();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(DEFAULT_QUANTITY);
        orderLineItem.setPrice(price);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }

    public static OrderLineItem newOne(Menu menu) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(DEFAULT_QUANTITY);
        orderLineItem.setPrice(DEFAULT_PRICE);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }

    public static OrderLineItem newOne(Menu menu, int price) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(DEFAULT_QUANTITY);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }

    public static OrderLineItem newOne(int quantity) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        var menu = MenuFixture.newOne();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(DEFAULT_PRICE);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }
}
