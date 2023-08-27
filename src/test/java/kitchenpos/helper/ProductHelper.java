package kitchenpos.helper;


import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public final class ProductHelper {

    private static final String DEFAULT_NAME = "테스트 기본 상품명";

    private ProductHelper() {
    }

    public static Product create(BigDecimal price) {
        return create(DEFAULT_NAME, price);
    }

    public static Product create(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

}
