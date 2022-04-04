package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product create(final UUID id, final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product createRequest(final int price) {
        return createRequest("족발(중)", price);
    }

    public static Product createRequest(final String name, final int price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product createMetaProduct(final String name, final int price) {
        return create(UUID.randomUUID(), name, BigDecimal.valueOf(price));
    }

}
