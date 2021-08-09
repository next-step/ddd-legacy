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

    public static Product PRODUCT1() {
        final Product product = new Product();
        product.setId(UUID1);
        product.setPrice(BigDecimal.valueOf(PRICE1));
        product.setName(NAME1);
        return product;
    }

    public static Product PRODUCT2() {
        final Product product = new Product();
        product.setId(UUID2);
        product.setPrice(BigDecimal.valueOf(PRICE2));
        product.setName(NAME2);
        return product;
    }

    public static Product CHEAP_PRODUCT() {
        final Product product = new Product();
        product.setId(UUID2);
        product.setPrice(BigDecimal.valueOf(CHEAP_PRICE));
        product.setName(NAME2);
        return product;
    }

    public static Product PRICE_NULL_PRODUCT() {
        final Product product = new Product();
        product.setId(UUID1);
        product.setPrice(null);
        product.setName(NAME1);
        return product;
    }

    public static Product PRICE_NEGATIVE_PRODUCT() {
        final Product product = new Product();
        product.setId(UUID1);
        product.setPrice(BigDecimal.valueOf(NAGATIVE_PRICE));
        product.setName(NAME1);
        return product;
    }

    public static Product PRODUCT_WITH_NAME(final String name) {
        final Product product = new Product();
        product.setId(UUID1);
        product.setPrice(BigDecimal.valueOf(PRICE1));
        product.setName(name);
        return product;
    }

    public static List<Product> PRODUCTS() {
        return Arrays.asList(PRODUCT1(), PRODUCT2());
    }

}
