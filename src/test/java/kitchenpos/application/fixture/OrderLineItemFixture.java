package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.Objects;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public record OrderLineItemFixture(Menu 메뉴, long 주문수량, BigDecimal 주문가격) {

    public static final long DEFAULT_ORDER_LINE_ITEM_QTY = 1;
    public static final BigDecimal DEFAULT_ORDER_LINE_ITEM_PRICE = BigDecimal.valueOf(20_000);

    public static OrderLineItemFixture init() {
        return new OrderLineItemFixture(
            MenuFixture.init().create(),
            DEFAULT_ORDER_LINE_ITEM_QTY,
            DEFAULT_ORDER_LINE_ITEM_PRICE
        );
    }

    public static OrderLineItemFixture test(Menu 메뉴, long 주문수량, BigDecimal 주문가격) {
        return new OrderLineItemFixture(
            Objects.requireNonNullElse(메뉴, MenuFixture.init().create()),
            Objects.requireNonNullElse(주문수량, DEFAULT_ORDER_LINE_ITEM_QTY),
            Objects.requireNonNullElse(주문가격, DEFAULT_ORDER_LINE_ITEM_PRICE)
        );
    }

    public OrderLineItem create() {
        var orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(메뉴);
        orderLineItem.setQuantity(주문수량);
        orderLineItem.setPrice(주문가격);
        return orderLineItem;
    }
}

