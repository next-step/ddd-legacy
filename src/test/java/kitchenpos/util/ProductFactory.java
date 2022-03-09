package kitchenpos.util;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFactory {
    private ProductFactory() {
    }

    public static Product createProduct(UUID uuid, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
