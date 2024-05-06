package kitchenpos.menu.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static final Product 떡볶이 = create(UUID.randomUUID(), "떡볶이", new BigDecimal(2000));

    public static Product create(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
