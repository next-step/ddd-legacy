package kitchenpos.fixtures;

import kitchenpos.application.ProductService;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    private final Product product;

    public ProductFixture(String name, BigDecimal price) {
        this.product = this.ofFixture(name, price);
    }

    public Product ofFixture(String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    public Product getProduct() {
        return product;
    }
}
