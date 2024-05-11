package kitchenpos.order.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.menu.fixture.MenuFixture;

import java.math.BigDecimal;

public class OrderLineItemFixture {
    private MenuFixture menuFixture = new MenuFixture();

    public OrderLineItem 주문_항목 = create(menuFixture.메뉴, 1, new BigDecimal(10000));
    public OrderLineItem 주문_항목_A = create(menuFixture.메뉴_A_가격_10000, 1, new BigDecimal(10000));
    public OrderLineItem 수량_없는_주문_항목 = create(menuFixture.메뉴, 1, new BigDecimal(10000));

    public static OrderLineItem create(Menu menu, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);

        return orderLineItem;
    }
}
