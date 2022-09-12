package kitchenpos.application.fixture;

import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixture {

  public static Order createOrderDelivery() {
    return createOrder(OrderType.DELIVERY, "경기도 남양주시", OrderLineItemFixture.createOrderLineItem());
  }

  public static Order createOrderTakeOut() {
    return createOrder(OrderType.TAKEOUT, OrderLineItemFixture.createOrderLineItem());
  }

  public static Order createOrderEatIn() {
    return createOrder(OrderType.EAT_IN, OrderTableFixture.createOrderTable(), OrderLineItemFixture.createOrderLineItem());
  }

  public static Order createOrder(OrderType type, OrderTable orderTable, OrderLineItem... orderLineItems) {
    Order order = createOrder(type, orderLineItems);
    order.setOrderTable(orderTable);

    return order;
  }

  public static Order createOrder(OrderType type, String address, OrderLineItem... orderLineItems) {
    Order order = createOrder(type, orderLineItems);
    order.setDeliveryAddress(address);

    return order;
  }

  public static Order createOrder(OrderType type, OrderLineItem... orderLineItems) {
    Order order = new Order();
    order.setType(type);
    order.setOrderLineItems(List.of(orderLineItems));
    order.setStatus(OrderStatus.WAITING);

    return order;
  }
}
