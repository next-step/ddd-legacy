package kitchenpos.fixtures;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

public class MenuProductFixture {

    public static long seq = 1L;

    public static MenuProduct create(Product product, long quantity, UUID productId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(seq++);
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }
}