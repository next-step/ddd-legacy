package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    private ProductFixture() {}

    private static final String DEFAULT_PRODUCT_NAME = "menu";
    private static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.ZERO;

    public static Product generateProduct() {
        return createProduct(UUID.randomUUID(), DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE);
    }

    public static Product generateNewProductWithName(final String name) {
        return createProduct(UUID.randomUUID(), name, DEFAULT_PRODUCT_PRICE);
    }

    public static Product generateProductWithPrice(final BigDecimal price) {
        return createProduct(UUID.randomUUID(), DEFAULT_PRODUCT_NAME, price);
    }

    private static Product createProduct(final UUID id, final String name, final BigDecimal price) {
        Product product = new Product();

        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
