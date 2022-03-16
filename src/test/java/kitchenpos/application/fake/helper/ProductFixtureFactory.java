package kitchenpos.application.fake.helper;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public final class ProductFixtureFactory {

    public static final String 미트파이_상품_이름 = "미트파이";
    public static final BigDecimal 미트파이_상품_가격 = BigDecimal.valueOf(1000L);

    public static final Product 미트파이_생성_요청 = new Builder()
            .name(미트파이_상품_이름)
            .price(미트파이_상품_가격)
            .build();

    public static final Product 미트파이 = new Builder()
            .id(UUID.randomUUID())
            .name(미트파이_상품_이름)
            .price(미트파이_상품_가격)
            .build();

    public static final class Builder implements FixtureBuilder<Product> {
        private UUID id;
        private String name;
        private BigDecimal price;

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(Long price) {
            return price(BigDecimal.valueOf(price));
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        @Override
        public Product build() {
            Product product = new Product();
            product.setId(this.id);
            product.setName(this.name);
            product.setPrice((this.price));
            return product;
        }

    }


}
