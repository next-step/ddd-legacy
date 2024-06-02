package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.springframework.test.util.ReflectionTestUtils;

public class OrderFixture {

  public static Order create(OrderType type, OrderStatus orderStatus, List<OrderLineItem> orderLineItems,
      String deliveryAddress, OrderTable orderTable) {
    Order order = new Order();
    ReflectionTestUtils.setField(order, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(order, "type", type);
    ReflectionTestUtils.setField(order, "status", orderStatus);
    ReflectionTestUtils.setField(order, "orderLineItems", orderLineItems);
    ReflectionTestUtils.setField(order, "deliveryAddress", deliveryAddress);
    ReflectionTestUtils.setField(order, "orderTable", orderTable);
    ReflectionTestUtils.setField(order, "orderTableId", orderTable.getId());
    return order;
  }

  public static Order create(OrderType type, List<OrderLineItem> orderLineItems,
      String deliveryAddress, OrderTable orderTable) {
    return create(type, OrderStatus.WAITING, orderLineItems, deliveryAddress, orderTable);
  }

  public static OrderLineItem createOrderLineItem(Menu menu, Long quantity, Long price) {
    OrderLineItem orderLineItem = new OrderLineItem();
    ReflectionTestUtils.setField(orderLineItem, "menu", menu);
    ReflectionTestUtils.setField(orderLineItem, "menuId", menu.getId());
    ReflectionTestUtils.setField(orderLineItem, "quantity", quantity);
    ReflectionTestUtils.setField(orderLineItem, "price", new BigDecimal(price));
    return orderLineItem;
  }

}
