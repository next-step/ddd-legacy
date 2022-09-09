package kitchenpos.fixture;

import kitchenpos.domain.OrderLineItem;

import java.util.Random;

import static kitchenpos.fixture.MenuFixture.menu;

public class OrderLineFixture {
    public static OrderLineItem orderLineItem() {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(new Random().nextLong());
        orderLineItem.setMenu(menu());
        return orderLineItem;
    }
}
