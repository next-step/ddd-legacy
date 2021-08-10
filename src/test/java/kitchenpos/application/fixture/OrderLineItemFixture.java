package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.MenuFixture.HIDED_MENU;
import static kitchenpos.application.fixture.MenuFixture.MENU1;
import static kitchenpos.application.fixture.MenuFixture.MENU2;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {

    private static final UUID UUID1 = UUID.randomUUID();
    private static final UUID UUID2 = UUID.randomUUID();
    private static final long QUANTITY1 = 2L;
    private static final long QUANTITY2 = 3L;
    private static final long NEGATIVE_QUANTITY = -2L;
    private static final long WRONG_PRICE = 12345L;

    public static OrderLineItem ORDER_LINE_ITEM1() {
        return createOrderLineItem(UUID1, MENU1().getPrice(), QUANTITY1, MENU1());
    }

    public static OrderLineItem ORDER_LINE_ITEM2() {
        return createOrderLineItem(UUID2, MENU2().getPrice(), QUANTITY2, MENU1());
    }

    public static OrderLineItem HIDED_MENU_ORDER_LINE_ITEM() {
        return createOrderLineItem(UUID2, MENU2().getPrice(), QUANTITY2, HIDED_MENU());
    }

    public static OrderLineItem NEGATIVE_QUANTITY_ORDER_LINE_ITEM() {
        return createOrderLineItem(UUID1, MENU1().getPrice(), NEGATIVE_QUANTITY, MENU1());
    }

    public static OrderLineItem WRONG_PRICE_MENU_ORDER_LINE_ITEM() {
        return createOrderLineItem(UUID1, WRONG_PRICE, NEGATIVE_QUANTITY, MENU1());
    }

    public static List<OrderLineItem> ORDER_LINE_ITEMS() {
        return Arrays.asList(ORDER_LINE_ITEM1(), ORDER_LINE_ITEM2());
    }

    public static List<OrderLineItem> NEGATIVE_QUANTITY_ORDER_LINE_ITEMS() {
        return Arrays.asList(NEGATIVE_QUANTITY_ORDER_LINE_ITEM(), NEGATIVE_QUANTITY_ORDER_LINE_ITEM());
    }

    public static List<OrderLineItem> HIDED_MENU_ORDER_LINE_ITEMS() {
        return Arrays.asList(HIDED_MENU_ORDER_LINE_ITEM(), HIDED_MENU_ORDER_LINE_ITEM());
    }

    public static List<OrderLineItem> WRONG_PRICE_MENU_ORDER_LINE_ITEMS() {
        return Arrays.asList(WRONG_PRICE_MENU_ORDER_LINE_ITEM(), WRONG_PRICE_MENU_ORDER_LINE_ITEM());
    }

    private static OrderLineItem createOrderLineItem(final UUID uuid, final long price, final long quantity, final Menu menu) {
        return createOrderLineItem(uuid, BigDecimal.valueOf(price), quantity, menu);
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
