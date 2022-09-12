package kitchenpos.application.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Menu;
import kitchenpos.domain.OrderLineItem;

public class OrderLineItemFixture {

  public static OrderLineItem createOrderLineItem() {
    return createOrderLineItem(MenuFixture.createMenu(), 19_000L, 2);
  }

  public static OrderLineItem createOrderLineItem(Menu menu, long price, int quantity) {
    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenu(menu);
    orderLineItem.setPrice(BigDecimal.valueOf(price));
    orderLineItem.setQuantity(quantity);

    return orderLineItem;
  }
}
