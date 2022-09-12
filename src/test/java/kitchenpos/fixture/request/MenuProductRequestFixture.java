package kitchenpos.fixture.request;

import kitchenpos.domain.MenuProduct;

import java.util.UUID;

public class MenuProductRequestFixture {
    public static MenuProduct createMenuProductRequest(final UUID productId, final long quantity) {
        final MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }
}
