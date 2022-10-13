package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {

    private static final int DEFAULT_ORDER_LINE_ITEM_QUANTITY = 1;

    public static OrderLineItem create() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(MenuFixture.ONE_FRIED_CHICKEN.get());
        orderLineItem.setQuantity(DEFAULT_ORDER_LINE_ITEM_QUANTITY);
        return orderLineItem;
    }

    public static OrderLineItem request(UUID menuId, int quantity, BigDecimal price) {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
