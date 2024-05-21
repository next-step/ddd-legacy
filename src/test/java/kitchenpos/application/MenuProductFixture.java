package kitchenpos.application;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

public class MenuProductFixture {

    public static MenuProduct createMenuProductRequest(final Product productRequest) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productRequest.getId());
        menuProduct.setQuantity(1L);
        menuProduct.setProduct(productRequest);
        menuProduct.setSeq(1L);
        return menuProduct;
    }


}
