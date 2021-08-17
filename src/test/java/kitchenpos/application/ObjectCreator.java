package kitchenpos.application;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ObjectCreator {
    public List<MenuProduct> createMenuProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(2L);
        return Collections.singletonList(menuProduct);
    }

    public MenuGroup createMenuGroup(String name) {
        MenuGroup request = new MenuGroup();
        request.setName(name);
        return request;
    }

    public Menu createMenu(List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName("메뉴");
        menu.setPrice(BigDecimal.valueOf(1000L));
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    public Menu createMenu(boolean isDisPlayed, long price) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(isDisPlayed);
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

    public List<Menu> getMenus(Menu... menus) {
        return Arrays.asList(menus);
    }

    public Menu createRequest(String name, BigDecimal price, UUID menuGroupId, List<MenuProduct> menuProducts) {
        Menu request = new Menu();
        request.setName(name);
        request.setPrice(price);
        request.setMenuGroupId(menuGroupId);
        request.setMenuProducts(menuProducts);
        return request;
    }

    public Menu createChangeMenuRequest(BigDecimal price) {
        Menu request = new Menu();
        request.setPrice(price);
        return request;
    }

    public Product createProductRequest(String name, BigDecimal price) {
        Product request = new Product();
        request.setName(name);
        request.setPrice(price);
        return request;
    }

    public Product createChangePriceRequest(BigDecimal price) {
        Product request = new Product();
        request.setPrice(price);
        return request;
    }

    public Product createProduct(long productPrice) {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(productPrice));
        return product;
    }

    public List<MenuProduct> createMenuProducts(List<Product> products, long quantity) {
        return products.stream()
                .map(product -> getMenuProduct(product, quantity))
                .collect(Collectors.toList());
    }

    public MenuProduct getMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public Order completeOrder(UUID id, OrderType orderType, String deliveryAddress, UUID orderTableId,
                               OrderStatus orderStatus, List<OrderLineItem> orderLineItems) {
        Order order = createOrder(id, orderType, deliveryAddress, orderTableId, orderStatus, orderLineItems);
        order.setOrderTable(getOrderTable(orderTableId, false));
        return order;
    }

    public Order createOrder(UUID id, OrderType orderType, String deliveryAddress, UUID orderTableId,
                             OrderStatus orderStatus, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableId);
        order.setStatus(orderStatus);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public Order createOrderRequest(OrderType orderType, String deliveryAddress, UUID orderTableId,
                                    List<OrderLineItem> orderLineItems) {
        Order request = new Order();
        request.setType(orderType);
        request.setOrderLineItems(orderLineItems);
        request.setDeliveryAddress(deliveryAddress);
        request.setOrderTableId(orderTableId);
        return request;
    }

    public List<OrderLineItem> getOrderLineItems(List<Menu> menus) {
        return getOrderLineItems(menus, 1);
    }

    public List<OrderLineItem> getOrderLineItems(List<Menu> menus, long quantity) {
        return menus.stream()
                .map(menu -> {
                    OrderLineItem orderLineItem = new OrderLineItem();
                    orderLineItem.setMenu(menu);
                    orderLineItem.setPrice(menu.getPrice());
                    orderLineItem.setQuantity(quantity);
                    return orderLineItem;
                }).collect(Collectors.toList());
    }

    public OrderTable getOrderTable(UUID orderTableId, boolean isEmpty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableId);
        orderTable.setNumberOfGuests(3);
        orderTable.setEmpty(isEmpty);
        return orderTable;
    }

    public OrderTable getExitTable(OrderTable exitTable) {
        exitTable.setNumberOfGuests(3);
        exitTable.setEmpty(false);
        return exitTable;
    }

    public OrderTable getChangeNumberOfGuestsRequest(int numberOfGuest) {
        OrderTable request = new OrderTable();
        request.setNumberOfGuests(numberOfGuest);
        return request;
    }

    public OrderTable createRequest(String name) {
        OrderTable request = new OrderTable();
        request.setName(name);
        return request;
    }

    public OrderTable defaultOrderTable(UUID id, String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(true);
        return orderTable;
    }
}
