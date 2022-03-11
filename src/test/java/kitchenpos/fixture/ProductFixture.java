package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static class ProductBuilder {
        private UUID id;
        private String name;
        private BigDecimal price;

        public ProductBuilder() {
            this.id = UUID.randomUUID();
        }

        public ProductBuilder name(String name) {
            this.name=name;
            return this;
        }

        public ProductBuilder price(BigDecimal price) {
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
}
