package kitchenpos.builder;

import kitchenpos.model.Product;

import java.math.BigDecimal;

public class ProductBuilder {
    private Long id;
    private String name;
    private BigDecimal price;

    public ProductBuilder() {
    }

    public ProductBuilder id(Long val) {
        id = val;
        return this;
    }

    public ProductBuilder name(String val) {
        name = val;
        return this;
    }

    public ProductBuilder price(BigDecimal val) {
        price = val;
        return this;
    }

    public Product build() {
        final Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setPrice(this.price);

        return product;
    }
}
