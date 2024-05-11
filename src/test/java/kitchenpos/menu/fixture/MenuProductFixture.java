package kitchenpos.menu.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

public class MenuProductFixture {
    private ProductFixture productFixture = new ProductFixture();

    public MenuProduct 메뉴_상품 = create(productFixture.상품, 10);
    public MenuProduct 메뉴_상품_A_수량_10 = create(productFixture.상품_A_가격_1000,10);
    public MenuProduct 메뉴_상품_B = create(productFixture.상품_B, 10);
    public MenuProduct 메뉴_상품_C = create(productFixture.상품_C_가격_10000, 10);

    public static MenuProduct create(Product product, long quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);

        return menuProduct;
    }
}