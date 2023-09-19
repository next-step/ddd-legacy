package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineFixture {

    public static OrderLineItem create(Menu menu) {
        return create(1L, menu, 1, menu.getId(), menu.getPrice());
    }

    public static OrderLineItem create(Menu menu, int quantity) {
        return create(1L, menu, quantity, menu.getId(), menu.getPrice());
    }

    public static OrderLineItem create(Menu menu, int quantity, BigDecimal price) {
        return create(1L, menu, quantity, menu.getId(), price);
    }

    public static OrderLineItem create(final Long seq, final Menu menu, final long quantity, final UUID menuId, final BigDecimal price) {
        final OrderLineItem orderLineItem = new OrderLineItem();

        orderLineItem.setSeq(seq);
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);

        return orderLineItem;
    }

}
