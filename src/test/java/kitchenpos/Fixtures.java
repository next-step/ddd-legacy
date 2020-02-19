package kitchenpos;

import kitchenpos.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Fixtures {

    public static Menu getMenu(long id, BigDecimal price, long menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Product getProduct(long id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static MenuGroup getMenuGroup(long id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuProduct getMenuProduct(long seq, long productId, long quantity, long menuId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setMenuId(productId);
        menuProduct.setProductId(menuId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct getMenuProduct(long seq, Product product, long quantity, Menu menu) {
        return getMenuProduct(seq, product.getId(), quantity, menu.getId());
    }

    public static Order getOrder(long id, LocalDateTime localDateTime, List<OrderLineItem> orderLineItems, String status, long tableId) {
        Order order = new Order();
        order.setId(id);
        order.setOrderedTime(localDateTime);
        order.setOrderLineItems(orderLineItems);
        order.setOrderStatus(status);
        order.setOrderTableId(tableId);
        return order;
    }

    public static OrderLineItem getOrderLineItem(long seq, long menuId, long orderId, long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSeq(seq);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setOrderId(orderId);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }
}
