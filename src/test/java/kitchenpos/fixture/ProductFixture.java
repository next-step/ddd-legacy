package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static final String DEFAULT_PRODUCT_NAME = "후라이드 치킨";
    public static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(16000);

    private ProductFixture() {
    }

    public static Product product() {
        return product(createProductId(), DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
    }

    public static Product product(UUID id, String name, int price) {
        return product(id, name, BigDecimal.valueOf(price));
    }

    public static Product product(UUID id, String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static UUID createProductId() {
        return UUID.randomUUID();
    }
}
