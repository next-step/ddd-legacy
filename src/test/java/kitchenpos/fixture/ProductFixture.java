package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product createProduct(BigDecimal price) {
        final var product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("익명상품");
        product.setPrice(price);
        return product;
    }

    public static Product createProduct(String name, BigDecimal price) {
        final var product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
