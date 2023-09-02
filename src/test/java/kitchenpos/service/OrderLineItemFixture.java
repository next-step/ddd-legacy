package kitchenpos.service;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {
    private final OrderLineItem orderLineItem;

    public OrderLineItemFixture() {
        orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
    }

    public static OrderLineItemFixture builder() {
        return new OrderLineItemFixture();
    }

    public static OrderLineItemFixture builder(Menu menu) {
        return builder()
                .menu(menu)
                .price(menu.getPrice());
    }

    public OrderLineItem build() {
        return orderLineItem;
    }

    public OrderLineItemFixture menu(Menu menu) {
        if (menu != null) {
            orderLineItem.setMenuId(menu.getId());
        }
        orderLineItem.setMenu(menu);
        return this;
    }

    public OrderLineItemFixture menuId(UUID menuId) {
        orderLineItem.setMenuId(menuId);
        return this;
    }

    public OrderLineItemFixture quantity(long quantity) {
        orderLineItem.setQuantity(quantity);
        return this;
    }

    public OrderLineItemFixture price(BigDecimal price) {
        orderLineItem.setPrice(price);
        return this;
    }

    public OrderLineItemFixture price(long price) {
        return price(new BigDecimal(price));
    }
}
