package kitchenpos.application.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    public static Product generateProduct() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("간장치킨");
        product.setPrice(BigDecimal.valueOf(12000));
        return product;
    }

    public static Product generateProduct(String name, Long price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
