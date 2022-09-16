package kitchenpos.factory;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFactory {

    public static final int DEFAULT_QUANTITY = 10;

    public static MenuProduct of(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(DEFAULT_QUANTITY);
        return menuProduct;
    }
}
