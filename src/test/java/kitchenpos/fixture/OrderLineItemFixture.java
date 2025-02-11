package kitchenpos.fixture;

import kitchenpos.OrderTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.TestConstant.*;

public class OrderLineItemFixture {

    private OrderLineItemFixture() {
    }

    public static OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static OrderLineItem createOrderLineItem() {
        return createOrderLineItem(후라이드치킨_MENU_UUID, DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
    }

    public static OrderLineItem createOrderLineItem(UUID menuId) {
        return createOrderLineItem(menuId, DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
    }

    public static OrderLineItem createOrderLineItem(Menu menu) {
        return createOrderLineItem(menu, DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
    }

    public static OrderLineItem createOrderLineItem(Menu menu, int quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
