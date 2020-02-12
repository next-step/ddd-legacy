package kitchenpos.mock;

import kitchenpos.model.Product;

import java.math.BigDecimal;

public class ProductBuilder {
    private Long id;
    private String name;
    private BigDecimal price;

    private ProductBuilder() {
    }

    ;

    public static ProductBuilder mock() {
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
        product.setId(id);
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
