package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.common.UuidBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

public class OrderFixture {

  public static Order createEatInOrder(OrderTable orderTable, OrderLineItem orderLineItem){
    Order order = new Order();
    order.setType(OrderType.EAT_IN);
    order.setOrderTable(orderTable);
    order.setOrderTableId(orderTable.getId());
    order.setOrderLineItems(List.of(orderLineItem));
    return order;
  }

  public static OrderLineItem createOrderLineItem(Menu menu, int quantity){
    OrderLineItem orderLineItem = new OrderLineItem();

    orderLineItem.setMenu(menu);
    orderLineItem.setQuantity(quantity);
    orderLineItem.setMenuId(menu.getId());
    orderLineItem.setPrice(menu.getPrice());

    return orderLineItem;
  }
}
