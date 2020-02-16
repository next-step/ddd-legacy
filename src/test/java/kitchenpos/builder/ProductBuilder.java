package kitchenpos.builder;

import kitchenpos.model.Product;

import java.math.BigDecimal;

public class ProductBuilder {
    private Long id;
    private String name;
    private BigDecimal price;

    private ProductBuilder() {
    }

    public static ProductBuilder product() {
        return new ProductBuilder();
    }

    public ProductBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Product build() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setPrice(this.price);
        return product;
    }
}
