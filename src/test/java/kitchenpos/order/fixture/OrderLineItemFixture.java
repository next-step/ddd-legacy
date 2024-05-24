package kitchenpos.order.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

import java.math.BigDecimal;


public class OrderLineItemFixture {

    private static OrderLineItem 주문항목을_생성한다(Long seq, Menu menu, long quantity) {
        var 주문항목 = new OrderLineItem();
        주문항목.setSeq(seq);
        주문항목.setMenu(menu);
        주문항목.setMenuId(menu.getId());
        주문항목.setQuantity(quantity);
        주문항목.setPrice(menu.getPrice());

        return 주문항목;
    }

    private static OrderLineItem 주문항목을_생성한다(Long seq, Menu menu, long quantity, BigDecimal price) {
        var 주문항목 = new OrderLineItem();
        주문항목.setSeq(seq);
        주문항목.setMenu(menu);
        주문항목.setMenuId(menu.getId());
        주문항목.setQuantity(quantity);
        주문항목.setPrice(price);

        return 주문항목;
    }

}
