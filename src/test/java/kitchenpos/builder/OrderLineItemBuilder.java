package kitchenpos.builder;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public final class OrderLineItemBuilder {
    private Long seq;
    private Menu menu;
    private long quantity;
    private UUID menuId;
    private BigDecimal price;

    private OrderLineItemBuilder() {
        seq = new Random().nextLong();
        menu = MenuBuilder.aMenu().build();
        quantity = 1L;
        menuId = menu.getId();
        price = BigDecimal.valueOf(16_000L);
    }

    public static OrderLineItemBuilder anOrderLineItem() {
        return new OrderLineItemBuilder();
    }

    public OrderLineItemBuilder setSeq(Long seq) {
        this.seq = seq;
        return this;
    }

    public OrderLineItemBuilder setMenu(Menu menu) {
        this.menu = menu;
        this.menuId = menu.getId();
        return this;
    }

    public OrderLineItemBuilder setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public OrderLineItemBuilder setMenuId(UUID menuId) {
        this.menuId = menuId;
        return this;
    }

    public OrderLineItemBuilder setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OrderLineItemBuilder setPrice(Long price) {
        this.price = BigDecimal.valueOf(price);
        return this;
    }

    public OrderLineItem build() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
