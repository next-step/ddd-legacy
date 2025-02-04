package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static final String DEFAULT_PRODUCT_NAME = "옛날통닭";

    public static final BigDecimal DEFAULT_PRODUCT_PRICE = new BigDecimal(10_000);

    private ProductFixture() {
    }

    public static Product product(final UUID id, final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product product(final String name, final int price) {
        return product(getUuid(), name, BigDecimal.valueOf(price));
    }

    public static Product product() {
        return product(getUuid(), DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
    }

    private static UUID getUuid() {
        return UUID.randomUUID();
    }

}
