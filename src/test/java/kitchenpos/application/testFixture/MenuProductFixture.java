package kitchenpos.application.testFixture;

import kitchenpos.domain.MenuProduct;

public record MenuProductFixture() {
    public static MenuProduct newOne() {
        var menuProduct = new MenuProduct();
        menuProduct.setProduct(ProductFixture.newOne());
        menuProduct.setQuantity(1);
        return menuProduct;
    }
}
