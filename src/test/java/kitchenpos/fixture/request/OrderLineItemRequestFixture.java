package kitchenpos.fixture.request;

import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemRequestFixture {

    public static OrderLineItem createOrderLineRequest(final UUID menuId) {
        return createOrderLineRequest(menuId, 1, 20_000L);
    }

    public static OrderLineItem createOrderLineRequest(final UUID menuId, final Long price) {
        return createOrderLineRequest(menuId, 1, price);
    }

    public static OrderLineItem createOrderLineRequest(final UUID menuId, final int quantity) {
        return createOrderLineRequest(menuId, quantity, 20_000L);
    }

    public static OrderLineItem createOrderLineRequest(final UUID menuId, final int quantity, final Long price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setMenuId(menuId);
        return orderLineItem;
    }
}
