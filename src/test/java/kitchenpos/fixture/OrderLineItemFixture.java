package kitchenpos.fixture;

import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLineItemFixture {

    private static final long DEFAULT_PRICE = 15_000L;

    public static OrderLineItem createRequest() {
        return createRequest(MenuFixture.createDefault().getId(), DEFAULT_PRICE, 1L);
    }

    public static OrderLineItem createRequest(final UUID menuId) {
        return createRequest(menuId, DEFAULT_PRICE, 1L);
    }

    public static OrderLineItem createRequest(final Long price) {
        return createRequest(MenuFixture.createDefault().getId(), price, 1L);
    }

    public static OrderLineItem createRequest(final UUID menuId, final Long price) {
        return createRequest(menuId, price, 1L);
    }

    public static OrderLineItem createRequest(final UUID menuId, final Long price, final Long quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setQuantity(quantity);
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
