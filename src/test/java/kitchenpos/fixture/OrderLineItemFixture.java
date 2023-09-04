package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.util.UUID;

public class OrderLineItemFixture {

    private OrderLineItemFixture() {
    }

    public static OrderLineItem create(Menu menu, Long quantity) {
        OrderLineItem result = new OrderLineItem();
        result.setMenu(menu);
        result.setMenuId(menu.getId());
        result.setPrice(menu.getPrice());
        result.setQuantity(quantity);
        return result;
    }

}