package kitchenpos.fixture;

import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemFixture {

    public static OrderLineItem createRequest() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(BigDecimal.valueOf(15_000));
        orderLineItem.setMenuId(MenuFixture.createDefault().getId());
        orderLineItem.setQuantity(1L);
        return orderLineItem;
    }

    public static OrderLineItem createDefault() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenu(MenuFixture.createDefault());
        orderLineItem.setQuantity(1L);
        return orderLineItem;
    }
}
