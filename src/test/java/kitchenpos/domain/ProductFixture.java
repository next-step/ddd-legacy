package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {

    public static final Product FRIED_CHICKEN =
        ProductFixture.builder()
                      .id(UUID.randomUUID())
                      .name("후라이드 치킨")
                      .price(BigDecimal.valueOf(18000L))
                      .build();

    public static final Product HONEY_COMBO =
        ProductFixture.builder()
                      .id(UUID.randomUUID())
                      .name("허니콤보")
                      .price(BigDecimal.valueOf(20000L))
                      .build();

    private UUID id;
    private String name;
    private BigDecimal price;

    public static ProductFixture builder() {
        return new ProductFixture();
    }

    public ProductFixture id(UUID id) {
        this.id = id;
        return this;
    }

    public ProductFixture name(String name) {
        this.name = name;
        return this;
    }

    public ProductFixture price(BigDecimal price) {
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
