package kitchenpos.fixture.request;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductRequestFixture {
    public static Product createProductRequest() {
        return createProductRequest(3_000L);
    }

    public static Product createProductRequest(final Long price) {
        return createProductRequest(BigDecimal.valueOf(price));
    }

    public static Product createProductRequest(final BigDecimal price) {
        final Product product = new Product();
        product.setName("명란닭가슴살꼬치");
        product.setPrice(price);
        return product;
    }

    public static Product createProductRequest(final String name) {
        final Product product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(3_000L));
        return product;
    }

    public static Product changeProductPriceRequest() {
        return changeProductPriceRequest(10_000L);
    }

    public static Product changeProductPriceRequest(final BigDecimal price) {
        final Product product = new Product();
        product.setPrice(price);
        return product;
    }

    public static Product changeProductPriceRequest(final Long price) {
        return changeProductPriceRequest(BigDecimal.valueOf(price));
    }
}
