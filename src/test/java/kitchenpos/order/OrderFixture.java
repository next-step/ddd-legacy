package kitchenpos.order;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderType;
import kitchenpos.order.vo.DeliveryAddress;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.product.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static List<OrderLineItem> orderLineItems(Menu menu) {
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem(menu, new Quantity(1));
        orderLineItems.add(orderLineItem);
        return orderLineItems;
    }

    public static Order deliveryOrder(Menu menu) {
        return new Order(UUID.randomUUID(), OrderType.DELIVERY, orderLineItems(menu), null, new DeliveryAddress("주소"));
    }

    public static Order takeoutOrder(Menu menu) {
        return new Order(UUID.randomUUID(), OrderType.TAKEOUT, orderLineItems(menu), null, null);
    }

    public static Order eatInOrder(Menu menu, OrderTable orderTable) {
        return new Order(UUID.randomUUID(), OrderType.EAT_IN, orderLineItems(menu), orderTable, null);
    }

    public static Order 주문타입NULL(Menu menu, OrderTable orderTable) {
        return new Order(UUID.randomUUID(), null, orderLineItems(menu), orderTable, null);
    }

    public static Order 주문항목NULL(Menu menu) {
        return new Order(UUID.randomUUID(), OrderType.TAKEOUT, null, null, null);
    }

    public static Order 배송지없는배달주문(Menu menu) {
        return new Order(UUID.randomUUID(), OrderType.DELIVERY, orderLineItems(menu), null, null);
    }

    public static Order order(List<OrderLineItem> orderLineItems, OrderTable orderTable) {
        return new Order(UUID.randomUUID(), OrderType.EAT_IN, orderLineItems, orderTable, null);
    }
}


