package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    private static final Long DEFAULT_SEQ = 1L;
    private static final Long DEFAULT_QUANTITY = 1L;

    public static MenuProduct createDefault() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(DEFAULT_SEQ);
        menuProduct.setProduct(ProductFixture.createDefault());
        menuProduct.setQuantity(DEFAULT_QUANTITY);
        return menuProduct;
    }

    public static MenuProduct createRequest(final Long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(ProductFixture.createDefault().getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct of(final Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(DEFAULT_SEQ);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(DEFAULT_QUANTITY);
        return menuProduct;
    }

    public static MenuProduct create(final Product product, final Long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(DEFAULT_SEQ);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
