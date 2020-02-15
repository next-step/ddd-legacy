package kitchenpos.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class TestFixtures {

    public static Menu veryCheapMenu(List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("엄청이득메뉴");
        menu.setPrice(new BigDecimal(10000));
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu veryExpensiveMenu(List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("호갱메뉴");
        menu.setPrice(new BigDecimal(100000));
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static MenuProduct menuProduct(Long productId, Long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(1L);
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static Product veryExpensiveProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("엄청비싼음식");
        product.setPrice(new BigDecimal(1000000));
        return product;
    }

    public static Menu customPriceMenu(BigDecimal price) {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("엄청이득메뉴");
        menu.setPrice(price);
        menu.setMenuGroupId(1L);
        menu.setMenuProducts(Collections.emptyList());
        return menu;
    }

    public static Product customPriceProduct(BigDecimal price) {
        Product product = new Product();
        product.setId(1L);
        product.setName("얼마인지모를음식");
        product.setPrice(price);
        return product;
    }

    public static MenuGroup menuGroup() {
        MenuGroup menuGruop = new MenuGroup();
        menuGruop.setId(1L);
        menuGruop.setName("상상도못한조합");
        return menuGruop;
    }

    public static OrderLineItem orderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(1L);
        orderLineItem.setQuantity(2);
        return orderLineItem;
    }

    public static OrderTable orderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(1L);
        orderTable.setEmpty(false);
        return orderTable;
    }

    public static Order order(List<OrderLineItem> orderLineItems, Long orderTableId) {
        Order order = new Order();
        order.setId(1L);
        order.setOrderLineItems(orderLineItems);
        order.setOrderedTime(LocalDateTime.now());
        order.setOrderTableId(orderTableId);
        order.setOrderStatus(OrderStatus.COOKING.name());
        return order;
    }

    public static OrderTable customOrderTable(Long tableGruopId, boolean isEmpty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(4);
        orderTable.setTableGroupId(tableGruopId);
        orderTable.setEmpty(isEmpty);
        return orderTable;
    }

    public static OrderTable customGuestOfNumberTable(int numberOfGuest) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(1L);
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setTableGroupId(1L);
        orderTable.setEmpty(false);
        return orderTable;
    }

    public static Order customStatusOrderList(OrderStatus orderStatus, List<OrderLineItem> orderLineItems, OrderTable orderTable) {
        Order expected = new Order();
        expected.setId(1L);
        expected.setOrderLineItems(orderLineItems);
        expected.setOrderedTime(LocalDateTime.now());
        expected.setOrderTableId(orderTable.getId());
        expected.setOrderStatus(orderStatus.name());
        return expected;
    }

    public static TableGroup tableGroup(List<OrderTable> orderTables) {
        TableGroup tableGroup = new TableGroup();
        tableGroup.setId(1L);
        tableGroup.setCreatedDate(LocalDateTime.now());
        tableGroup.setOrderTables(orderTables);
        return tableGroup;
    }
}
