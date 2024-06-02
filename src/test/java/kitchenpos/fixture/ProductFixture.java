package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static Product createRequest(final String name) {
        return createRequest(name, 20_000L);
    }

    public static Product createRequest(final long price) {
        return createRequest("후라이드", price);
    }

    public static Product createRequest(final String name, final long price) {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(price));
        product.setName(name);
        return product;
    }

    public static Product changePriceRequest(final Long price) {
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product createFired() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(20_000L));
        product.setName("후라이드");
        return product;
    }

    public static Product createSeasoned() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(BigDecimal.valueOf(25_000L));
        product.setName("양념");
        return product;
    }
}
