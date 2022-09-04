package kitchenpos.application.stub;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductStub {

    private static final String DEFAULT_NAME = "후라이드 치킨";
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(15_000);

    public static Product createDefault() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(DEFAULT_NAME);
        product.setPrice(DEFAULT_PRICE);
        return product;
    }

    public static Product create(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
