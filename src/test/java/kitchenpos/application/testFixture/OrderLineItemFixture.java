package kitchenpos.application.testFixture;

import kitchenpos.domain.OrderLineItem;

public record OrderLineItemFixture() {

    public static OrderLineItem newOne() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(MenuFixture.newOne());
        orderLineItem.setQuantity(1);
        return orderLineItem;
    }
}
