package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product createProduct() {
        return createProduct("치킨", new BigDecimal(100));
    }

    public static Product createProduct(final UUID id) {
        return createProduct(id, "치킨", new BigDecimal(100));
    }

    public static Product createProduct(final String name, final BigDecimal price) {
        return createProduct(null, name, price);
    }

    public static Product createProduct(final UUID id, final String name, final BigDecimal price) {
        final Product product = new Product();

        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
