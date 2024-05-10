package kitchenpos.helper;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemTestHelper {

    public OrderLineItemTestHelper() {
    }

    public static OrderLineItem 주문할_메뉴_생성(Menu menu, int quantity){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice());

        return orderLineItem;
    }
}
