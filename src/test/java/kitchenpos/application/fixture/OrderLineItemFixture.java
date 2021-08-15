package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.fixture.MenuFixture.SHOW_MENU_REQUEST;

public class OrderLineItemFixture {

    private static final long ORDER_LINE_ITEM_QUANTITY_ONE = 1L;

    public static OrderLineItem ORDER_LINE_ITEM_ONE_REQUEST() {
        return createOrderLineItem(SHOW_MENU_REQUEST().getId(), SHOW_MENU_REQUEST().getPrice(), ORDER_LINE_ITEM_QUANTITY_ONE, SHOW_MENU_REQUEST());
    }

    public static List<OrderLineItem> ORDER_LINE_ITEMS() {
        return Arrays.asList(ORDER_LINE_ITEM_ONE_REQUEST());
    }

    private static OrderLineItem createOrderLineItem(final UUID uuid, final BigDecimal price, final long quantity, final Menu menu) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(uuid);
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenu(menu);
        return orderLineItem;
    }
}
