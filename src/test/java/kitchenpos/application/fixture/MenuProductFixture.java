package kitchenpos.application.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.application.fixture.ProductFixture.PRODUCT_ONE_REQUEST;

public class MenuProductFixture {
    private static final long ONE = 1L;

    public static MenuProduct MENU_PRODUCT_ONE() {
        return createMenuProduct(PRODUCT_ONE_REQUEST(), PRODUCT_ONE_REQUEST().getId(), ONE);
    }

    public static List<MenuProduct> MENU_PRODUCTS() {
        return Arrays.asList(MENU_PRODUCT_ONE());
    }

    private static MenuProduct createMenuProduct(final Product product, final UUID productId, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
