package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixtures {

    private static final String name = "햄버거";
    private static final BigDecimal price = new BigDecimal("1000");


    public static Product createProduct() {
        return createProduct(name, price);
    }

    public static Product createProduct(BigDecimal price) {
        return createProduct(name, price);
    }

    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
