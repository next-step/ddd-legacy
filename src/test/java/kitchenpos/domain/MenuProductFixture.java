package kitchenpos.domain;

import java.util.UUID;

public class MenuProductFixture {

    public static MenuProduct MenuProduct(UUID productId, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
