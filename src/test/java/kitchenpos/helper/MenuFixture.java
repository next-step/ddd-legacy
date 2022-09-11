package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {

    private static final String DEFAULT_MENU_NAME = "default menu name";
    private static final int DEFAULT_MENU_QUANTITY = 1;

    public static Menu create(
        String name,
        int price,
        Product product,
        int quantity,
        boolean displayed
    ) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        menu.setMenuGroup(MenuGroupFixture.create());
        menu.setMenuProducts(List.of(MenuProductFixture.create(product, quantity)));
        return menu;
    }

    public static Menu create() {
        Product product = ProductFixture.create();
        int price = product.getPrice().intValue() * DEFAULT_MENU_QUANTITY;
        return create(
            DEFAULT_MENU_NAME,
            price,
            product,
            DEFAULT_MENU_QUANTITY,
            true
        );
    }

    private static class MenuProductFixture {

        public static MenuProduct create(Product product, int quantity) {
            var menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(quantity);
            return menuProduct;
        }
    }
}
