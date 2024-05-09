package fixtures;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemBuilder {

    private Menu menu = new Menu();
    private BigDecimal price = BigDecimal.ZERO;
    private long quantity = 0L;

    public OrderLineItemBuilder aOrderLineItem() {
        return new OrderLineItemBuilder();
    }

    public OrderLineItemBuilder withMenu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public OrderLineItemBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OrderLineItemBuilder withQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }


    public OrderLineItem build() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

}
