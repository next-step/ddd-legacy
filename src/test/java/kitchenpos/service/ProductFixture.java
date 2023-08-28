package kitchenpos.service;

import java.math.BigDecimal;
import java.util.UUID;

import kitchenpos.domain.Product;

public class ProductFixture {
    private final Product product;

    public ProductFixture() {
        product = new Product();
        product.setId(UUID.randomUUID());
    }

    public static ProductFixture builder() {
        return new ProductFixture();
    }

    public ProductFixture name(String name) {
        product.setName(name);
        return this;
    }

    public ProductFixture price(BigDecimal price) {
        product.setPrice(price);
        return this;
    }

    public Product build() {
        return product;
    }

    public static class Data {
        public static Product 강정치킨() {
            return builder()
                    .name("강정치킨")
                    .price(new BigDecimal(18000))
                    .build();
        }

        public static Product 양념치킨() {
            return builder()
                    .name("양념치킨")
                    .price(new BigDecimal(17000))
                    .build();
        }
    }
}
