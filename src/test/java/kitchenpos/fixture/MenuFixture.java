package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static kitchenpos.MoneyConstants.오천원;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProduct;

public class MenuFixture {

    final private static String 메뉴명 = "메뉴명";

    public static Menu createMenu(Product product) {
        return createMenu(메뉴명, 오천원, product);
    }

    public static Menu createMenu(final long price) {
        return createMenu(메뉴명, price, createProduct());
    }

    public static Menu createMenu(Product product, MenuGroup menuGroup) {
        return createMenu(메뉴명, 오천원, product, menuGroup);
    }

    public static Menu createMenu(final long price, final Product product) {
        return createMenu(메뉴명, price, product);
    }

    public static Menu createMenuWithoutName(String name, Product product, MenuGroup menuGroup) {
        return createMenu(name, 오천원, product, menuGroup);
    }

    public static Menu createMenu(final long price, final Product product, final MenuGroup menuGroup) {
        return createMenu(메뉴명, price, product, menuGroup);
    }

    public static Menu createMenu(final String name, final long price, final Product product) {
        return createMenu(name, price, product, createMenuGroup());
    }

    public static @NotNull Menu createMenu(final String name, final long price, final Product product, final MenuGroup menuGroup) {
        return createMenu(name, price, menuGroup, product);
    }

    public static @NotNull Menu createMenu(final String name, final long price, final MenuGroup menuGroup, final Product... product) {
        Menu menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(true);
        menu.setMenuGroup(menuGroup);

        final List<MenuProduct> menuProducts = new ArrayList<>();
        for (Product prod : product) {
            menuProducts.add(createMenuProduct(prod));
        }
        menu.setMenuProducts(menuProducts);
        return menu;
    }
}
