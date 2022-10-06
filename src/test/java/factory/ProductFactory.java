package factory;

import kitchenpos.domain.Product;

import java.math.BigDecimal;

public class ProductFactory {

    public static final String DEFAULT_NAME = "황금올리브";
    public static final long DEFAULT_PRICE = 20000L;

    public static Product getDefaultProduct() {
        return ProductFactory.of(DEFAULT_NAME, BigDecimal.valueOf(DEFAULT_PRICE));
    }

    public static Product of(BigDecimal price) {
        return ProductFactory.of(DEFAULT_NAME, price);
    }

    public static Product of(long price) {
        return ProductFactory.of(DEFAULT_NAME, BigDecimal.valueOf(price));
    }

    public static Product of(String name) {
        return ProductFactory.of(name, BigDecimal.valueOf(DEFAULT_PRICE));
    }

    public static Product of(String name, long price) {
        return ProductFactory.of(name, BigDecimal.valueOf(price));
    }

    public static Product of(String name, BigDecimal price) {
        final Product request = new Product();
        request.setName(name);
        request.setPrice(price);
        return request;
    }
}
