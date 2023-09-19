package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixure {

    public static Product create(final String name, final int price) {
        return create(UUID.randomUUID(), name, new BigDecimal(price));
    }

    public static Product create(final UUID id, final String name, final BigDecimal price) {
        final Product product = new Product();

        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }

}
