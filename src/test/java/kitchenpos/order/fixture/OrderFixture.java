package kitchenpos.order.fixture;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;

public class OrderFixture {

    public static final String 서울_주소 = "서울시 송파구 위례성대로 2";


    public static OrderLineItem 주문_상품_생성_요청(final int price, final long quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setPrice(new BigDecimal(price));
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    public static Order 주문_생성_요청(final OrderType orderType, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderTableId(UUID.randomUUID());
        order.setOrderLineItems(orderLineItems);
        return order;
    }

    public static OrderTable 주문_1번_테이블(final boolean isEmptyTable) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("1번");
        orderTable.setEmpty(isEmptyTable);
        orderTable.setNumberOfGuests(0);
        return orderTable;
    }

    public static OrderTable 주문_테이블(final String name, final int numberOfGuests, final boolean isEmptyTable) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setEmpty(isEmptyTable);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    public static OrderLineItem 주문_상품(final int quantity, final int price, Menu menu) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(new BigDecimal(price));
        orderLineItem.setMenuId(UUID.randomUUID());
        orderLineItem.setMenu(menu);
        return orderLineItem;
    }

    public static Order 주문(final OrderType type, final String address, OrderStatus orderStatus, OrderTable orderTable, final List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setType(type);
        order.setOrderTableId(UUID.randomUUID());
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(address);
        order.setStatus(orderStatus);
        order.setOrderTable(orderTable);
        return order;
    }

    public static Order 배달_주문(final OrderStatus orderStatus, final List<OrderLineItem> orderLineItems) {
        return 주문(DELIVERY, 서울_주소, orderStatus, null, orderLineItems);
    }

    public static Order 매장_주문(final OrderStatus orderStatus, final List<OrderLineItem> orderLineItems) {
        return 주문(EAT_IN, null, orderStatus, null, orderLineItems);
    }

    public static Product 후라이드_상품(int price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드");
        product.setPrice(new BigDecimal(price));
        return product;
    }

    public static MenuProduct 메뉴_상품(final Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(1L);
        menuProduct.setProduct(product);
        return menuProduct;
    }

    public static Menu 후라이드_한마리_메뉴(final int price, final boolean isDisplayed) {
        MenuProduct menuProduct = 메뉴_상품(후라이드_상품(price));
        MenuGroup menuGroup = 한마리_메뉴_그룹();
        return 후라이드_메뉴(menuProduct, menuGroup, price, isDisplayed);
    }

    public static Menu 후라이드_메뉴(final MenuProduct menuProduct, final MenuGroup menuGroup, final int price, final boolean isDisplayed) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName("후라이드");
        menu.setPrice(new BigDecimal(price));
        menu.setDisplayed(isDisplayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(Arrays.asList(menuProduct));
        return menu;
    }

    public static MenuGroup 한마리_메뉴_그룹() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("한마리메뉴");
        return menuGroup;
    }
}
