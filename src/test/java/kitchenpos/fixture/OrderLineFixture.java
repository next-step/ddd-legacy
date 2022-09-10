package kitchenpos.fixture;

import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;
import java.util.Random;

import static kitchenpos.fixture.MenuFixture.menu;

public class OrderLineFixture {
    public static OrderLineItem orderLineItem() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(new Random().nextLong());
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(BigDecimal.valueOf(20_000L));
        orderLineItem.setMenu(menu());
        return orderLineItem;
    }
}
