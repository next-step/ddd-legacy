package kitchenpos.commons;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderLineItemGenerator {

    public OrderLineItem generateRequestByMenu(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setQuantity(2L);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(BigDecimal.valueOf(1000));
        return orderLineItem;
    }

}
