package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MenuFixture {
    private MenuFixture() {

    }
    
    public static Menu createMenu(final MenuGroup menuGroup, final String name, final BigDecimal price,
                                  final boolean displayed, final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setMenuGroup(menuGroup);
        if (Objects.nonNull(menuGroup)) {
            menu.setMenuGroupId(menuGroup.getId());
        }
        menu.setMenuGroupId(menuGroup.getId());
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createMenuWithId(final MenuGroup menuGroup, final String name, final BigDecimal price,
                                        final boolean displayed, final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setMenuGroupId(menuGroup.getId());
        menu.setMenuGroup(menuGroup);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
