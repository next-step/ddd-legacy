package kitchenpos.mock.fixture;

import java.math.BigDecimal;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static Product create(final String name, final BigDecimal price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    public static Product create(final BigDecimal price) {
        Product product = new Product();
        product.setPrice(price);
        return product;
    }

}
