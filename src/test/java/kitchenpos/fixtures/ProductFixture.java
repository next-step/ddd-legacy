package kitchenpos.fixtures;

import kitchenpos.domain.*;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product create(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product 후라이드치킨_16000원_상품() {
        return ProductFixture.create("후라이드 치킨", BigDecimal.valueOf(16000));
    }
}
