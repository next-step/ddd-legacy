package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    private ProductFixture() {
    }

    public static Product create(UUID id, String name, int price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));

        return product;
    }

    public static Product create(String name) {
        return create(UUID.randomUUID(), name, 15_000);
    }

    public static Product create(int price) {
        return create(UUID.randomUUID(), "testProductName", price);
    }
    public static Product create() {
        return create("testProductName");
    }
}
