package kitchenpos.application.fixture;

import kitchenpos.domain.Product;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product createProduct(String name, BigDecimal price) {
        return createProduct(null, name, price);
    }

    public static Product createProduct(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        ReflectionTestUtils.setField(product, "id", id);
        ReflectionTestUtils.setField(product, "name", name);
        ReflectionTestUtils.setField(product, "price", price);
        return product;
    }

    private ProductFixture() {
    }
}
