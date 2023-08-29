package kitchenpos.dummy;

import kitchenpos.domain.MenuProduct;

public class DummyMenuProduct {

    public static MenuProduct createMenuProduct() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setProduct(DummyProduct.defaultProduct());
        menuProduct.setQuantity(1L);
        menuProduct.setProductId(DummyProduct.defaultProduct().getId());
        return menuProduct;
    }

    public static MenuProduct defaultMenuProduct() {
        return createMenuProduct();
    }
}
