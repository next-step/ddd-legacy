package kitchenpos.util;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemBuilder {

    private long quantity;
    private BigDecimal price;
    private UUID menuId;
    private Menu menu;

    private OrderLineItemBuilder() {
    }

    public static OrderLineItemBuilder quantity(long quantity) {
        OrderLineItemBuilder orderLineItemBuilder = new OrderLineItemBuilder();
        orderLineItemBuilder.quantity = quantity;
        return orderLineItemBuilder;
    }

    public OrderLineItemBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OrderLineItemBuilder menuId(UUID menuId) {
        this.menuId = menuId;
        return this;
    }

    public OrderLineItemBuilder menu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OrderLineItem build() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setMenu(menu);
        return orderLineItem;
    }
}
