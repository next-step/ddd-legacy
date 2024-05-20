package fixtures;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductBuilder {

    private UUID id = UUID.randomUUID();
    private String name = "후라이드치킨";
    private BigDecimal price = BigDecimal.ZERO;

    public ProductBuilder anProduct() {
        return this;
    }

    public ProductBuilder with(String name, BigDecimal price) {
        this.name = name;
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
