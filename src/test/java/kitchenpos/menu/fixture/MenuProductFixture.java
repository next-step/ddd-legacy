package kitchenpos.menu.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {
    public MenuProduct 메뉴_상품 = create(ProductFixture.상품, 10);
    public MenuProduct 메뉴_상품_A = create(ProductFixture.상품_A,10);
    public MenuProduct 메뉴_상품_B = create(ProductFixture.상품_B, 10);
    public MenuProduct 메뉴_상품_C = create(ProductFixture.상품_C, 10);

    public static MenuProduct create(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }
}
