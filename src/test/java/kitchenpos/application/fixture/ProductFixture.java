package kitchenpos.application.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product createProduct(final BigDecimal price, final String name) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product createProduct(final long price, final String name) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product createProductRequest(BigDecimal price, String name) {
        Product request = new Product();
        request.setPrice(price);
        request.setName(name);
        return request;
    }

}
