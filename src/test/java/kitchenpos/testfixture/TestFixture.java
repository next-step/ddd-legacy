package kitchenpos.testfixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestFixture {
    private TestFixture() {
    }


    public static MenuGroup createMenuGroup(String name) {
        return createMenuGroup(UUID.randomUUID(), name);
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        var menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static Menu createMenu(
            String name,
            long price
    ) {
        return createMenu(name, price, true);
    }

    public static Menu createMenu(
            String name,
            boolean displayed
    ) {
        return createMenu(name, 10000L, displayed);
    }

    public static Menu createMenu(
            String name,
            Long price,
            boolean displayed
    ) {
        var menuGroup = createMenuGroup("메뉴그룹");
        return createMenu(UUID.randomUUID(), name, price, displayed, menuGroup.getId(), menuGroup);
    }

    public static Menu createMenu(
            String name,
            Long price,
            boolean displayed,
            MenuGroup menuGroup,
            List<Product> products
    ) {

        List<MenuProduct> menuProducts = IntStream.range(0, products.size())
                .mapToObj(i -> createMenuProduct(products.get(i), i, 1))
                .collect(Collectors.toList());

        return createMenu(UUID.randomUUID(), name, price, displayed, menuGroup.getId(), menuGroup, menuProducts);
    }

    public static MenuProduct createMenuProduct(
            Product product,
            long seq,
            int quantity
    ) {
        var menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }


    public static Menu createMenu(
            UUID id,
            String name,
            Long price,
            boolean displayed,
            UUID menuGroupId,
            MenuGroup menuGroup
    ) {
        return createMenu(id, name, price, displayed, menuGroupId, menuGroup, Collections.emptyList());
    }

    public static Menu createMenu(
            UUID id,
            String name,
            Long price,
            boolean displayed,
            UUID menuGroupId,
            MenuGroup menuGroup,
            List<MenuProduct> menuProducts
    ) {
        var menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu copy(Menu request) {
        var menu = new Menu();
        menu.setId(request.getId());
        menu.setName(request.getName());
        menu.setPrice(request.getPrice());
        menu.setDisplayed(request.isDisplayed());
        menu.setMenuGroupId(request.getMenuGroupId());
        menu.setMenuGroup(request.getMenuGroup());
        return menu;
    }

    public static Order copy(Order order) {
        var copiedOrder = new Order();
        copiedOrder.setId(order.getId());
        copiedOrder.setOrderDateTime(order.getOrderDateTime());
        copiedOrder.setStatus(order.getStatus());
        copiedOrder.setOrderLineItems(order.getOrderLineItems());
        copiedOrder.setOrderTableId(order.getOrderTableId());
        copiedOrder.setDeliveryAddress(order.getDeliveryAddress());
        copiedOrder.setType(order.getType());
        copiedOrder.setOrderTable(order.getOrderTable());
        return copiedOrder;
    }

    public static Order createOrder(OrderStatus status, OrderType type) {
        var orderTable = createOrderTable("테이블명", 4);

        return createdOrder(
                UUID.randomUUID(),
                LocalDateTime.now(),
                status,
                List.of(
                        createOrderLineItem(createMenu("후라이드", 16000L), 16000L, 1),
                        createOrderLineItem(createMenu("양념치킨", 26000L), 26000L, 5)
                ),
                UUID.randomUUID(),
                "서울시 강남구 역삼동",
                type,
                orderTable
        );
    }

    public static Order createdOrder(
            UUID id,
            LocalDateTime orderDateTime,
            OrderStatus status,
            List<OrderLineItem> orderLineItems,
            UUID orderTableId,
            String deliveryAddress,
            OrderType type,
            OrderTable orderTable
    ) {
        var order = new Order();
        order.setId(id);
        order.setOrderDateTime(orderDateTime);
        order.setStatus(status);
        order.setOrderLineItems(orderLineItems);
        order.setOrderTableId(orderTableId);
        order.setDeliveryAddress(deliveryAddress);
        order.setType(type);
        order.setOrderTable(orderTable);
        return order;
    }

    public static OrderTable createOrderTable(
            UUID id,
            String name,
            int numberOfGuests,
            boolean occupied
    ) {
        var orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        return orderTable;
    }

    public static OrderTable createOrderTable(
            String name,
            int numberOfGuests
    ) {
        return createOrderTable(UUID.randomUUID(), name, numberOfGuests, true);
    }

    public static OrderTable copy(OrderTable orderTable) {
        var copiedOrderTable = new OrderTable();
        copiedOrderTable.setId(orderTable.getId());
        copiedOrderTable.setNumberOfGuests(orderTable.getNumberOfGuests());
        copiedOrderTable.setName(orderTable.getName());
        copiedOrderTable.setOccupied(orderTable.isOccupied());
        return copiedOrderTable;
    }

    public static OrderLineItem createOrderLineItem(
            Menu menu,
            long price,
            int quantity
    ) {
        var orderLineItem = new OrderLineItem();

        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static Product createProduct(String name, long price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    public static Product createProduct(UUID id, String name, long price) {
        var product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product copy(Product product) {
        var copiedProduct = new Product();
        copiedProduct.setId(product.getId());
        copiedProduct.setName(product.getName());
        copiedProduct.setPrice(product.getPrice());
        return copiedProduct;
    }
}
