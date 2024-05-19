package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static Product createProduct() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("HOT 후라이드치킨");
        product.setPrice(BigDecimal.valueOf(16_000L));
        return product;
    }

}
