package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(10000);
    private static final String DEFAULT_NAME = "상품";
    public static Product create(String name){
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(DEFAULT_PRICE);
        product.setName(name);
        return product;
    }

    public static Product create(BigDecimal price){
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(DEFAULT_NAME);
        return product;
    }
}
