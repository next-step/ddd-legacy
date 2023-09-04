package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemFixture {

    private OrderLineItemFixture() {
    }

    public static OrderLineItem create(Menu menu, Long quantity) {
        return create(menu, menu.getPrice(), quantity);
    }
    public static OrderLineItem create(Menu menu, BigDecimal price, Long quantity) {
        OrderLineItem result = new OrderLineItem();
        result.setMenu(menu);
        result.setMenuId(menu.getId());
        result.setPrice(price);
        result.setQuantity(quantity);
        return result;
    }

}
