package kitchenpos.helper;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductTestHelper {
    public static MenuProduct 음식메뉴_생성(Product product, int quantity){
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(product.getId());

        return menuProduct;
    }
}
