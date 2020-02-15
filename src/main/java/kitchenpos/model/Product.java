package kitchenpos.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private BigDecimal price;

    private Product(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.price = builder.price;
    }

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

    public static class Builder {
        private Long id;
        private String name;
        private BigDecimal price;

        public Builder id (Long id){
            this.id = id;
            return this;
        }

        public Builder name (String name){
            this.name = name;
            return this;
        }

        public Builder price (BigDecimal price){
            this.price = price;
            return this;
        }

        public Product build(){
            return new Product(this);
        }

    }
}
