package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemFixture {

    public static OrderLineItem OrderLineIterm(UUID menuId, int price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(BigDecimal.valueOf(price).setScale(2));
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static OrderLineItem OrderLineIterm(UUID menuId, BigDecimal price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }
}
