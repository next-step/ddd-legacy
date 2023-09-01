package kitchenpos.application.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OrderTestFixture {
    public Order createOrder(OrderStatus status, OrderType type){
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(null);
        order.setOrderTableId(null);
        order.setOrderDateTime(null);
        order.setOrderLineItems(null);
        order.setDeliveryAddress("서울시");
        order.setStatus(status);
        order.setType(type);
        return order;
    }

    public Order createOrder(OrderStatus status, OrderType type, List<OrderLineItem> orderLineItems){
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(null);
        order.setOrderTableId(null);
        order.setOrderDateTime(null);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(null);
        order.setStatus(status);
        order.setType(type);
        return order;
    }

    public Order createOrder(OrderStatus status, OrderType type, List<OrderLineItem> orderLineItems, OrderTable orderTable){
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(orderTable);
        order.setOrderTableId(null);
        order.setOrderDateTime(null);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(null);
        order.setStatus(status);
        order.setType(type);
        return order;
    }

    public Order createOrder(OrderStatus status, OrderType type, OrderTable orderTable){
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(orderTable);
        order.setOrderTableId(null);
        order.setOrderDateTime(null);
        order.setOrderLineItems(null);
        order.setDeliveryAddress(null);
        order.setStatus(status);
        order.setType(type);
        return order;
    }


    public OrderLineItem createOrderLineItem(long quantity){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(null);
        orderLineItem.setPrice(null);
        orderLineItem.setSeq(null);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(null);
        return orderLineItem;
    }
    public OrderLineItem createOrderLineItem(Menu menu, long quantity){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(null);
        orderLineItem.setSeq(null);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }


    public OrderLineItem createOrderLineItem(Menu menu, BigDecimal price, long quantity){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(price);
        orderLineItem.setSeq(null);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menu.getId());
        return orderLineItem;
    }

    public MenuProduct createMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(UUID.randomUUID());
        return menuProduct;
    }
}
