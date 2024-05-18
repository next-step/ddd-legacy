package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.jetbrains.annotations.NotNull;

public class MenuProductFixture {
    public static @NotNull MenuProduct createMenuProduct(Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1L);
        return menuProduct;
    }
}
