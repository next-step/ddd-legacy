package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemFixture {
    private OrderLineItemFixture() {
    }

    public static OrderLineItem createOrderLineItem(BigDecimal price,
                                                    Menu menu,
                                                    long quantity) {
        OrderLineItem orderLineTime = new OrderLineItem();
        orderLineTime.setPrice(price);
        orderLineTime.setMenu(menu);
        orderLineTime.setMenuId(menu.getId());
        orderLineTime.setQuantity(quantity);

        return orderLineTime;
    }
}
