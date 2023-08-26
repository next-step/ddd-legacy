package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixtures.createMenu;
import static kitchenpos.fixture.MenuFixtures.createMenuProduct;

public class OrderFixtures {

    private final static UUID uuid = UUID.randomUUID();

    private final static MenuProduct menuProduct = createMenuProduct();
    private final static Menu menu = createMenu(new BigDecimal("2000"), "메뉴", List.of(menuProduct));
    private final static OrderLineItem orderLineItem = createOrderLineItem(1L, menu.getPrice(), menu);

    public static Order createOrder(OrderType orderType, OrderStatus orderStatus) {
        Order order = new Order();
        order.setId(uuid);
        order.setType(orderType);
        order.setStatus(orderStatus);
        return order;
    }

    public static Order eatInOrder() {
        return createOrder(OrderType.EAT_IN, List.of(orderLineItem));
    }

    public static Order eatInOrder(OrderStatus orderStatus) {
        return createOrder(OrderType.EAT_IN, orderStatus, List.of(orderLineItem), null);
    }

    public static Order eatInOrder(List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.EAT_IN, orderLineItems);
    }

    public static Order takeoutOrder(OrderStatus orderStatus) {
        return createOrder(OrderType.TAKEOUT, orderStatus, List.of(orderLineItem), null);
    }

    public static Order deliveryOrder(OrderStatus orderStatus) {
        return createOrder(OrderType.DELIVERY, orderStatus, List.of(orderLineItem), null);
    }

    public static Order createOrder(OrderType orderType, List<OrderLineItem> orderLineItems) {
        return createOrder(orderType, OrderStatus.WAITING, orderLineItems, null);
    }

    public static Order createOrder(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress) {
        return createOrder(orderType, OrderStatus.WAITING, orderLineItems, deliveryAddress);
    }

    public static Order createOrder(OrderType orderType, OrderStatus orderStatus, List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order order = new Order();
        order.setId(uuid);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(uuid);
        return order;
    }

    public static OrderLineItem createOrderLineItem(long quantity, BigDecimal price, Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
