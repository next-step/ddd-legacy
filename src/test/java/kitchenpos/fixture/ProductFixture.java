package kitchenpos.fixture;

import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    private ProductFixture() {
    }

    public static Product 기본_상품() {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("상품명");
        product.setPrice(BigDecimal.valueOf(10_000));

        return product;
    }

    public static Product 상품_생성(BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("상품명");
        product.setPrice(price);

        return product;
    }

    public static Product 상품_생성(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}
