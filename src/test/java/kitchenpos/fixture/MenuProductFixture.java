package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import static kitchenpos.fixture.ProductFixture.PRODUCT;

public class MenuProductFixture {

    public static MenuProduct MENU_PRODUCT() {
        MenuProduct menuProduct = new MenuProduct();
        Product product = PRODUCT();

        menuProduct.setSeq(1L);
        menuProduct.setProductId(product.getId());
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);

        return menuProduct;
    }
}
