package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    public static Menu create(MenuGroup menuGroup, Product product) {
        return create(UUID.randomUUID(), "치킨", new BigDecimal(1000), menuGroup, true, List.of(MenuProductFixture.create(product)));
    }

    public static Menu create(MenuGroup menuGroup, Product product, boolean displayed) {
        return create(UUID.randomUUID(), "치킨", new BigDecimal(1000), menuGroup, displayed, List.of(MenuProductFixture.create(product)));
    }

    public static Menu create(MenuGroup menuGroup, Product product, int price) {
        return create(UUID.randomUUID(), "치킨", new BigDecimal(price), menuGroup, true, List.of(MenuProductFixture.create(product)));
    }

    public static Menu create(String name, MenuGroup menuGroup, MenuProduct menuProduct, int price) {
        return create(UUID.randomUUID(), name, new BigDecimal(price), menuGroup, true, List.of(menuProduct));
    }

    private static Menu create(final UUID id, final String name,
                               final BigDecimal price, final MenuGroup menuGroup,
                               final boolean displayed, final List<MenuProduct> menuProducts) {

        final Menu menu = new Menu();

        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        menu.setMenuGroupId(menuGroup.getId());

        return menu;
    }

}
