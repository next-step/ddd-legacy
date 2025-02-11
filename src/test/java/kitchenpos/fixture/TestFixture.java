package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestFixture {

    public static Product makeTestProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static MenuProduct makeTestMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(1);
        menuProduct.setSeq(1L);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static Menu makeTestMenu(String name, BigDecimal price, MenuGroup menuGroup, MenuProduct menuProduct) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }

    public static MenuGroup makeTestMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static OrderTable makeTestOrderTable(String name, int numberOfGuest) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setOccupied(false);
        return orderTable;
    }

    public static Order makeTestEatInOrder(OrderStatus orderStatus, OrderLineItem orderLineItem, OrderTable orderTable) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order makeTestTakeOutOrder(OrderStatus orderStatus, OrderLineItem orderLineItem) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.TAKEOUT);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        return order;
    }

    public static Order makeTestDeliveryOrder(OrderStatus orderStatus, OrderLineItem orderLineItem, String address) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(List.of(orderLineItem));
        order.setDeliveryAddress(address);
        return order;
    }

    public static OrderLineItem makeTestOrderLineItem(BigDecimal price, Menu menu, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setPrice(price);
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }
}
