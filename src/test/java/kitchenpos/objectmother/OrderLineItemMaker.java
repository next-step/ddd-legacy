package kitchenpos.objectmother;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemMaker {

    public static OrderLineItem make(Menu menu, long quantity, Long price) {
        return new OrderLineItem(menu, quantity, new BigDecimal(price), menu.getId());
    }

}
