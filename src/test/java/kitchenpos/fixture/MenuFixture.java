package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {


    public static Menu createMenu(final String name, final BigDecimal price, final List<MenuProduct> menuProducts, final MenuGroup menuGroup) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(false);
        return menu;
    }

    public static Menu createMenuRequest(final BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        return menu;
    }

}
