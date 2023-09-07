package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    private MenuProductFixture() {
    }

    public static MenuProduct create() {
        return create(ProductFixture.create(), 1);
    }

    public static MenuProduct create(Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setSeq(1L);
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        menuProduct.setProductId(product.getId());

        return menuProduct;
    }
}
