package kitchenpos.application.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product createProduct(String name, BigDecimal price) {
        return createProduct(null, name, price);
    }

    public static Product createProduct(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private ProductFixture() {
    }
}
