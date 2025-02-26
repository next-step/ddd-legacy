package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class MenuProductFixture {

    public static MenuProduct createMenuProduct(final Product product, final long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct createMenuProduct() {
        final Product product = ProductFixture.createProduct("후라이드", BigDecimal.valueOf(16000));
        return createMenuProduct(product, 1);
    }

}
