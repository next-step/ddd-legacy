package kitchenpos.service;

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
        return builder(null);
    }

    public static OrderLineItemFixture builder(Menu menu) {
        return new OrderLineItemFixture()
                .menu(menu);
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
}
