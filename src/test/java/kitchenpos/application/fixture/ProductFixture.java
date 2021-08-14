package kitchenpos.application.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    private static final UUID UUID_ONE = UUID.randomUUID();
    private static final String PRODUCT_ONE_NAME = "순살치킨";
    private static final long PRICE_ONE = 16000L;

    public static Product PRODUCT_ONE_REQUEST() {
        return createProduct(UUID_ONE, PRODUCT_ONE_NAME, PRICE_ONE);
    }

    private static Product createProduct(final UUID id, final String name, final long price) {
        final Product product = new Product();
        product.setId(id);
        product.setPrice(BigDecimal.valueOf(price));
        product.setName(name);
        return product;
    }
}
