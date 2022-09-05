package kitchenpos.application.support;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestFixture {
    // menuGroup field
    public final static UUID FIRST_MENU_GROUP_ID = UUID.randomUUID();
    public final static String FIRST_MENU_GROUP_NAME = "첫번째 메뉴그룹";
    public final static UUID SECOND_MENU_GROUP_ID = UUID.randomUUID();
    public final static String SECOND_MENU_GROUP_NAME = "두번째 메뉴그룹";

    // product field
    public final static UUID FIRST_PRODUCT_ID = UUID.randomUUID();
    public final static String FIRST_PRODUCT_NAME = "후라이드 치킨";
    public final static BigDecimal FIRST_PRODUCT_PRICE = BigDecimal.TEN;
    public final static UUID SECOND_PRODUCT_ID = UUID.randomUUID();
    public final static String SECOND_PRODUCT_NAME = "후라이드 치킨";
    public final static BigDecimal SECOND_PRODUCT_PRICE = BigDecimal.ONE;

    // menu field
    public static final UUID FIRST_MENU_ID = UUID.randomUUID();
    public static final String FIRST_MENU_NAME = "첫번째메뉴";
    public static final BigDecimal FIRST_MENU_PRICE = BigDecimal.TEN;
    public static final UUID SECOND_MENU_ID = UUID.randomUUID();
    public static final String SECOND_MENU_NAME = "두번째메뉴";
    public static final BigDecimal SECOND_MENU_PRICE = BigDecimal.ONE;

    // order table fields
    public static final UUID FIRST_ORDER_TABLE_ID = UUID.randomUUID();
    public static final String FIRST_ORDER_TABLE_NAME = "1번 테이블";
    public static final int FIRST_ORDER_TABLE_GUEST = 0;
    public static final UUID SECOND_ORDER_TABLE_ID = UUID.randomUUID();
    public static final String SECOND_ORDER_TABLE_NAME = "1번 테이블";
    public static final int SECOND_ORDER_TABLE_GUEST = 0;

    // order fields
    public final static UUID FIRST_ORDER_ID = UUID.randomUUID();
    public final static Long FIRST_ORDER_LINE_ITEM_ID = 1L;
    public final static BigDecimal FIRST_ORDER_PRICE = BigDecimal.TEN;
    public final static int FIRST_ORDER_QUANTITY = 1;
    public final static UUID SECOND_ORDER_ID = UUID.randomUUID();
    public final static Long SECOND_ORDER_LINE_ITEM_ID = 2L;
    public final static BigDecimal SECOND_ORDER_PRICE = BigDecimal.TEN;
    public final static int SECOND_ORDER_QUANTITY = 1;
    public final static String ADDRESS = "서울특별시";

    public static MenuGroup createFirstMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(FIRST_MENU_GROUP_ID);
        menuGroup.setName(FIRST_MENU_GROUP_NAME);

        return menuGroup;
    }

    public static MenuGroup createSecondMenuGroup() {
        MenuGroup menuGroup = new MenuGroup();

        menuGroup.setId(SECOND_MENU_GROUP_ID);
        menuGroup.setName(SECOND_MENU_GROUP_NAME);

        return menuGroup;
    }


    public static List<MenuProduct> createFirstMenuProducts() {
        MenuProduct defaultMenuProduct = new MenuProduct();
        Product product = createFirstProduct();
        defaultMenuProduct.setProductId(product.getId());
        defaultMenuProduct.setQuantity(1);
        defaultMenuProduct.setProduct(product);

        return List.of(defaultMenuProduct);
    }

    public static List<MenuProduct> createSecondMenuProducts() {
        MenuProduct defaultMenuProduct = new MenuProduct();
        Product product = createSecondProduct();
        defaultMenuProduct.setProductId(product.getId());
        defaultMenuProduct.setQuantity(1);
        defaultMenuProduct.setProduct(product);

        return List.of(defaultMenuProduct);
    }

    public static Product createFirstProduct() {
        Product product = new Product();

        product.setId(FIRST_PRODUCT_ID);
        product.setName(FIRST_PRODUCT_NAME);
        product.setPrice(FIRST_PRODUCT_PRICE);

        return product;
    }

    public static Product createSecondProduct() {
        Product product = new Product();

        product.setId(SECOND_PRODUCT_ID);
        product.setName(SECOND_PRODUCT_NAME);
        product.setPrice(SECOND_PRODUCT_PRICE);

        return product;
    }

    public static OrderTable createFirstOrderTable() {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(FIRST_ORDER_TABLE_ID);
        orderTable.setName(FIRST_ORDER_TABLE_NAME);
        orderTable.setNumberOfGuests(FIRST_ORDER_TABLE_GUEST);

        return orderTable;
    }

    public static OrderTable createSecondOrderTable() {
        OrderTable orderTable = new OrderTable();

        orderTable.setId(SECOND_ORDER_TABLE_ID);
        orderTable.setName(SECOND_ORDER_TABLE_NAME);
        orderTable.setNumberOfGuests(SECOND_ORDER_TABLE_GUEST);

        return orderTable;
    }

    public static Menu createFirstMenu() {
        Menu menu = new Menu();

        menu.setId(FIRST_MENU_ID);
        menu.setName(FIRST_MENU_NAME);
        menu.setPrice(FIRST_MENU_PRICE);
        MenuGroup menuGroup = createFirstMenuGroup();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(createFirstMenuProducts());
        menu.setDisplayed(true);

        return menu;
    }

    public static Menu createSecondMenu() {
        Menu menu = new Menu();

        menu.setId(SECOND_MENU_ID);
        menu.setName(SECOND_MENU_NAME);
        menu.setPrice(SECOND_MENU_PRICE);
        MenuGroup menuGroup = createSecondMenuGroup();
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(createSecondMenuProducts());
        menu.setDisplayed(true);

        return menu;
    }

    public static Order createFirstOrder(final OrderType orderType) {
        Order order = new Order();

        order.setId(FIRST_ORDER_ID);

        OrderTable orderTable = createFirstOrderTable();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(createFirstOrderLineItems());
        order.setType(orderType);
        order.setDeliveryAddress(ADDRESS);
        order.setStatus(OrderStatus.WAITING);

        return order;
    }

    public static List<OrderLineItem> createFirstOrderLineItems() {
        OrderLineItem orderLineItem = new OrderLineItem();

        final Menu menu = createFirstMenu();
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(FIRST_ORDER_PRICE);
        orderLineItem.setQuantity(FIRST_ORDER_QUANTITY);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setSeq(FIRST_ORDER_LINE_ITEM_ID);

        return List.of(orderLineItem);
    }

    public static Order createSecondOrder(final OrderType orderType) {
        Order order = new Order();

        order.setId(SECOND_ORDER_ID);

        OrderTable orderTable = createSecondOrderTable();
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTable.getId());
        order.setOrderLineItems(createSecondOrderLineItems());
        order.setType(orderType);
        order.setDeliveryAddress(ADDRESS);
        order.setStatus(OrderStatus.WAITING);

        return order;
    }

    public static List<OrderLineItem> createSecondOrderLineItems() {
        OrderLineItem orderLineItem = new OrderLineItem();

        final Menu menu = createSecondMenu();
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(SECOND_ORDER_PRICE);
        orderLineItem.setQuantity(SECOND_ORDER_QUANTITY);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setSeq(SECOND_ORDER_LINE_ITEM_ID);

        return List.of(orderLineItem);
    }

    public static List<Order> createAllTypeOrders() {
        return Arrays.stream(OrderType.values())
                .map(TestFixture::createFirstOrder)
                .collect(Collectors.toList());
    }
}
