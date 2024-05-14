package kitchenpos.fixtures;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product ofFixture(String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(name);
        return product;
    }
}
