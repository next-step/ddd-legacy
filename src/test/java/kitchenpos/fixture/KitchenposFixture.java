package kitchenpos.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitchenposFixture {

    public static MenuGroup menuGroup() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("추천메뉴");
        return menuGroup;
    }

    public static Product chickenProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(10000));
        product.setName("치킨");
        return product;
    }

    public static Product pastaProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(15000));
        product.setName("파스타");
        return product;
    }

    public static Menu menu(MenuGroup menuGroup, Product... products) {
        List<MenuProduct> menuProducts = Arrays.stream(products)
                .map(product -> KitchenposFixture.menuProduct(product, 1))
                .collect(Collectors.toList());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(25000));
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        menu.setName("치킨 파스타 정식");
        menu.setMenuGroup(menuGroup);
        return menu;
    }

    public static Menu menuWithMenuProduct(MenuGroup menuGroup, MenuProduct... menuProducts) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(BigDecimal.valueOf(25000));
        menu.setMenuProducts(Arrays.asList(menuProducts));
        menu.setDisplayed(true);
        menu.setName("치킨 파스타 정식");
        menu.setMenuGroup(menuGroup);
        return menu;
    }

    public static MenuProduct menuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static OrderTable orderTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        return orderTable;
    }

    public static Order orderEatIn(Menu... menus) {
        OrderTable orderTable = orderTable();

        Order order = commonOrder(OrderType.EAT_IN, menus);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        return order;
    }

    public static Order orderTakeout(Menu... menus) {
        return commonOrder(OrderType.TAKEOUT, menus);
    }

    public static Order orderTakeoutWithOrderItems(OrderLineItem... items) {
        return commonOrder(OrderType.TAKEOUT, items);
    }

    public static Order orderDeliveryWithOrderItems(OrderLineItem... items) {
        Order order = commonOrder(OrderType.DELIVERY, items);
        order.setDeliveryAddress("강원도 양양군 현북면 남대천로 1418");
        return order;
    }

    public static Order orderDelivery(Menu... menus) {
        Order order = commonOrder(OrderType.DELIVERY, menus);
        order.setDeliveryAddress("강원도 양양군 현북면 남대천로 1418");
        return order;
    }

    private static Order commonOrder(OrderType type, Menu... menus) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(orderLineItems(menus));
        order.setType(type);
        return order;
    }

    private static Order commonOrder(OrderType type, OrderLineItem... items) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderLineItems(Arrays.asList(items));
        order.setType(type);
        return order;
    }

    private static List<OrderLineItem> orderLineItems(Menu... menus) {
        return Arrays.stream(menus)
                .map(menu -> KitchenposFixture.orderLineItem(menu, 1))
                .collect(Collectors.toList());
    }

    public static OrderLineItem orderLineItem(Menu menu, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }
}
