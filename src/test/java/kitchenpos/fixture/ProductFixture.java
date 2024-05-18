package kitchenpos.fixture;

import java.math.BigDecimal;
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
}
