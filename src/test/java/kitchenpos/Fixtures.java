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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Fixtures {

    public static MenuGroup 메뉴그룹_생성(String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName(name);
        return menuGroup;
    }

    public static Menu createMenu_두마리_치킨(BigDecimal price,
                                         UUID menuGroupId,
                                         MenuGroup menuGroup,
                                         List<MenuProduct> products,
                                         String name,
                                         boolean displayed) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(products);
        menu.setName(name);
        menu.setDisplayed(displayed);
        return menu;
    }

    public static MenuProduct 메뉴_상품_생성(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setSeq(new Random().nextLong());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct 메뉴_상품_생성(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(new Random().nextLong());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }


    public static Product 상품_생성(String name, Long price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product 상품_생성(final long price) {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Order 주문_생성(OrderType type,
                              LocalDateTime orderTime,
                              List<OrderLineItem> orderLineItems,
                              String address,
                              OrderTable orderTable,
                              UUID tableId) {
        Order order = new Order();

        order.setId(UUID.randomUUID());
        order.setType(type);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(orderTime);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(address);
        order.setOrderTable(orderTable);
        order.setOrderTableId(tableId);

        return order;
    }

    public static OrderLineItem 메뉴_목록_생성(Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(BigDecimal.valueOf(32000));
        orderLineItem.setQuantity(2);
        orderLineItem.setSeq(1L);
        return orderLineItem;
    }

    public static OrderTable 주문테이블_착석() {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setNumberOfGuests(1);
        orderTable.setName("먹방 찍나봐");
        orderTable.setOccupied(true);
        return orderTable;
    }

    public static OrderTable 주문테이블_생성(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        return orderTable;
    }

    public static OrderTable 주문테이블_생성(String name, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(empty);
        return orderTable;
    }

    public static OrderTable 망가진_테이블() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("보수중 - 앉으면 망가져요");
        orderTable.setNumberOfGuests(1);
        return orderTable;
    }

    public static Menu 메뉴_생성_두마리_매콤_치킨_시험중() {
        final Product product = 상품_생성("매콤 후라이드", 0L);
        final MenuGroup menuGroup = 메뉴그룹_생성("매콤 치킨 세트");
        return createMenu_두마리_치킨(BigDecimal.valueOf(0), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 2)), "불나는 치킨 세트", false);
    }

    public static Menu 메뉴_생성_두마리_치킨() {
        final Product product = 상품_생성("후라이드", 16_000L);
        final MenuGroup menuGroup = 메뉴그룹_생성("치킨 세트");
        return createMenu_두마리_치킨(BigDecimal.valueOf(32_000L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 2)), "아름다운 치킨 세트", true);
    }

    public static Menu 메뉴_생성_두마리_치킨(long price) {
        final Product product = 상품_생성("후라이드", price);
        final MenuGroup menuGroup = 메뉴그룹_생성("치킨 세트");
        return createMenu_두마리_치킨(BigDecimal.valueOf(32_000L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 2)), "아름다운 치킨 세트", true);
    }

    public static Menu 메뉴_생성_두마리_반_치킨() {
        final Product product = 상품_생성("후라이드", 16_000L);
        final MenuGroup menuGroup = 메뉴그룹_생성("치킨 세트");
        return createMenu_두마리_치킨(BigDecimal.valueOf(31_500L), menuGroup.getId(), menuGroup, List.of(메뉴_상품_생성(product, 2)), "아름다운 치킨 세트", true);
    }

    public static MenuProduct 상품_메뉴(final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(new Random().nextLong());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static Menu 메뉴_생성(final long price, final boolean displayed, final MenuProduct... menuProducts) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드+후라이드");
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroup(메뉴그룹_생성("치킨 세트"));
        menu.setDisplayed(displayed);
        menu.setMenuProducts(Arrays.asList(menuProducts));
        return menu;
    }


    public static Order 주문_요청() {
        return 주문_생성(OrderType.EAT_IN, LocalDateTime.now(),
                List.of(메뉴_목록_생성(메뉴_생성_두마리_매콤_치킨_시험중())), null, null, null);
    }

    public static Order 주문_요청_매장(OrderTable orderTable) {
        return 주문_생성(OrderType.EAT_IN, LocalDateTime.now(),
                List.of(메뉴_목록_생성(메뉴_생성_두마리_치킨())), null, orderTable, orderTable.getId());
    }

    public static Order 주문_요청_매장() {
        return 주문_생성(OrderType.EAT_IN, LocalDateTime.now(),
                List.of(메뉴_목록_생성(메뉴_생성_두마리_치킨())), null, 주문테이블_착석(), 주문테이블_착석().getId());
    }

    public static Order 주문_요청_배달() {
        return 주문_생성(OrderType.DELIVERY, LocalDateTime.now(),
                List.of(메뉴_목록_생성(메뉴_생성_두마리_치킨())), "경기도 안양시 우리집", null, null);
    }

    public static Order 주문_요청_포장() {
        return 주문_생성(OrderType.TAKEOUT, LocalDateTime.now(),
                List.of(메뉴_목록_생성(메뉴_생성_두마리_치킨())), null, null, null);
    }
}
