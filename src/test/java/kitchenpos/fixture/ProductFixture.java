package kitchenpos.fixture;

import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFixture {
    private static final String DEFAULT_PRODUCT_NAME = "상품명";
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.valueOf(10_000);

    private ProductFixture() {
    }

    public static Product 기본_상품() {
        return 상품_생성(DEFAULT_PRODUCT_NAME, DEFAULT_PRICE);
    }

    public static Product 상품_생성(BigDecimal price) {
        return 상품_생성(DEFAULT_PRODUCT_NAME, price);
    }

    public static Product 상품_생성(String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName(name);
        product.setPrice(price);

        return product;
    }
}