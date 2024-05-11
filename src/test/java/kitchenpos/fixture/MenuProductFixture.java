package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;

import java.util.UUID;

public class MenuProductFixture {
    private MenuProductFixture() {

    }

    public static MenuProduct createMenuProduct(UUID productId, int quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
