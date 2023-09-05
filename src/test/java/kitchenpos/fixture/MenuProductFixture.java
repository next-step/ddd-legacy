package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.util.UUID;

public class MenuProductFixture {

    public static MenuProduct 간장치킨_메뉴_상품() {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(ProductFixture.간장치킨());
        menuProduct.setQuantity(4L);
        menuProduct.setProductId(UUID.randomUUID());
        return menuProduct;
    }

    public static MenuProduct 수량이_음수인_메뉴상품(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(-1);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

    public static MenuProduct 양념치킨_메뉴_상품(Product product) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(4L);
        menuProduct.setProductId(product.getId());
        return menuProduct;
    }

}
