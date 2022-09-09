package kitchenpos;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FixtureFactory {


    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Menu createMenu(String name, BigDecimal price, boolean display, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(display);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static OrderTable createOrderTable(String name) {
        return createOrderTable(name, 0, false);
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, BigDecimal price, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static Order createEatInOrder(OrderStatus orderStatus, OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.EAT_IN, orderStatus, null, orderTable, orderTable.getId(), orderLineItems);
    }

    public static Order createEatInOrder(OrderTable orderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.EAT_IN, OrderStatus.WAITING, null, orderTable, orderTable.getId(), orderLineItems);
    }

    public static Order createDeliveryOrder(String deliveryAddress, OrderStatus orderStatus, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.DELIVERY, orderStatus, deliveryAddress, null, null, orderLineItems);
    }

    public static Order createDeliveryOrder(String deliveryAddress, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.DELIVERY, OrderStatus.WAITING, deliveryAddress, null, null, orderLineItems);
    }

    public static Order createTakeOutOrder(OrderStatus orderStatus, List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.TAKEOUT, orderStatus, null, null, null, orderLineItems);
    }

    public static Order createTakeOutOrder(List<OrderLineItem> orderLineItems) {
        return createOrder(OrderType.TAKEOUT, OrderStatus.WAITING, null, null, null, orderLineItems);
    }

    public static Order createOrder(OrderType orderType, OrderStatus orderStatus, String address, OrderTable orderTable, UUID orderTableId, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderTableId(orderTableId);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setDeliveryAddress(address);
        order.setOrderTable(orderTable);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

}
