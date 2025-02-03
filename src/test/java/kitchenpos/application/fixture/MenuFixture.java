package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(UUID menuGroupId, String name, BigDecimal price, List<MenuProduct> products) {
        return createMenu(null, null, menuGroupId, name, price, false, products);
    }

    public static Menu createMenu(UUID id, MenuGroup menuGroup, UUID menuGroupId, String name, BigDecimal price, boolean displayed, List<MenuProduct> products) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(products);
        return menu;
    }

    private MenuFixture() {
    }
}
