package kitchenpos;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.Random;
import java.util.UUID;

import static kitchenpos.ProductFixture.product;

public class MenuProductFixture {

    public static MenuProduct menuProduct() {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(new Random().nextLong());
        menuProduct.setProduct(product());
        menuProduct.setQuantity(1L);
        return menuProduct;
    }

    public static MenuProduct menuProduct(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(new Random().nextLong());
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());

        return menuProduct;
    }

    public static MenuProduct menuProduct(UUID productId, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(new Random().nextLong());
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);

        return menuProduct;
    }
}
