package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductFixture {

    public static Product createProduct(String name, Integer price) {
        Product product = new Product();
        product.setName(name);
        if (price == null) {
            product.setPrice(null);
        } else {
            product.setPrice(new BigDecimal(price));
        }
        return product;
    }

    public static Product createProduct() {
        return createProduct("후라이드", 500);
    }

    public static Product createProductWithName(String name) {
        return createProduct(name, 500);
    }

    public static Product createProductWithPrice(Integer price) {
        return createProduct("후라이드", price);
    }
}
