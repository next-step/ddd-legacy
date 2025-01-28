package kitchenpos.builder;

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

public class TestFactory {

    public static MenuGroup createMenuGroup() {
        return new MenuGroup("한식");
    }

    public static Product createProduct(BigDecimal price) {
        return new Product("김치", price);
    }

    public static Menu createMenu() {
        MenuGroup menuGroup = createMenuGroup();
        Product product = createProduct(BigDecimal.valueOf(5000));
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());

        return new Menu("김치찌개", BigDecimal.valueOf(8000), true, List.of(menuProduct), menuGroup,
                menuGroup.getId());
    }

    public static Menu createPureMenu(MenuGroup menuGroup, Product product) {
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        return new Menu("김치찌개", BigDecimal.valueOf(8000), true, List.of(menuProduct), menuGroup,
                menuGroup.getId());
    }

    public static Menu createMenu(String name, long price, Product product) {
        MenuProduct menuProduct = new MenuProduct(1, product, product.getId());
        MenuGroup menuGroup = new MenuGroup("찌개");
        return new Menu(name, BigDecimal.valueOf(price), true, List.of(menuProduct), menuGroup, menuGroup.getId());
    }

    public static OrderTable createOrderTable(String name, int numberOfGuests, boolean occupied) {
        return new OrderTable(name, numberOfGuests, occupied);
    }

    public static Product createProduct(String name, long price) {
        return new Product(name, BigDecimal.valueOf(price));
    }

    public static Order createDefaultOrder(OrderLineItem orderLineItem, OrderTable orderTable) {
        return new Order(OrderType.DELIVERY, OrderStatus.WAITING, LocalDateTime.now(), List.of(orderLineItem),
                "주소", orderTable, orderTable.getId());
    }
}
