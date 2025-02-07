package kitchenpos.mock.fixture;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

public class MenuFixture {

    public static Menu create(final String name, final BigDecimal price, boolean displayed,
        MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menu.getMenuGroup().getId());
        return menu;
    }

    public static Menu create(final BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        return menu;
    }

}
