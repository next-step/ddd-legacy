package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.jetbrains.annotations.NotNull;

public class MenuProductFixture {
    public static @NotNull MenuProduct createMenuProduct(Product product) {
        return createMenuProduct(product, 1L);
    }

    public static @NotNull MenuProduct createMenuProduct(Product product, long Quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(Quantity);
        return menuProduct;
    }
}
