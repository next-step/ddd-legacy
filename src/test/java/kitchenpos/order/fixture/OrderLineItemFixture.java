package kitchenpos.order.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.menu.MenuTestHelper;

import java.math.BigDecimal;

public class OrderLineItemFixture {
    public static final OrderLineItem 주문_항목 = create(MenuTestHelper.메뉴, 1, new BigDecimal(10000));

    private MenuTestHelper menuTestHelper = new MenuTestHelper();
    public OrderLineItem 주문_항목_A = create(menuTestHelper.메뉴_A, 1, new BigDecimal(10000));
    public OrderLineItem 주문_항목_B = create(menuTestHelper.메뉴_B, 1, new BigDecimal(5000));
    public OrderLineItem 주문_항목_C = create(menuTestHelper.메뉴_C, 1, new BigDecimal(100000));

    public static OrderLineItem create(Menu menu, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);

        return orderLineItem;
    }
}
