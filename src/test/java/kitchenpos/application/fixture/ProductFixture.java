package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    private static final UUID UUID1 = java.util.UUID.randomUUID();
    private static final UUID UUID2 = java.util.UUID.randomUUID();
    private static final long PRICE1 = 10000L;
    private static final long PRICE2 = 20000L;
    private static final long CHEAP_PRICE = 1000L;
    private static final long NAGATIVE_PRICE = -10000L;
    private static final String NAME1 = "상품1";
    private static final String NAME2 = "상품2";

    public static Product PRODUCT1_REQUEST() {
        return createProduct(null, NAME1, PRICE1);
    }

    public static Product PRICE_NULL_PRODUCT_REQUEST() {
        return createProduct(null, NAME1, null);
    }

    public static Product PRICE_NULL_PRODUCT() {
        return createProduct(UUID1, NAME1, null);
    }

    public static Product PRODUCT1() {
        return createProduct(UUID1, NAME1, PRICE1);
    }

    public static Product PRODUCT2() {
        return createProduct(UUID2, NAME2, PRICE2);
    }

    public static Product CHEAP_PRODUCT() {
        return createProduct(UUID2, NAME2, CHEAP_PRICE);
    }

    public static Product CHEAP_PRODUCT_REQUEST() {
        return createProduct(null, NAME2, CHEAP_PRICE);
    }

    public static Product PRICE_NEGATIVE_PRODUCT_REQUEST() {
        return createProduct(null, NAME1, NAGATIVE_PRICE);
    }

    public static Product PRICE_NEGATIVE_PRODUCT() {
        return createProduct(UUID1, NAME1, NAGATIVE_PRICE);
    }

    public static Product PRODUCT_WITH_NAME_REQUEST(final String name) {
        return createProduct(null, name, PRICE1);
    }

    public static List<Product> PRODUCTS() {
        return Arrays.asList(PRODUCT1(), PRODUCT2());
    }

    private static Product createProduct(final UUID id, final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(id);
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    private static Product createProduct(final UUID id, final String name, final long price) {
        return createProduct(id, name, BigDecimal.valueOf(price));
    }

}
