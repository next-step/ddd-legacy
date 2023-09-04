package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {

    public static MenuProduct createMenuProduct(Product product, long quantity) {
        MenuProduct menuGroup = new MenuProduct();
        menuGroup.setProduct(product);
        menuGroup.setQuantity(quantity);
        return menuGroup;
    }

    public static MenuProduct createMenuProduct() {
        return createMenuProduct(ProductFixture.createProduct(), 1);
    }
}
