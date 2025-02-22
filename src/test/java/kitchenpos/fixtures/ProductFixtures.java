package kitchenpos.fixtures;

import static java.math.BigDecimal.valueOf;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixtures {

    public static final UUID 후라이드치킨_ID = UUID.randomUUID();
    public static final UUID 양념치킨_ID = UUID.randomUUID();

    public static final String 후라이드치킨_이름 = "후라이드치킨";
    public static final String 양념치킨_이름 = "양념치킨";

    public static final BigDecimal 후라이드치킨_가격 = valueOf(16000);
    public static final BigDecimal 양념치킨_가격 = valueOf(17000);

    public static Product 후라이드치킨() {
        Product product = new Product();
        product.setId(후라이드치킨_ID);
        product.setName(후라이드치킨_이름);
        product.setPrice(후라이드치킨_가격);

        return product;
    }

    public static Product 양념치킨() {
        Product product = new Product();
        product.setId(양념치킨_ID);
        product.setName(양념치킨_이름);
        product.setPrice(양념치킨_가격);

        return product;
    }
    public static Product createProduct(final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    /**
     * OrderServiceTest
     */
    public static Product product() {
        return product("후라이드치킨", 16_000L);
    }

    public static Product product(final String name, final long price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
