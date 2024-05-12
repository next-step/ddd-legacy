package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

public class MenuProductFixture {
    private MenuProductFixture() {

    }

    public static MenuProduct createMenuProduct(UUID productId, int quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct createMenuProduct(Product product, int quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
