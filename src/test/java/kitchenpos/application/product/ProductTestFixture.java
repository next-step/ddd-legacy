package kitchenpos.application.product;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTestFixture {

    public static Product aProduct(final String name, final Long price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product aProductJustPrice(final Long price) {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("상품:" + Math.random());
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }


}
