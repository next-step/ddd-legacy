package kitchenpos.support;

import kitchenpos.model.Product;

import java.math.BigDecimal;

public class ProductBuilder {
    private long id;
    private String name;
    private BigDecimal price;

    private ProductBuilder() {
    }

    public static ProductBuilder product() {
        return new ProductBuilder();
    }

    public ProductBuilder withId(final long id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withName(final String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withPrice(final BigDecimal price) {
        this.price = price;
        return this;
    }

    public Product build() {
        final Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
