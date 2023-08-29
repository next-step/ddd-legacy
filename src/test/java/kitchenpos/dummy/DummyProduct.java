package kitchenpos.dummy;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class DummyProduct {
    public static Product createProduct(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product defaultProduct() {
        return createProduct("테스트 상품", new BigDecimal(10000));
    }
}
