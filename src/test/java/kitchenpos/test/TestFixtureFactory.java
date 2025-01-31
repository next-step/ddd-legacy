package kitchenpos.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;

public class TestFixtureFactory {

    public static MenuGroup createMenuGroup() {
        return new MenuGroup("한식");
    }

    public static Product createProduct(BigDecimal price) {
        return new Product("김치", price);
    }

    public static Menu createMenuWithProductAndGroup() {
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(5000));
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        return new Menu("김치찌개", BigDecimal.valueOf(8000), true, List.of(menuProduct), menuGroup,
                menuGroup.getId());
    }

    public static Menu createMenuWithProductAndGroup(boolean displayed) {
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(5000));
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        return new Menu("김치찌개", BigDecimal.valueOf(8000), displayed, List.of(menuProduct), menuGroup,
                menuGroup.getId());
    }

    public static Menu createMenu(MenuGroup menuGroup, Product product) {
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        return new Menu("김치찌개", BigDecimal.valueOf(8000), true, List.of(menuProduct), menuGroup,
                menuGroup.getId());
    }

    public static Menu createMenuWithProductAndGroup(String name, long price, Product product) {
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        MenuGroup menuGroup = new MenuGroup("찌개");
        return new Menu(name, BigDecimal.valueOf(price), true, List.of(menuProduct), menuGroup, menuGroup.getId());
    }

    public static OrderTable createEmptyOrderTable() {
        return new OrderTable("비어 있는 테이블", 0, false);
    }

    public static OrderTable createUsingOrderTable() {
        return new OrderTable("사용 중인 테이블", 4, true);
    }

    public static Product createProduct(String name, long price) {
        return new Product(name, BigDecimal.valueOf(price));
    }

    public static Order createOrderWithDeliveryType(OrderLineItem orderLineItem, OrderTable orderTable, OrderStatus status) {
        return new Order(OrderType.DELIVERY, status, LocalDateTime.now(), List.of(orderLineItem),
                "주소", orderTable, orderTable.getId());
    }

    public static Order createOrderWithTakeOutType(OrderLineItem orderLineItem, OrderTable orderTable,OrderStatus status) {
        return new Order(OrderType.TAKEOUT, status, LocalDateTime.now(), List.of(orderLineItem),
                "주소", orderTable, orderTable.getId());
    }

    public static Order createOrderWithEatInType(OrderLineItem orderLineItem, OrderTable orderTable, OrderStatus status) {
        return new Order(OrderType.EAT_IN, status, LocalDateTime.now(), List.of(orderLineItem),
                "주소", orderTable, orderTable.getId());
    }

    public static Order createOrder(OrderLineItem orderLineItem, OrderTable orderTable, OrderType orderType,
                                    OrderStatus orderStatus, String address) {
        return new Order(orderType, orderStatus, LocalDateTime.now(), List.of(orderLineItem),
                address, orderTable, orderTable.getId());
    }

    public static OrderLineItem createOrderLineItem(Menu menu) {
        return new OrderLineItem(menu, 2, menu.getId(), BigDecimal.valueOf(8000));
    }
}
