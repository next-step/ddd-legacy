package kitchenpos.builder;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public final class ProductBuilder {
    private UUID id;
    private String name;
    private BigDecimal price;

    private ProductBuilder() {
        id = UUID.randomUUID();
        name = "후라이드";
        price = BigDecimal.valueOf(16_000L);
    }

    public static ProductBuilder newInstance() {
        return new ProductBuilder();
    }

    public ProductBuilder setId(UUID id) {
        this.id = id;
        return this;
    }

    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductBuilder setPrice(long price) {
        this.price = BigDecimal.valueOf(price);
        return this;
    }

    public Product build() {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
