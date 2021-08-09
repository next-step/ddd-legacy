package kitchenpos.application.fixture;

import static kitchenpos.application.fixture.ProductFixture.CHEAP_PRODUCT;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT1;
import static kitchenpos.application.fixture.ProductFixture.PRODUCT2;

import java.util.Arrays;
import java.util.List;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    private static final long TWO = 2L;
    private static final long THREE = 3L;
    private static final long NEGATIVE_QUANTITY = -3L;

    public static MenuProduct MENU_PRODUCT1() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(PRODUCT1());
        menuProduct.setProductId(PRODUCT1().getId());
        menuProduct.setQuantity(TWO);
        return menuProduct;
    }

    public static MenuProduct MENU_PRODUCT2() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(PRODUCT2());
        menuProduct.setProductId(PRODUCT2().getId());
        menuProduct.setQuantity(THREE);
        return menuProduct;
    }

    private static MenuProduct NEGATIVE_QUANTITY_MENU_PRODUCT() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(PRODUCT2());
        menuProduct.setProductId(PRODUCT2().getId());
        menuProduct.setQuantity(NEGATIVE_QUANTITY);
        return menuProduct;
    }

    public static MenuProduct CHEAP_MENU_PRODUCT() {
        final MenuProduct menuProduct = new MenuProduct();
        final Product cheapProduct = CHEAP_PRODUCT();
        menuProduct.setProduct(cheapProduct);
        menuProduct.setProductId(cheapProduct.getId());
        menuProduct.setQuantity(TWO);
        return menuProduct;
    }

    public static List<MenuProduct> MENU_PRODUCTS() {
        return Arrays.asList(MENU_PRODUCT1(), MENU_PRODUCT2());
    }

    public static List<MenuProduct> CHEAP_MENU_PRODUCTS() {
        return Arrays.asList(CHEAP_MENU_PRODUCT(), CHEAP_MENU_PRODUCT());
    }

    public static List<MenuProduct> QUANTITY_NAGATIVE_MENU_PRODUCTS() {
        return Arrays.asList(MENU_PRODUCT1(), NEGATIVE_QUANTITY_MENU_PRODUCT());
    }

}
