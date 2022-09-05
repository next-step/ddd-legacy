package kitchenpos.application.stub;

import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;

public class OrderLineItemStub {

    public static OrderLineItem createRequest() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setPrice(BigDecimal.valueOf(15_000));
        orderLineItem.setMenuId(MenuStub.createDefault().getId());
        orderLineItem.setQuantity(1L);
        return orderLineItem;
    }

    public static OrderLineItem createDefault() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenu(MenuStub.createDefault());
        orderLineItem.setQuantity(1L);
        return orderLineItem;
    }
}
