package kitchenpos.application.fixture;

import java.math.BigDecimal;
import java.util.UUID;
import kitchenpos.domain.Product;

public final class ProductFixtures {

    private ProductFixtures() {
        throw new RuntimeException("생성할 수 없는 클래스");
    }

    public static Product createProduct(BigDecimal price) {
        return createProduct("좋은말", price);
    }

    public static Product createProduct(String name, BigDecimal price) {
        return createProduct(UUID.randomUUID(), name, price);
    }

    public static Product createProduct(UUID productId, BigDecimal price) {
        return createProduct(productId, "좋은말", price);
    }

    public static Product 일원짜리_Product(UUID productId) {
        return createProduct(productId, "좋은말", BigDecimal.ONE);
    }

    public static Product createProduct(UUID productId, String name, BigDecimal price) {
        final Product product = new Product();
        product.setId(productId);
        product.setPrice(price);
        product.setName(name);
        return product;
    }

    public static Product 십원_상품(UUID productId) {
        return createProduct(productId, "십원짜리", BigDecimal.TEN);
    }

    public static Product 십원_상품() {
        return createProduct(UUID.randomUUID(), "십원짜리", BigDecimal.TEN);
    }
}
