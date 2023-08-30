package kitchenpos;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Fixtures {

    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);

        return product;
    }

    public static MenuGroup createMenuGroup(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static Menu createMenu(MenuGroup menuGroup, List<MenuProduct> menuProducts, BigDecimal price) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName("메뉴이름");
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static OrderTable createOrderTable(String name, boolean occupied) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(0);

        return orderTable;
    }

    public static MenuProduct createMenuProduct(Long seq, Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Order createOrder(OrderStatus orderStatus, OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        OrderTable orderTable = createOrderTable("테이블", false);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setStatus(orderStatus);
        order.setType(orderType);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);

        return order;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(1L);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
}
