package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixtures {
    private static final String name = "등심돈까스";
    private static final int price = 15000;


    public static Product 상품_등록() {
        return 상품_등록(name, price);
    }

    public static Product 상품_등록(int price) {
        return 상품_등록(name, price);
    }

    public static Product 상품_등록(String name, int price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static MenuProduct 메뉴_상품_등록(final Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setQuantity(quantity);
        menuProduct.setProduct(product);
        return menuProduct;
    }
}
