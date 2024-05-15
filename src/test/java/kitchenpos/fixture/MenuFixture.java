package kitchenpos.fixture;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.MoneyConstants.오천원;

public class MenuFixture {

    final private static String 메뉴명 = "메뉴명";

    public static Menu createMenu(Product product) {
        return createMenu(메뉴명, 오천원, product);
    }

    public static Menu createMenu(final long price, final Product product) {
        return createMenu(메뉴명, price, product);
    }

    public static @NotNull Menu createMenu(final String name, final long price, final Product product) {
        Menu menu = new Menu();
        menu.setMenuGroupId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(true);
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menu.setMenuProducts(List.of(menuProduct));
        return menu;
    }
}
