package kitchenpos.domain;

import java.math.BigDecimal;

public class ProductFixture {

    public static Product Product(String name, int price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
