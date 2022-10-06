package factory;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFactory {

    public static final int DEFAULT_QUANTITY = 1;

    public static MenuProduct of(Product product) {
        return MenuProductFactory.of(product, DEFAULT_QUANTITY);
    }

    public static MenuProduct of(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
