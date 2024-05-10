package kitchenpos.menu.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {
    private kitchenpos.menu.fixture.productFixture productFixture = new productFixture();

    public MenuProduct 메뉴_상품 = create(productFixture.상품, 10);
    public MenuProduct 메뉴_상품_A = create(productFixture.상품_A,10);
    public MenuProduct 메뉴_상품_B = create(productFixture.상품_B, 10);
    public MenuProduct 메뉴_상품_C = create(productFixture.상품_C, 10);

    public static MenuProduct create(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }
}
