package kitchenpos.application.stub;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductStub {

    private static final Long DEFAULT_SEQ = 1L;
    private static final Long DEFAULT_QUANTITY = 1L;

    public static MenuProduct createDefault() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(DEFAULT_SEQ);
        menuProduct.setProduct(ProductStub.createDefault());
        menuProduct.setQuantity(DEFAULT_QUANTITY);
        return menuProduct;
    }

    public static MenuProduct of(final Product product) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(DEFAULT_SEQ);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(DEFAULT_QUANTITY);
        return menuProduct;
    }
}
