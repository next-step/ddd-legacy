package kitchenpos.domain;

import java.math.BigDecimal;

public class ProductTest {
    public static Product create(String name, long price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
