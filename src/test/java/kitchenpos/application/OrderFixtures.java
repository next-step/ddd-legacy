package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;

public class OrderFixtures {

  static Order createRequestEatInOrder(UUID orderTableId,
      List<OrderLineItem> orderLineItems) {
    Order order = new Order();
    order.setType(OrderType.EAT_IN);
    order.setOrderTableId(orderTableId);
    order.setOrderLineItems(orderLineItems);
    return order;
  }

  static Order createRequestDeliveryOrder(
      UUID orderTableId,
      List<OrderLineItem> orderLineItems,
      String deliveryAddress
  ) {
    Order order = new Order();
    order.setType(OrderType.DELIVERY);
    order.setOrderTableId(orderTableId);
    order.setDeliveryAddress(deliveryAddress);
    order.setOrderLineItems(orderLineItems);
    return order;
  }

  static Order createRequestTakeout(List<OrderLineItem> orderLineItems) {
    Order order = new Order();
    order.setType(OrderType.TAKEOUT);
    order.setOrderLineItems(orderLineItems);
    return order;
  }

  static OrderLineItem createRequestOrderLineItem(UUID menuId, BigDecimal price,
      int quantity) {
    OrderLineItem orderLineItem = new OrderLineItem();
    orderLineItem.setMenuId(menuId);
    orderLineItem.setPrice(price);
    orderLineItem.setQuantity(quantity);
    return orderLineItem;
  }

  static OrderTable createRequestOrderTable(String name, int numberOfGuests,
      boolean occupied) {
    OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName(name);
    orderTable.setNumberOfGuests(numberOfGuests);
    orderTable.setOccupied(occupied);
    return orderTable;
  }

  static Menu createRequestMenu(String name, BigDecimal price, boolean displayed) {
    Menu menu = new Menu();
    menu.setId(UUID.randomUUID());
    menu.setName(name);
    menu.setPrice(price);
    menu.setDisplayed(displayed);
    return menu;
  }
}
