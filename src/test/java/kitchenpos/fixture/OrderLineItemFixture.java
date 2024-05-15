package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemFixture {
    private OrderLineItemFixture() {
    }

    public static OrderLineItem createOrderLineItem(UUID menuId,
                                                    BigDecimal price,
                                                    long quantity) {
        OrderLineItem orderLineTime = new OrderLineItem();
        orderLineTime.setMenuId(menuId);
        orderLineTime.setPrice(price);
        orderLineTime.setQuantity(quantity);

        return orderLineTime;
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
