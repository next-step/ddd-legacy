package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuProductFixture.generateMenuProduct;

public class MenuFixture {

    private MenuFixture() {}

    private static final String DEFAULT_NAME = "menu";
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;

    public static Menu generateMenu(final Product product, final int quantity, final MenuGroup menuGroup) {
        return createMenu(
                UUID.randomUUID(),
                DEFAULT_NAME,
                DEFAULT_PRICE,
                menuGroup,
                true,
                List.of(generateMenuProduct(product, quantity))
        );
    }

    public static Menu generateMenu(
            final Product product,
            final int quantity,
            final MenuGroup menuGroup,
            final BigDecimal price
    ) {
        return createMenu(
                UUID.randomUUID(),
                DEFAULT_NAME,
                price,
                menuGroup,
                price.intValue() < (product.getPrice().intValue() * quantity),
                List.of(generateMenuProduct(product, quantity))
        );
    }

    public static Menu generateMenu(
            final Product product,
            final int quantity,
            final MenuGroup menuGroup,
            final BigDecimal price,
            final boolean displayed
    ) {
        return createMenu(
                UUID.randomUUID(),
                DEFAULT_NAME,
                price,
                menuGroup,
                displayed,
                List.of(generateMenuProduct(product, quantity))
        );
    }

    private static Menu createMenu(
            final UUID id,
            final String name,
            final BigDecimal price,
            final MenuGroup menuGroup,
            final boolean displayed,
            final List<MenuProduct> menuProducts
    ) {
        Menu menu = new Menu();

        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroup.getId());
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);

        return menu;
    }
}
