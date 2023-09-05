package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemFixture {

    public static OrderLineItem createOrderLineItem(int price, int quantity, Menu menu) {
        OrderLineItem item = new OrderLineItem();
        item.setMenu(menu);
        item.setPrice(new BigDecimal(price));
        item.setQuantity(quantity);
        item.setMenuId(menu.getId());
        return item;
    }

    public static OrderLineItem createOrderLineItem() {
        Menu menu = MenuFixture.createMenu();
        return createOrderLineItem(menu.getPrice().intValue(), 1, MenuFixture.createMenu());
    }

    public static OrderLineItem createOrderLineItemWithMenu(Menu menu) {
        return createOrderLineItem(menu.getPrice().intValue(), 1, MenuFixture.createMenu());
    }

    public static OrderLineItem createOrderLineItemWithMenuAndQuantity(Menu menu, int quantity) {
        return createOrderLineItem(menu.getPrice().intValue(), quantity, MenuFixture.createMenu());
    }
}
