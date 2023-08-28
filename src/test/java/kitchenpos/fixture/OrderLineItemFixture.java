package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;

import static kitchenpos.fixture.MenuFixture.MENU;

public class OrderLineItemFixture {

    public static OrderLineItem ORDER_LINE_ITEM() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = MENU();

        orderLineItem.setSeq(1L);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(1);
        orderLineItem.setPrice(new BigDecimal(10_000));

        return orderLineItem;
    }
}
