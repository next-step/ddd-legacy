package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

public class MenuProductFixture {
    public static MenuProduct createMenuProduct(
            final Long seq, final Product product, final long quantity
    ) {
        final MenuProduct menuProduct = new MenuProduct();

        menuProduct.setSeq(seq);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());

        return menuProduct;
    }
}
