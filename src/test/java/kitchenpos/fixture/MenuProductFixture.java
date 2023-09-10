package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    public static MenuProduct create(Product product) {
        return create(1L, product, 1);
    }

    public static MenuProduct create(Product product, int quantity) {
        return create(1L, product, quantity);
    }

    private static MenuProduct create(final Long seq, final Product product, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();

        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());

        return menuProduct;
    }

}
