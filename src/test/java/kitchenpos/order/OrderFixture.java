package kitchenpos.order;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.product.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static List<OrderLineItem> orderLineItems() {
        MenuGroup menuGroup = menuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = menu(menuGroup);
        menu.display();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem(menu, new Quantity(1));
        orderLineItems.add(orderLineItem);
        return orderLineItems;
    }

    private static Menu menu(MenuGroup menuGroup) {
        return new Menu(UUID.randomUUID(), new Name("메뉴명", false), menuGroup, menuProducts(new MenuProduct(product(), new Quantity(1))), new Price(BigDecimal.TEN));
    }

    private static Product product() {
        return new Product(UUID.randomUUID(), new Name("productName", false), new Price(BigDecimal.TEN));
    }

    private static MenuGroup menuGroup(UUID id, String name) {
        return new MenuGroup(id, new Name(name, false));
    }

    private static List<MenuProduct> menuProducts(final MenuProduct... menuProducts) {
        return Arrays.asList(menuProducts);
    }
}


