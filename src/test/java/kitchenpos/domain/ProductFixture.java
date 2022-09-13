package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product Product(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price).setScale(2));
        return product;
    }

    public static Product ProductWithUUID(String name, int price) {
        Product product = Product(name, price);
        product.setId(UUID.randomUUID());
        return product;
    }
}
