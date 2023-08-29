package kitchenpos.objectmother;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductMaker {

    public static MenuProduct make(Product product, long quantity) {
        return new MenuProduct(product, quantity, product.getId());
    }

}
