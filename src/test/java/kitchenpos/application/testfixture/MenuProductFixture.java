package kitchenpos.application.testfixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public record MenuProductFixture() {
    public static MenuProduct newOne() {
        var menuProduct = new MenuProduct();
        menuProduct.setProduct(ProductFixture.newOne());
        menuProduct.setQuantity(1);
        return menuProduct;
    }

    public static MenuProduct newOne(int quantity) {
        var menuProduct = new MenuProduct();
        menuProduct.setProduct(ProductFixture.newOne());
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    public static MenuProduct newOne(Product product) {
        var menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(1);
        return menuProduct;
    }
}
