package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderLineItemFixture {

    public static final List<OrderLineItem> DEFAULT_ORDER_LINE_ITEM = Arrays.asList(createOrderLineItem());

    public static OrderLineItem createOrderLineItem() {
        return createOrderLineItem(1);
    }

    public static OrderLineItem createOrderLineItem(int quantity) {
        return createOrderLineItem(MenuFixture.DEFAULT_MENU, quantity);
    }

    public static List<OrderLineItem> createOrderLineItems(int quantity) {
        return List.of(createOrderLineItem(MenuFixture.DEFAULT_MENU, quantity));
    }

    public static OrderLineItem createOrderLineItem(Menu menu, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

    public static List<OrderLineItem> moreMenuLine(int quantity, Menu... menus) {
        List<OrderLineItem> result = new ArrayList<>();
        result.add(OrderLineItemFixture.createOrderLineItem(MenuFixture.DEFAULT_MENU, quantity));
        if(menus == null || menus.length == 0) return result;

        Arrays.stream(menus).forEach(menu -> result.add(createOrderLineItem(menu, quantity)));
        return result;
    }
}
