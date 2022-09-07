package kitchenpos.product;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product product() {
        return new Product(UUID.randomUUID(), "상품", BigDecimal.valueOf(10000));
    }

    public static Product product(String name, int price) {
        return new Product(UUID.randomUUID(), name, BigDecimal.valueOf(price));
    }
}
