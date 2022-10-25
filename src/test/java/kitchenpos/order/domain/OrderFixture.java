package kitchenpos.order.domain;

import kitchenpos.common.vo.Name;
import kitchenpos.common.vo.Price;
import kitchenpos.common.vo.Quantity;
import kitchenpos.menu.menu.domain.Menu;
import kitchenpos.menu.menu.domain.MenuProduct;
import kitchenpos.menu.menugroup.domain.MenuGroup;
import kitchenpos.product.domain.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    public static List<OrderLineItem> orderLineItems() {
        MenuGroup menuGroup = createMenuGroup(UUID.randomUUID(), "메뉴 그룹명");
        Menu menu = new Menu(menuGroup, createMenuProducts(new MenuProduct(new Product(new Name("productName", false), new Price(BigDecimal.TEN)), new Quantity(1))), new Price(BigDecimal.TEN));
        menu.display();
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        OrderLineItem orderLineItem = new OrderLineItem(menu);
        orderLineItems.add(orderLineItem);
        return orderLineItems;
    }

    private static MenuGroup createMenuGroup(UUID id, String menuGroupName) {
        return new MenuGroup(id, new Name(menuGroupName, false));
    }

    private static List<MenuProduct> createMenuProducts(final MenuProduct... menuProducts) {
        return Arrays.asList(menuProducts);
    }
}


