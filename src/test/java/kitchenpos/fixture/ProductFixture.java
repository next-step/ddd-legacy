package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static final String NAME_후라이드치킨 = "후라이드치킨";
    public static final String NAME_양념치킨 = "양념치킨";

    public static final BigDecimal PRICE_18000 = BigDecimal.valueOf(18_000);
    public static final BigDecimal PRICE_20000 = BigDecimal.valueOf(20_000);

    public static Product productCreateRequest(String name, BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product productResponse(String name, BigDecimal price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product productChangePriceRequest(BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);
        return product;
    }
}
