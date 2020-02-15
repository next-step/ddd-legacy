package kitchenpos.builder;

import kitchenpos.model.Product;

import java.math.BigDecimal;

public class ProductBuilder {
    private Product product;
    private Long id;
    private String name;
    private BigDecimal price;

    private ProductBuilder() {
        this.product = new Product();
    }

    public static ProductBuilder create() {
        return new ProductBuilder();
    }

    public Product build() {
        return this.product;
    }

    public ProductBuilder setId(Long id) {
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
}
