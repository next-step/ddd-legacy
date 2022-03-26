package kitchenpos;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TestFixtures {


    public static OrderTable createEmptyTableWithName(String name) {
        OrderTable orderTable = createOrderTable(name);
        orderTable.setEmpty(true);
        return orderTable;
    }

    public static Order createOrderRequest(OrderType orderType, List<OrderLineItem> orderLineItems, OrderTable orderTable) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setDeliveryAddress("어느시 어느동");
        return order;
    }
    public static  Order createOrderRequestWithoutOrderTable(OrderType orderType, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress("어느시 어느동");
        return order;
    }

    public static Order createOrderRequest(OrderType orderType, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress("어느시 어느동");
        return order;
    }

    public static OrderTable createOrderTable(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable createOrderTableRequest(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }


    public static Menu createMenuWithMenuProductsAndGroup(String name, List<MenuProduct> menuProducts, MenuGroup menuGroup) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(10000));
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName(name);
        return menu;
    }

    public static Menu createMenu(String name, int price) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static Product updateProductRequest(Product product, int price) {
        Product updateProductRequest = new Product();
        updateProductRequest.setId(product.getId());
        updateProductRequest.setName(product.getName());
        updateProductRequest.setPrice(BigDecimal.valueOf(price));
        return updateProductRequest;
    }

    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Menu createMenuRequest(int price) {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }
    public static Menu createMenuRequest(BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        return menu;
    }


    public static Product createProductRequest(final String name) {
        return createProductRequest(name, 18_000);
    }

    public static Product createProductRequest(final int price) {
        return createProductRequest("후라이드", price);
    }

    public static Product createProductRequest(final String name, final int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product createProduct(final String name, final int price) {
        return createProduct(UUID.randomUUID(), name, BigDecimal.valueOf(price));
    }

    public static Product createProduct(final String name, final BigDecimal price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    public static Product createProduct(final UUID id, final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }


    public static MenuProduct createMenuProductRequest(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Menu createMenuRequest(String name, int price) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

    public static OrderLineItem createOrderLineItemRequest(Long orderId, UUID menuId, long quantity, int price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        return orderLineItem;
    }

    public static Menu createMenuRequest(String name, boolean isDisplayed, int price) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

}
