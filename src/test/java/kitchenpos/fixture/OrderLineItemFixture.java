package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;

public class OrderLineItemFixture {
    public static OrderLineItem createOrderLineItem() {
        return createOrderLineItem(null, createMenu(), 1, BigDecimal.TEN);
    }

    public static OrderLineItem createOrderLineItem(final Menu menu) {
        return createOrderLineItem(null, menu, 1, BigDecimal.TEN);
    }

    public static OrderLineItem createOrderLineItem(final Menu menu, final long quantity) {
        return createOrderLineItem(null, menu, quantity, BigDecimal.TEN);
    }

    public static OrderLineItem createOrderLineItem(
            final Long seq, final Menu menu, final long quantity, final BigDecimal price
    ) {
        return createOrderLineItem(seq, menu, quantity, menu.getId(), price);
    }

    public static OrderLineItem createOrderLineItem(
            final Long seq, final Menu menu, final long quantity, final UUID menuId, final BigDecimal price
    ) {
        final OrderLineItem orderLineItem = new OrderLineItem();

        orderLineItem.setSeq(seq);
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);

        return orderLineItem;
    }
}
