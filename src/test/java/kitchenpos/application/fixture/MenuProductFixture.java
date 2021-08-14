package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.ProductFixture.CHEAP_PRODUCT;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT1;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT2;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    private static final long TWO = 2L;
    private static final long THREE = 3L;
    private static final long NEGATIVE_QUANTITY = -3L;

    public static MenuProduct MENU_PRODUCT1() {
        return createMenuProduct(PRODUCT1(), PRODUCT1().getId(), TWO);
    }

    public static MenuProduct MENU_PRODUCT2() {
        return createMenuProduct(PRODUCT2(), PRODUCT2().getId(), THREE);
    }

    public static MenuProduct WRONG_PRODUCT() {
        return createMenuProduct(PRODUCT2(), UUID.randomUUID(), THREE);
    }

    private static MenuProduct NEGATIVE_QUANTITY_MENU_PRODUCT() {
        return createMenuProduct(PRODUCT2(), PRODUCT2().getId(), NEGATIVE_QUANTITY);
    }

    public static MenuProduct CHEAP_MENU_PRODUCT() {
        final Product cheapProduct = CHEAP_PRODUCT();
        return createMenuProduct(cheapProduct, cheapProduct.getId(), TWO);
    }

    public static List<MenuProduct> MENU_PRODUCTS() {
        return Arrays.asList(MENU_PRODUCT1(), MENU_PRODUCT2());
    }

    public static List<MenuProduct> WRONG_PRODUCTS() {
        return Arrays.asList(MENU_PRODUCT1(), WRONG_PRODUCT());
    }

    public static List<MenuProduct> CHEAP_MENU_PRODUCTS() {
        return Arrays.asList(CHEAP_MENU_PRODUCT(), CHEAP_MENU_PRODUCT());
    }

    public static List<MenuProduct> QUANTITY_NAGATIVE_MENU_PRODUCTS() {
        return Arrays.asList(MENU_PRODUCT1(), NEGATIVE_QUANTITY_MENU_PRODUCT());
    }

    private static MenuProduct createMenuProduct(final Product product, final UUID productId, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

}
