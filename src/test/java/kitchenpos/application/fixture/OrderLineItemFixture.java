package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.MenuFixture.HIDED_MENU;
import static kitchenpos.application.fixture.MenuFixture.MENU1;
import static kitchenpos.application.fixture.MenuFixture.MENU2;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {

    private static final UUID UUID1 = UUID.randomUUID();
    private static final UUID UUID2 = UUID.randomUUID();
    private static final long QUANTITY1 = 2L;
    private static final long QUANTITY2 = 3L;
    private static final long NEGATIVE_QUANTITY = -2L;
    private static final long WRONG_PRICE = 12345L;

    public static OrderLineItem ORDER_LINE_ITEM1() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID1);
        orderLineItem.setPrice(MENU1().getPrice());
        orderLineItem.setQuantity(QUANTITY1);
        orderLineItem.setMenu(MENU1());
        return orderLineItem;
    }

    public static OrderLineItem ORDER_LINE_ITEM2() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID2);
        orderLineItem.setPrice(MENU2().getPrice());
        orderLineItem.setQuantity(QUANTITY2);
        orderLineItem.setMenu(MENU1());
        return orderLineItem;
    }

    public static OrderLineItem HIDED_MENU_ORDER_LINE_ITEM() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID2);
        orderLineItem.setPrice(MENU2().getPrice());
        orderLineItem.setQuantity(QUANTITY2);
        orderLineItem.setMenu(HIDED_MENU());
        return orderLineItem;
    }

    public static OrderLineItem NEGATIVE_QUANTITY_ORDER_LINE_ITEM() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID1);
        orderLineItem.setPrice(MENU1().getPrice());
        orderLineItem.setQuantity(NEGATIVE_QUANTITY);
        orderLineItem.setMenu(MENU1());
        return orderLineItem;
    }

    public static OrderLineItem WRONG_PRICE_MENU_ORDER_LINE_ITEM() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID1);
        orderLineItem.setPrice(BigDecimal.valueOf(WRONG_PRICE));
        orderLineItem.setQuantity(NEGATIVE_QUANTITY);
        orderLineItem.setMenu(MENU1());
        return orderLineItem;
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

}
