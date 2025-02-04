package kitchenpos.application.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public record MenuProductFixture(Product 상품, long 수량) {

    public static MenuProductFixture init() {
        return new MenuProductFixture(ProductFixture.init().create(), 10);
    }

    public MenuProduct create() {
        var menuProduct = new MenuProduct();
        menuProduct.setProduct(상품);
        menuProduct.setQuantity(수량);

        return menuProduct;
    }
}

