package kitchenpos.menu;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.menugroup.MenuGroupFixture;
import kitchenpos.product.ProductFixture;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MenuFixture {
    public static Menu menu(String name, int price) {
        List<MenuProduct> 메뉴상품_목록 = convert(1, ProductFixture.product());
        return new Menu(UUID.randomUUID(), name, BigDecimal.valueOf(price), true, 메뉴상품_목록, MenuGroupFixture.menuGroup().getId());
    }

    public static Menu positiveCountMenu(String name, int price, Product... products) {
        List<MenuProduct> menuProducts = convert(1, products);
        return new Menu(name, BigDecimal.valueOf(price), true, menuProducts, null);
    }

    public static Menu negativeCountMenu(String name, int price, Product... products) {
        List<MenuProduct> menuProducts = convert(-1, products);
        return new Menu(name, BigDecimal.valueOf(price), true, menuProducts, null);
    }

    public static Menu hideMenu(String name, int price, Product... products) {
        List<MenuProduct> menuProducts = convert(1, products);
        return new Menu(name, BigDecimal.valueOf(price), false, menuProducts, null);
    }

    private static List<MenuProduct> convert(int count, Product... products) {
        return Arrays.stream(products)
                .map(product -> new MenuProduct(product, count, product.getId()))
                .collect(Collectors.toList());
    }

    public static Menu changeMenuRequest(int price) {
        return new Menu(BigDecimal.valueOf(price));
    }

    public static List<Product> extractProducts(Menu menu) {
        return menu.getMenuProducts().stream()
                .map(MenuProduct::getProduct)
                .collect(Collectors.toList());
    }
}
