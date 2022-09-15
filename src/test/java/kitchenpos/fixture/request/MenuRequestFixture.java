package kitchenpos.fixture.request;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MenuRequestFixture {


    public static Menu changePriceMenuRequest() {
        return changePriceMenuRequest(13_000L);
    }

    public static Menu changePriceMenuRequest(Long price) {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(price));
        return menu;
    }

    public static Menu changePriceMenuRequest(BigDecimal price) {
        Menu menu = new Menu();
        menu.setPrice(price);
        return menu;
    }

    public static Menu createMenuRequest(
            final String name,
            final long price,
            final UUID menuGroupId,
            final boolean displayed,
            final MenuProduct... menuProducts
    ) {
        return createMenuRequest(name, BigDecimal.valueOf(price), menuGroupId, displayed, menuProducts);
    }

    public static Menu createMenuRequest(
            final String name,
            final BigDecimal price,
            final UUID menuGroupId,
            final boolean displayed,
            final MenuProduct... menuProducts
    ) {
        return createMenuRequest(name, price, menuGroupId, displayed, List.of(menuProducts));
    }

    public static Menu createMenuRequest(
            final String name,
            final BigDecimal price,
            final UUID menuGroupId,
            final boolean displayed,
            final List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
