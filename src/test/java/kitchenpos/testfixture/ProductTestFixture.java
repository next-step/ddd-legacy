package kitchenpos.testfixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductTestFixture {

    public static Product createProductRequest() {
        return createProductRequest(20000L);
    }

    public static Product createProductRequest(final long price) {
        return createProductRequest("후라이드치킨", price);
    }

    public static Product createProductRequest(final String name, final long price) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }

    public static Product createProduct(UUID id, String name, long price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(price));
        return product;
    }
}
