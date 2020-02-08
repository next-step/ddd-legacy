package kitchenpos.model;

import java.math.BigDecimal;

public class Product {
    private Long id; // 제품 id
    private String name; // 제품명
    private BigDecimal price; // 제품 가격

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }
}
