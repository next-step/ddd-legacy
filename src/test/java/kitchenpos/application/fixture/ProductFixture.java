package kitchenpos.application.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product setProduct(String name, BigDecimal price) {
        Product product = new Product();

        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);

        return product;
    }



}
