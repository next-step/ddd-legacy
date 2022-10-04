package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
