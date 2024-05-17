package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrderLineItemFixture {
    private OrderLineItemFixture() {
    }

    public static OrderLineItem createOrderLineItem(final UUID menuId,
                                                    final BigDecimal price,
                                                    final long quantity) {
        final OrderLineItem orderLineTime = new OrderLineItem();
        orderLineTime.setMenuId(menuId);
        orderLineTime.setPrice(price);
        orderLineTime.setQuantity(quantity);

        return orderLineTime;
    }
    
    public static OrderLineItem createOrderLineItem(final Menu menu,
                                                    final BigDecimal price,
                                                    final long quantity) {
        final OrderLineItem orderLineTime = new OrderLineItem();
        orderLineTime.setMenu(menu);
        if (Objects.nonNull(menu)) {
            orderLineTime.setMenuId(menu.getId());
        }
        orderLineTime.setPrice(price);
        orderLineTime.setQuantity(quantity);

        return orderLineTime;
    }
}
