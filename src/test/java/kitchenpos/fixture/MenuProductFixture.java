package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

import static kitchenpos.TestConstant.후라이드치킨_PRODUCT_UUID;
import static kitchenpos.fixture.productFixture.createProduct;

public class MenuProductFixture {
    public static MenuProduct createMenuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct createMenuProduct(final Product product, final int quantity, final UUID productId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }

    public static MenuProduct createMenuProduct() {
        return createMenuProduct(createProduct(), 1, 후라이드치킨_PRODUCT_UUID);
    }
}
