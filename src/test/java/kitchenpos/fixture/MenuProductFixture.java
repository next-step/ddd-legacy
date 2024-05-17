package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.Objects;
import java.util.UUID;

public class MenuProductFixture {
    private MenuProductFixture() {

    }

    public static MenuProduct createMenuProduct(final UUID productId, final int quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct createMenuProduct(final Product product, final int quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        if (Objects.nonNull(product)) {
            menuProduct.setProductId(product.getId());
        }
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
