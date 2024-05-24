package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

  public static Order create(OrderType type, List<OrderLineItem> orderLineItems,
      String deliveryAddress, OrderTable orderTable) {
    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setType(type);
    order.setOrderLineItems(orderLineItems);
    order.setDeliveryAddress(deliveryAddress);
    order.setOrderTable(orderTable);
    order.setOrderTableId(orderTable.getId());
    return order;
  }

  public static OrderLineItem createOrderLineItem(Menu menu, Long quantity, Long price) {
    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenu(menu);
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setQuantity(quantity);
    orderLineItem.setPrice(new BigDecimal(price));
    return orderLineItem;
  }

}
