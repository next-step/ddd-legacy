package kitchenpos.helper;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    private static final String DEFAULT_PRODUCT_NAME = "default product name";
    private static final int DEFAULT_PRODUCT_PRICE = 1000;

    public static Product create(String name, int price) {
        var product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product create(int price) {
        return create(DEFAULT_PRODUCT_NAME, price);
    }

    public static Product create() {
        return create(DEFAULT_PRODUCT_PRICE);
    }
}
