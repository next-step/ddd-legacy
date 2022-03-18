package kitchenpos.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

public final class Fixtures {

    private Fixtures() {
        throw new RuntimeException("생성할 수 없는 클래스");
    }

    public static Product createProduct(BigDecimal price) {
        return createProduct("좋은말", price);
    }

    public static Product createProduct(String name, BigDecimal price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    public static Product createProduct(UUID productId, BigDecimal price) {
        return createProduct(productId, "좋은말", price);
    }

    public static Product 일원짜리_Product(UUID productId) {
        return createProduct(productId, "좋은말", BigDecimal.ONE);
    }

    public static Product createProduct(UUID productId, String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(productId);
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    public static Product 십원_상품(UUID productId) {
        return createProduct(productId, "십원짜리", BigDecimal.TEN);
    }

    public static Product 십원_상품() {
        return createProduct(UUID.randomUUID(), "십원짜리", BigDecimal.TEN);
    }

    public static MenuProduct createMenuProduct(Product product) {
        return createMenuProduct(0L, product, 2);
    }

    public static MenuProduct createMenuProduct(
        Long seq,
        Product product,
        long quantity
    ) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static MenuProduct 십원짜리_상품_2개인_menuProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(2);
        menuProduct.setProduct(십원_상품());
        return menuProduct;
    }

    public static OrderTable createOrderTable(
        UUID id,
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }

    public static OrderTable createOrderTable(
        String name,
        int numberOfGuests,
        boolean empty
    ) {
        return createOrderTable(
            UUID.randomUUID(),
            name,
            numberOfGuests,
            empty
        );
    }

    public static MenuGroup createMenuGroup(UUID id, String name) {
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    public static MenuGroup createMenuGroup(String name) {
        return createMenuGroup(UUID.randomUUID(), name);
    }

    public static Menu createMenu(
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        MenuProduct... menuProducts
    ) {
        return createMenu(price, display, menuProductId, Arrays.asList(menuProducts));
    }

    public static Menu createMenu(
        BigDecimal price,
        String name,
        boolean display,
        UUID menuProductId,
        MenuProduct... menuProducts
    ) {
        return createMenu(
            UUID.randomUUID(),
            price,
            name,
            display,
            menuProductId,
            Arrays.asList(menuProducts)
        );
    }

    public static Menu createMenu(
        UUID menuId,
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        MenuProduct... menuProducts
    ) {
        return createMenu(
            menuId,
            price,
            "좋은말",
            display,
            menuProductId,
            Arrays.asList(menuProducts)
        );
    }

    public static Menu createMenu(
        BigDecimal price,
        boolean display,
        UUID menuProductId,
        List<MenuProduct> menuProducts
    ) {
        return createMenu(
            UUID.randomUUID(),
            price,
            "좋은말",
            display,
            menuProductId,
            menuProducts
        );
    }

    public static Menu createMenu(
        UUID menuId,
        BigDecimal price,
        String name,
        boolean display,
        UUID menuProductId,
        List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setPrice(price);
        menu.setName(name);
        menu.setDisplayed(display);
        menu.setMenuGroupId(menuProductId);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static OrderLineItem createOrderLineItem(Menu menu, long quantity, BigDecimal price) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    public static Order createOrder(OrderType type, UUID orderTableId, OrderLineItem... orderLineItems) {
        final Order order = new Order();
        order.setType(type);
        order.setOrderTableId(orderTableId);
        order.setOrderLineItems(Arrays.asList(orderLineItems));
        return order;
    }

    public static Order createOrder(OrderType type, String deliveryAddress, OrderLineItem... orderLineItems) {
        return createOrder(type, deliveryAddress, Arrays.asList(orderLineItems));
    }

    public static Order createOrder(OrderType type, String deliveryAddress, List<OrderLineItem> orderLineItems) {
        final Order order = new Order();
        order.setType(type);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static Order createOrder(
        UUID id,
        OrderType type,
        OrderStatus status,
        LocalDateTime orderDateTime,
        String deliveryAddress,
        OrderTable orderTable,
        OrderLineItem... orderLineItems
    ) {
        final Order order = new Order();
        order.setId(id);
        order.setType(type);
        order.setStatus(status);
        order.setOrderDateTime(orderDateTime);
        order.setOrderLineItems(Arrays.asList(orderLineItems));
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order createEatInOrder(
        UUID id,
        OrderStatus status,
        LocalDateTime orderDateTime,
        OrderTable orderTable,
        OrderLineItem... orderLineItems
    ) {
        return createOrder(
            id,
            OrderType.EAT_IN,
            status,
            orderDateTime,
            null,
            orderTable,
            orderLineItems
        );
    }

    public static Order createDeliveryOrder(
        UUID id,
        OrderStatus status,
        LocalDateTime orderDateTime,
        String deliveryAddress,
        OrderLineItem... orderLineItems
    ) {
        return createOrder(
            id,
            OrderType.DELIVERY,
            status,
            orderDateTime,
            deliveryAddress,
            null,
            orderLineItems
        );
    }

    public static Order createTakeOutOrder(
        UUID id,
        OrderStatus status,
        LocalDateTime orderDateTime,
        OrderLineItem... orderLineItems
    ) {
        return createOrder(
            id,
            OrderType.TAKEOUT,
            status,
            orderDateTime,
            null,
            null,
            orderLineItems
        );
    }
}
