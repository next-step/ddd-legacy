package kitchenpos.application.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static Order createOrderRequest(OrderType type, int quantity, BigDecimal price, boolean isDisplayed) {
        Product product = ProductFixture.createProduct(BigDecimal.valueOf(9_000), "Product");
        MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 2);
        Menu menu = MenuFixture.createMenu(menuProduct, MenuGroupFixture.createMenuGroup(), 20_000, "Menu", isDisplayed);
        OrderLineItem orderLineItem = createOrderLineItem(menu, quantity, price);

        Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(List.of(orderLineItem));

        return order;
    }

    public static Order createOrderRequest(OrderType type, int quantity, BigDecimal price, boolean isDisplayed, OrderTable orderTable) {
        Order order = createOrderRequest(type, quantity, price, isDisplayed);
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order createOrderRequest(OrderType type, int quantity, BigDecimal price, boolean isDisplayed, String address) {
        Order order = createOrderRequest(type, quantity, price, isDisplayed);
        order.setDeliveryAddress(address);
        return order;
    }

    public static Order createOrderRequest(OrderType type, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createSavedOrder(Order request) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(request.getType());
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(request.getOrderLineItems());
        return order;
    }

    public static Order createOrder(OrderStatus status, OrderType type, String deliveryAddress) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(status);
        order.setType(type);
        order.setOrderLineItems(createOrderLineItems());
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    public static Order createOrderWithTable(OrderStatus status, OrderType type, OrderTable orderTable) {
        Order order = createOrder(status, type, null);
        order.setOrderTable(orderTable);
        return order;
    }

    public static List<Order> createMockOrders() {
        return List.of(
                createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, null),
                createOrder(OrderStatus.WAITING, OrderType.TAKEOUT, null)
        );
    }

    public static List<OrderLineItem> createOrderLineItems() {
        Product product = ProductFixture.createProduct(BigDecimal.valueOf(9_000), "Product");
        MenuProduct menuProduct = MenuProductFixture.createMenuProduct(product, 2);
        Menu menu = MenuFixture.createMenu(menuProduct, MenuGroupFixture.createMenuGroup(), 20_000, "Menu", true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, 2, BigDecimal.valueOf(20_000));

        return List.of(orderLineItem);
    }

    public static OrderLineItem createOrderLineItem(Menu menu, int quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
