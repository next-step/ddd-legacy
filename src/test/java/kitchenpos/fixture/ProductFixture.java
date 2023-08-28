package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product TEST_PRODUCT() {
        Product product = new Product();
        product.setPrice(new BigDecimal(5_000));
        product.setName("핏자");
        product.setId(UUID.randomUUID());
        return product;
    }
}
