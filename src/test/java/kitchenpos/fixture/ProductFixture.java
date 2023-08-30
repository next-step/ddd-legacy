package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product TEST_PRODUCT() {
        return TEST_PRODUCT(new BigDecimal(5_000), "핏자");
    }

    public static Product TEST_PRODUCT(BigDecimal price) {
        return TEST_PRODUCT(price, "핏자");
    }

    public static Product TEST_PRODUCT(String name) {
        return TEST_PRODUCT(new BigDecimal(5_000), name);
    }

    public static Product TEST_PRODUCT(BigDecimal price, String name) {
        Product product = new Product();
        product.setPrice(price);
        product.setName(name);
        product.setId(UUID.randomUUID());
        return product;
    }
}
