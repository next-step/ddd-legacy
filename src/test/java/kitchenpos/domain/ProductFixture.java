package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product 후라이드() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("후라이드");
        product.setPrice(BigDecimal.valueOf(16000));
        return product;
    }

    public static Product 양념치킨() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(17000));
        return product;
    }
}
