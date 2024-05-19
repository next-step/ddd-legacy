package kitchenpos.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public class ProductFixture {

    public static Product createProduct() {
        return createProduct("HOT 후라이드 치킨", BigDecimal.valueOf(16_000L));
    }


    public static Product createProduct(String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
