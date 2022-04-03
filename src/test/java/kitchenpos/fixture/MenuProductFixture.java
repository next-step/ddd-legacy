package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {
    public static MenuProduct create(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        return menuProduct;
    }
}
