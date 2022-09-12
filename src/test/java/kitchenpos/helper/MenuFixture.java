package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuFixture {

    public static final Menu ONE_FRIED_CHICKEN = create(
        "후라이드 치킨 한마리",
        6000,
        true,
        List.of(MenuProductFixture.ONE_FRIED_CHICKEN)
    );

    public static final Menu TWO_FRIED_CHICKEN = create(
        "후라이드 치킨 두마리",
        9000,
        true,
        List.of(MenuProductFixture.TWO_FRIED_CHICKEN)
    );

    private static Menu create(
        String name,
        int price,
        boolean displayed,
        List<MenuProduct> menuProducts
    ) {
        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);
        return menu;
    }

    private static class MenuProductFixture {

        public static final MenuProduct ONE_FRIED_CHICKEN = create(ProductFixture.FRIED_CHICKEN, 1);

        public static final MenuProduct TWO_FRIED_CHICKEN = create(ProductFixture.FRIED_CHICKEN, 2);

        private static MenuProduct create(Product product, int quantity) {
            var menuProduct = new MenuProduct();
            menuProduct.setProduct(product);
            menuProduct.setQuantity(quantity);
            return menuProduct;
        }
    }
}
