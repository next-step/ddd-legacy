package kitchenpos.application.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {
    public static Menu setMenuGroup(MenuGroup menuGroup, String name, BigDecimal price, List<MenuProduct> products) {
        Menu menu = new Menu();

        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName(name);
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(price);
        menu.setMenuProducts(products);

        return menu;
    }
}
