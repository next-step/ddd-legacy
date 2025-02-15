package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static final String FRIED_CHICKEN = "후라이드 치킨";
    public static final String SEASONED_CHICKEN = "양념 치킨";
    public static final BigDecimal FRIED_CHICKEN_PRICE = BigDecimal.valueOf(16000);
    public static final BigDecimal SEASONED_CHICKEN_PRICE = BigDecimal.valueOf(16000);

    private ProductFixture() {
    }

    public static Product product() {
        return product(createProductId(), FRIED_CHICKEN, FRIED_CHICKEN_PRICE);
    }

    public static Product product(String name, BigDecimal price) {
        return product(createProductId(), name, price);
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
