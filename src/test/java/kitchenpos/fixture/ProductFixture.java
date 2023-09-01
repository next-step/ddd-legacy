package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    private static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(10000);
    private static final String DEFAULT_PRODUCT_NAME = "기본상품";

    public static Product create(String name) {
        return create(name, DEFAULT_PRODUCT_PRICE);
    }

    public static Product create(BigDecimal price) {
        return create(DEFAULT_PRODUCT_NAME, price);
    }

    public static Product create(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    public static Product createDefault() {
        return create(DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
    }

}
