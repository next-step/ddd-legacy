package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuFixture {

    private static final String DEFAULT_NAME = "후라이드 치킨";
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(15_000);
    private static final Boolean DEFAULT_DISPLAYED = Boolean.TRUE;

    public static Menu createDefault() {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(DEFAULT_NAME);
        menu.setPrice(DEFAULT_PRICE);
        menu.setDisplayed(DEFAULT_DISPLAYED);
        menu.setMenuGroup(MenuGroupFixture.createDefault());
        menu.setMenuProducts(List.of(MenuProductFixture.createDefault()));
        return menu;
    }

    public static Menu create(final String name,
                              final BigDecimal price,
                              final boolean displayed,
                              final MenuGroup menuGroup,
                              final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    public static Menu createRequest(final String name,
                              final BigDecimal price,
                              final boolean displayed,
                              final UUID menuGroupId,
                              final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
