package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static Product 양념치킨() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("양념치킨");
        product.setPrice(BigDecimal.valueOf(18000L));
        return product;
    }

    public static Product 간장치킨() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("간장치킨");
        product.setPrice(BigDecimal.valueOf(19000L));
        return product;
    }

    public static Product 무료치킨() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("무료치킨");
        product.setPrice(BigDecimal.valueOf(0));
        return product;
    }
}
