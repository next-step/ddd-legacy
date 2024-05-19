package kitchenpos.fixture.application;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product createProduct(String name) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(10_000));
        product.setName(name);
        return product;
    }

    public static Product createProduct(BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName("강정치킨");
        return product;
    }

    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    public static Product changeProduct(BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);
        return product;
    }
}
