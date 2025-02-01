package kitchenpos.application.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.Arrays;
import java.util.List;

public class MenuProductFixture {

    public static final List<MenuProduct> DEFAULT_MENU_PRODUCTS =
            ProductFixture.DEFAULT_PRODUCT_LIST.stream()
                    .map(v -> setMenuProduct(v, 1, 1)
                    ).toList();

    public static MenuProduct setMenuProduct(Product products, long seq , long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(products);
        menuProduct.setProductId(products.getId());
        menuProduct.setSeq(seq);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

}
