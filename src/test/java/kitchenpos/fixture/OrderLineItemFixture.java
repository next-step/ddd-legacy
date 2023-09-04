package kitchenpos.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {

    private OrderLineItemFixture() {
    }

    public static OrderLineItem create() {
        return create(MenuFixture.create());
    }

    public static OrderLineItem create(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setSeq(1L);
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(10_000));

        return orderLineItem;
    }


}
