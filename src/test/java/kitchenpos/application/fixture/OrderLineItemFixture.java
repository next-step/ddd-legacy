package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public record OrderLineItemFixture(Menu 메뉴, long 주문수량, BigDecimal 주문가격) {

    public static final long DEFAULT_ORDER_LINE_ITEM_QTY = 3;
    public static final String DEFAULT_ORDER_LINE_ITEM_PRICE = "60000";

    public static OrderLineItemFixture init() {
        return new OrderLineItemFixture(
            MenuFixture.init().create(),
            DEFAULT_ORDER_LINE_ITEM_QTY,
            new BigDecimal(DEFAULT_ORDER_LINE_ITEM_PRICE)
        );
    }

    public static OrderLineItemFixture test(Menu 메뉴, long 주문수량, BigDecimal 주문가격) {
        return new OrderLineItemFixture(
            Objects.requireNonNullElse(메뉴, MenuFixture.init().create()),
            Objects.requireNonNullElse(주문수량, DEFAULT_ORDER_LINE_ITEM_QTY),
            Objects.requireNonNullElse(주문가격, new BigDecimal(DEFAULT_ORDER_LINE_ITEM_PRICE))
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

