package kitchenpos.fixture.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu createMenu(String menuName, MenuGroup menuGroup, List<MenuProduct> menuProducts, BigDecimal menuPrice) {
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(menuName);
        menu.setPrice(menuPrice);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);

        return menu;
    }
}
