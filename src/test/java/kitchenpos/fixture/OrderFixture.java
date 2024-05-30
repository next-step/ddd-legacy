package kitchenpos.fixture;

import static kitchenpos.fixture.ProductFixture.THRITY_THOUSANDS;
import static kitchenpos.fixture.ProductFixture.TWENTY_THOUSANDS;
import static kitchenpos.fixture.ProductFixture.TWO_UDONS;

import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

public class OrderFixture {
  public static Product udon = ProductFixture.udon;
  public static Product ramen = ProductFixture.ramen;
  public static MenuGroup menuGroup = MenuGroupFixture.FOR_TWO;
  public static Menu menu = MenuFixture.menu;
  public static OrderTable orderTable = OrderTableFixture.createTable("table");

  public static OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 2);
  public static Order EAT_IN_ORDER = createEatInOrder(orderTable, orderLineItem);
  public static Order DELIVERY_ORDER = createDeliveryOrder("잠실한강공원", orderLineItem);
  public static Order TAKEOUT_ORDER = createTakeOutOrder(orderLineItem);

  public static Order createDeliveryOrder(String deliveryAddress, OrderLineItem item){
    Order order = new Order();
    order.setType(OrderType.DELIVERY);
    order.setDeliveryAddress(deliveryAddress);
    order.setOrderLineItems(List.of(item));

    return order;
  }

  public static Order createTakeOutOrder(OrderLineItem item){
    Order order = new Order();
    order.setType(OrderType.TAKEOUT);
    order.setOrderLineItems(List.of(item));

    return order;
  }

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
