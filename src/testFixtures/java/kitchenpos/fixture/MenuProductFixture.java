package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    private MenuProductFixture() {}

    public static MenuProduct generateMenuProduct(final Product product, final long quantity) {
        return createMenuProduct(product, quantity);
    }

    private static MenuProduct createMenuProduct(final Product product, final long quantity) {
        MenuProduct menuProduct = new MenuProduct();

        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }
}
