package kitchenpos.application.support;

import kitchenpos.domain.*;
import org.aspectj.weaver.ast.Or;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestFixture {
    // menuGroup field
    public final static UUID DEFAULT_MENU_GROUP_ID = UUID.randomUUID();
    public final static String DEFAULT_MENU_GROUP_NAME = "메뉴그룹";

    // product field
    public final static UUID PRODUCT_ID = UUID.randomUUID();
    public final static String PRODUCT_NAME = "후라이드 치킨";
    public final static BigDecimal PRODUCT_PRICE = BigDecimal.TEN;

    // menu field
    public static final UUID MENU_ID = UUID.randomUUID();
    public static final String MENU_NAME = "첫번째메뉴";
    public static final BigDecimal MENU_PRICE = BigDecimal.TEN;

    // order table fields
    public static final UUID ORDER_TABLE_ID = UUID.randomUUID();
    public static final String ORDER_TABLE_NAME = "1번 테이블";
    public static final int ORDER_TABLE_GUEST = 0;
    public static final boolean ORDER_TABLE_OCCUPIED = true;

    // order fields
    public final static UUID ORDER_ID = UUID.randomUUID();
    public final static OrderStatus ORDER_STATUS = OrderStatus.WAITING;
    public final static String ORDER_ADDRESS = "서울특별시";

    public static MenuGroup createGeneralMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(DEFAULT_MENU_GROUP_ID);
        menuGroup.setName(DEFAULT_MENU_GROUP_NAME);

        return menuGroup;
    }

    public static MenuGroup createMenuGroupWithName(final String name) {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);

        return menuGroup;
    }

    public static Menu createGeneralMenu() {
        Menu menu = new Menu();

        menu.setId(MENU_ID);
        menu.setName(MENU_NAME);
        menu.setPrice(MENU_PRICE);
        menu.setDisplayed(true);

        MenuGroup menuGroup = createGeneralMenuGroup();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);

        menu.setMenuProducts(createMenuProducts());

        return menu;
    }

    public static Menu createMenuWithName(final String name) {
        Menu menu = new Menu();

        menu.setId(MENU_ID);
        menu.setName(name);
        menu.setPrice(MENU_PRICE);
        menu.setDisplayed(true);

        MenuGroup menuGroup = createGeneralMenuGroup();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);

        menu.setMenuProducts(createMenuProducts());

        return menu;
    }

    public static Menu createMenuWithPrice(final Long price) {
        Menu menu = new Menu();

        menu.setId(MENU_ID);
        menu.setName(MENU_NAME);
        if (price != null) {
            menu.setPrice(BigDecimal.valueOf(price));
        }
        menu.setDisplayed(true);

        MenuGroup menuGroup = createGeneralMenuGroup();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);

        menu.setMenuProducts(createMenuProducts());
        return menu;
    }

    public static Menu createMenuWithDisplayed(final boolean displayed) {
        Menu menu = new Menu();

        menu.setId(MENU_ID);
        menu.setName(MENU_NAME);
        menu.setPrice(MENU_PRICE);
        menu.setDisplayed(displayed);

        MenuGroup menuGroup = createGeneralMenuGroup();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);

        menu.setMenuProducts(createMenuProducts());

        return menu;
    }

    public static Menu createMenuWithMenuGroup(final MenuGroup menuGroup) {
        Menu menu = new Menu();

        menu.setId(MENU_ID);
        menu.setName(MENU_NAME);
        menu.setPrice(MENU_PRICE);
        menu.setDisplayed(true);

        if (menuGroup != null) {
            menu.setMenuGroupId(menuGroup.getId());
            menu.setMenuGroup(menuGroup);
        }

        menu.setMenuProducts(createMenuProducts());

        return menu;
    }

    public static Menu createMenuWithMenuProducts(final List<MenuProduct> menuProducts) {
        Menu menu = new Menu();

        menu.setId(MENU_ID);
        menu.setName(MENU_NAME);
        menu.setPrice(MENU_PRICE);
        menu.setDisplayed(true);

        MenuGroup menuGroup = createGeneralMenuGroup();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);

        menu.setMenuProducts(menuProducts);

        return menu;
    }

    public static List<MenuProduct> createMenuProducts() {
        MenuProduct menuProduct = new MenuProduct();

        Product product = createGeneralProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1);

        return List.of(menuProduct);
    }

    public static List<MenuProduct> createMenuProductsWithProduct(Product product) {
        MenuProduct menuProduct = new MenuProduct();

        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1);

        return List.of(menuProduct);
    }

    public static List<MenuProduct> createMenuProductsWithQuantity(final int quantity) {
        MenuProduct menuProduct = new MenuProduct();

        Product product = createGeneralProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(quantity);

        return List.of(menuProduct);
    }

    public static Product createGeneralProduct() {
        Product product = new Product();

        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setPrice(PRODUCT_PRICE);

        return product;
    }

    public static Product createProductWithName(final String name) {
        Product product = new Product();

        product.setId(PRODUCT_ID);
        product.setName(name);
        product.setPrice(PRODUCT_PRICE);

        return product;
    }

    public static Product createProductWithPrice(final Long price) {
        Product product = new Product();

        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        if (price != null) {
            product.setPrice(BigDecimal.valueOf(price));
        }

        return product;
    }

    public static Order createOrderWithOrderType(final OrderType orderType) {
        Order order = new Order();

        order.setId(ORDER_ID);
        order.setStatus(ORDER_STATUS);
        order.setType(orderType);

        OrderTable orderTable = createGeneralOrderTable();
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);

        order.setOrderLineItems(createGeneralOrderLineItems());
        order.setDeliveryAddress(ORDER_ADDRESS);

        return order;
    }

    public static Order createOrderWithTypeAndStatus(final OrderType orderType, final OrderStatus orderStatus) {
        Order order = new Order();

        order.setId(ORDER_ID);
        order.setStatus(orderStatus);
        order.setType(orderType);

        OrderTable orderTable = createGeneralOrderTable();
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);

        order.setOrderLineItems(createGeneralOrderLineItems());
        order.setDeliveryAddress(ORDER_ADDRESS);

        return order;
    }

    public static Order createOrderWithAddress(final String address) {
        Order order = new Order();

        order.setId(ORDER_ID);
        order.setStatus(OrderStatus.WAITING);
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress(address);

        OrderTable orderTable = createGeneralOrderTable();
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);

        order.setOrderLineItems(createGeneralOrderLineItems());
        order.setDeliveryAddress(ORDER_ADDRESS);

        return order;
    }

    public static Order createOrderWithOrderLineItems(final List<OrderLineItem> orderLineItems) {
        Order order = new Order();

        order.setId(ORDER_ID);
        order.setStatus(OrderStatus.WAITING);
        order.setType(OrderType.DELIVERY);
        order.setDeliveryAddress(ORDER_ADDRESS);

        OrderTable orderTable = createGeneralOrderTable();
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);

        order.setOrderLineItems(orderLineItems);

        return order;
    }

    public static Order createOrderWithTypeAndOrderLineItems(final OrderType orderType, final List<OrderLineItem> orderLineItems) {
        Order order = new Order();

        order.setId(ORDER_ID);
        order.setStatus(OrderStatus.WAITING);
        order.setType(orderType);
        order.setDeliveryAddress(ORDER_ADDRESS);

        OrderTable orderTable = createGeneralOrderTable();
        order.setOrderTableId(orderTable.getId());
        order.setOrderTable(orderTable);

        order.setOrderLineItems(orderLineItems);

        return order;
    }

    public static List<OrderLineItem> createGeneralOrderLineItems() {
        OrderLineItem orderLineItem = new OrderLineItem();

        orderLineItem.setSeq(1L);
        Menu menu = createGeneralMenu();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(1L);
        orderLineItem.setPrice(BigDecimal.TEN);

        return List.of(orderLineItem);
    }

    public static List<OrderLineItem> createGeneralOrderLineItemsWithQuantity(final int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();

        orderLineItem.setSeq(1L);
        Menu menu = createGeneralMenu();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(BigDecimal.ONE);

        return List.of(orderLineItem);
    }

    public static OrderTable createGeneralOrderTable() {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setName(ORDER_TABLE_NAME);
        orderTable.setOccupied(ORDER_TABLE_OCCUPIED);
        orderTable.setNumberOfGuests(ORDER_TABLE_GUEST);

        return orderTable;
    }

    public static OrderTable createOrderTableWithName(final String name) {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setName(name);
        orderTable.setOccupied(ORDER_TABLE_OCCUPIED);
        orderTable.setNumberOfGuests(ORDER_TABLE_GUEST);

        return orderTable;
    }

    public static OrderTable createOrderTableWithOccupied(final boolean occupied) {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setName(ORDER_TABLE_NAME);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(ORDER_TABLE_GUEST);

        return orderTable;
    }

    public static OrderTable createOrderTableWithGuest(final int guest) {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(ORDER_TABLE_ID);
        orderTable.setName(ORDER_TABLE_NAME);
        orderTable.setOccupied(ORDER_TABLE_OCCUPIED);
        orderTable.setNumberOfGuests(guest);

        return orderTable;
    }

}
