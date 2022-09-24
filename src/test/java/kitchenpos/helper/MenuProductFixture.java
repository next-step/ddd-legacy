package kitchenpos.helper;

import java.util.UUID;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    public static final MenuProduct ONE_FRIED_CHICKEN = create(ProductFixture.FRIED_CHICKEN, 1);

    public static final MenuProduct TWO_FRIED_CHICKEN = create(ProductFixture.FRIED_CHICKEN, 2);

    public static final MenuProduct ONE_HOT_SPICY_CHICKEN = create(ProductFixture.HOT_SPICY_CHICKEN, 1);

    private static MenuProduct create(Product product, int quantity) {
        var menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct request(UUID productId, int quantity) {
        var request = new MenuProduct();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return request;
    }
}
