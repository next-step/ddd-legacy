package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    private static String PRODUCT_NAME = "상품이름1";

    public static Product PRODUCT() {
        Product product = new Product();

        product.setId(UUID.randomUUID());
        product.setName(PRODUCT_NAME);
        product.setPrice(new BigDecimal(10_000));

        return product;
    }
}
