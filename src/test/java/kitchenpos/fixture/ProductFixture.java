package kitchenpos.fixture;

import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product product(String name, final long price) {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(product, "name", name);
        ReflectionTestUtils.setField(product, "price", BigDecimal.valueOf(price));
        return product;
    }
}
